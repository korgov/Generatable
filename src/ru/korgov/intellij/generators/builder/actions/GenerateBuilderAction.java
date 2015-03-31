package ru.korgov.intellij.generators.builder.actions;

import com.intellij.codeInsight.generation.actions.BaseGenerateAction;

/**
 * User: Kirill Korgov (kirill@korgov.ru)
 * Date: 19/02/15 14:38
 */
public class GenerateBuilderAction extends BaseGenerateAction {
  public GenerateBuilderAction() {
    super(new GenerateBuilderActionHandler());
  }
}
