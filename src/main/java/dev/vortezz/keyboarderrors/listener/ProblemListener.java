package dev.vortezz.keyboarderrors.listener;

import com.intellij.analysis.problemsView.Problem;
import com.intellij.analysis.problemsView.ProblemsListener;
import dev.vortezz.keyboarderrors.KeyboardError;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class ProblemListener implements ProblemsListener {

    private final List<Problem> problems = new ArrayList<>();

    @Override
    public void problemAppeared(@NotNull Problem problem) {
        this.problems.add(problem);

        this.resolveColor();
    }

    @Override
    public void problemDisappeared(@NotNull Problem problem) {
        this.problems.remove(problem);

        this.resolveColor();
    }

    @Override
    public void problemUpdated(@NotNull Problem problem) {
        this.resolveColor();
    }

    private void resolveColor() {
        int problemsCount = this.problems.size();

        if (problemsCount == 0) {
            KeyboardError.getInstance().setKeyboardColor("26d813");
        } else if (problemsCount < 2) {
            KeyboardError.getInstance().setKeyboardColor("6ce415");
        } else if (problemsCount < 4) {
            KeyboardError.getInstance().setKeyboardColor("b2ef16");
        } else if (problemsCount < 5) {
            KeyboardError.getInstance().setKeyboardColor("f8fb18");
        } else if (problemsCount < 10) {
            KeyboardError.getInstance().setKeyboardColor("f9ce18");
        } else if (problemsCount < 15) {
            KeyboardError.getInstance().setKeyboardColor("f9a018");
        } else if (problemsCount < 20) {
            KeyboardError.getInstance().setKeyboardColor("fa7318");
        } else if (problemsCount < 25) {
            KeyboardError.getInstance().setKeyboardColor("fa4518");
        } else {
            KeyboardError.getInstance().setKeyboardColor("fb1818");
        }
    }
}
