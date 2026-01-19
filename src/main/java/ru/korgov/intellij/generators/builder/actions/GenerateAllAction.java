package ru.korgov.intellij.generators.builder.actions;

import com.intellij.codeInsight.generation.actions.GenerateEqualsAction;
import com.intellij.codeInsight.generation.actions.GenerateGetterAction;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.java.generate.GenerateToStringAction;

/**
 * User: Kirill Korgov (kirill@korgov.ru)
 * Date: 19/02/15 14:38
 */
public class GenerateAllAction extends AnAction {
    private final GenerateBuilderAction builderAction = new GenerateBuilderAction();
    private final GenerateGetterAction getterHandler = new GenerateGetterAction();
    private final GenerateEqualsAction equalsHandler = new GenerateEqualsAction();
    private final GenerateToStringAction toStringAction = new GenerateToStringAction();

    @Override
    public void actionPerformed(@NotNull final AnActionEvent e) {
        toStringAction.actionPerformed(e);
        equalsHandler.actionPerformed(e);
        getterHandler.actionPerformed(e);
        builderAction.actionPerformed(e);
    }
}
