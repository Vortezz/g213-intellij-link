package dev.vortezz.keyboarderrors.listener;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManagerListener;
import dev.vortezz.keyboarderrors.KeyboardError;
import org.jetbrains.annotations.NotNull;

public class ProjectListener implements ProjectManagerListener {

    @Override
    public void projectClosing(Project project) {
        KeyboardError.getInstance().activateKeyboardCycle();
    }
}
