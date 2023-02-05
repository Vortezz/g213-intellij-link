package dev.vortezz.keyboarderrors;

import com.intellij.openapi.Disposable;
import com.intellij.openapi.diagnostic.Logger;

import javax.usb.UsbControlIrp;
import javax.usb.UsbDevice;
import javax.usb.UsbDeviceDescriptor;
import javax.usb.UsbException;
import javax.usb.UsbHostManager;
import javax.usb.UsbHub;
import javax.usb.UsbInterface;
import javax.xml.bind.DatatypeConverter;
import java.io.UnsupportedEncodingException;
import java.util.List;

public class KeyboardError implements Disposable {

    private static final Logger LOG = Logger.getInstance(KeyboardError.class.getName());

    private static KeyboardError instance;
    private final UsbDevice device;

    private String color = "";

    private KeyboardError() throws UnsupportedEncodingException, UsbException {
        KeyboardError.LOG.info("Initializing KeyboardError plugin");

        KeyboardError.instance = this;

        UsbDevice device = null;
        UsbHub rootHub = null;
        try {
            rootHub = UsbHostManager.getUsbServices().getRootUsbHub();
        } catch (UsbException e) {
            throw new RuntimeException(e);
        }

        device = this.findDevice(rootHub);

        if (device == null) {
            throw new RuntimeException("Device not found");
        }

        this.device = device;

        this.sendColorCommand(device, "26d813");
    }

    public UsbDevice findDevice(UsbHub hub) {
        for (UsbDevice device : (List<UsbDevice>) hub.getAttachedUsbDevices()) {
            UsbDeviceDescriptor desc = device.getUsbDeviceDescriptor();

            if (desc.idVendor() == 0x046d && desc.idProduct() == -0x3CCA) {
                return device;
            }

            if (device.isUsbHub()) {
                device = this.findDevice((UsbHub) device);
                if (device != null) {
                    return device;
                }
            }
        }

        return null;
    }

    @Override
    public void dispose() {

    }

    public static KeyboardError getInstance() {
        return KeyboardError.instance;
    }

    public void setKeyboardColor(String color) {
        try {
            this.sendColorCommand(this.device, color);
        } catch (UsbException e) {
            throw new RuntimeException(e);
        }
    }

    private void sendData(byte bmRequestType, byte bmRequest, short wValue, short wIndex, byte[] data) throws UsbException {
        try {
            UsbInterface iface = this.device.getActiveUsbConfiguration().getUsbInterface((byte) 1);

            if (iface.isClaimed()) {
                iface.release();
            }

            iface.claim(usbInterface -> true);
        } catch (UsbException e) {
            e.printStackTrace();
        }

        UsbControlIrp irp = this.device.createUsbControlIrp(bmRequestType, bmRequest, wValue, wIndex);
        irp.setData(data);
        this.device.syncSubmit(irp);

        try {
            UsbInterface iface = this.device.getActiveUsbConfiguration().getUsbInterface((byte) 1);

            if (iface.isClaimed()) {
                iface.release();
            }
        } catch (UsbException e) {
            e.printStackTrace();
        }
    }

    private void sendColorCommand(UsbDevice device, String colorHex) throws UsbException {
        this.sendColorCommand(device, colorHex, 0);
    }

    private void sendColorCommand(UsbDevice device, String colorHex, int field) throws UsbException {
        if (this.color.equals(colorHex)) {
            return;
        }

        this.color = colorHex;

        String cmd = String.format("11ff0c3a%s01%s0200000000000000000000", String.format("%02x", field), colorHex);
        byte[] data = DatatypeConverter.parseHexBinary(cmd);

        this.sendData((byte) 0x21, (byte) 0x09, (short) 0x0211, (short) 0x0001, data);
    }

    public void activateKeyboardCycle() {
        String cmd = String.format("11ff0c3a0003ffffff0000%s64000000000000", "5000");
        byte[] data = DatatypeConverter.parseHexBinary(cmd);

        try {
            this.sendData((byte) 0x21, (byte) 0x09, (short) 0x0211, (short) 0x0001, data);
        } catch (UsbException e) {
            throw new RuntimeException(e);
        }
    }
}
