package ru.korgov.intellij.generators.builder.actions;

import com.intellij.codeInsight.generation.ClassMember;
import com.intellij.codeInsight.generation.GenerateConstructorHandler;
import com.intellij.codeInsight.generation.GenerationInfo;
import com.intellij.codeInsight.generation.PsiFieldMember;
import com.intellij.codeInsight.generation.PsiGenerationInfo;
import com.intellij.ide.util.MemberChooser;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElementFactory;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiModifier;
import com.intellij.psi.PsiModifierList;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * User: Kirill Korgov (kirill@korgov.ru)
 * Date: 19/02/15 14:39
 */
public class GenerateBuilderActionHandler extends GenerateConstructorHandler {

//  private static final GenerationInfo[] EMPTY_ARR = new GenerationInfo[0];
  private static final String BUILDER_CLASS_NAME = "Builder";

  @Override
  protected MemberChooser<ClassMember> createMembersChooser(final ClassMember[] members,
                                                            final boolean allowEmptySelection,
                                                            final boolean copyJavadocCheckbox,
                                                            final Project project) {
    final MemberChooser<ClassMember> chooser = super.createMembersChooser(members, allowEmptySelection, copyJavadocCheckbox, project);
    chooser.setTitle("Generate Builder");
    return chooser;
  }

  @NotNull
  @Override
  protected List<? extends GenerationInfo> generateMemberPrototypes(final PsiClass aClass,
                                                                    final ClassMember[] members) throws IncorrectOperationException {
    super.generateMemberPrototypes(aClass, members);
    final List<GenerationInfo> out = new ArrayList<GenerationInfo>();

    final PsiField[] fields = unwrapFields(members);
    appendConstructorIfNeed(aClass, out, fields);
    appendInitBuilderMethodIfNeed(aClass, out);
    appendInnerBuilderIfNeed(aClass, out, fields);

    return out;
  }

  private void appendInitBuilderMethodIfNeed(final PsiClass clazz, final List<GenerationInfo> out) {
    final Project project = clazz.getProject();
    final PsiElementFactory elementFactory = JavaPsiFacade.getElementFactory(project);
    final PsiMethod methodFromText = elementFactory.createMethodFromText(getBuilderMethodText(), clazz);
    final PsiMethod[] existsMethods = clazz.findMethodsByName(methodFromText.getName(), false);
    if(existsMethods.length == 0){
      out.add(new PsiGenerationInfo<PsiMethod>(methodFromText));
    }
  }

  private String getBuilderMethodText() {
    return "" +
      "public static " + BUILDER_CLASS_NAME + " builder() {" +
      "    return new " + BUILDER_CLASS_NAME + "();" +
      "}";
  }

  private void appendInnerBuilderIfNeed(final PsiClass clazz, final List<GenerationInfo> out, final PsiField[] fields) {
    final Project project = clazz.getProject();
    final PsiElementFactory elementFactory = JavaPsiFacade.getElementFactory(project);
    final String builderClassText = buildClassBuilderText(clazz, fields);
    if (builderClassText != null && !builderClassText.isEmpty()) {
      final PsiClass classFromText = elementFactory.createClassFromText(builderClassText, clazz);
      classFromText.setName(BUILDER_CLASS_NAME);
      makePublicStatic(classFromText);
//      System.out.println("compiled: " + classFromText.getText());
      final PsiClass existsInnerClass = clazz.findInnerClassByName(classFromText.getName(), false);
      if (existsInnerClass == null) {
        out.add(new PsiGenerationInfo<PsiClass>(classFromText));
      }
    }
  }

  private void makePublicStatic(final PsiClass classFromText) {
    final PsiModifierList modifierList = classFromText.getModifierList();
    if (modifierList != null) {
      modifierList.setModifierProperty(PsiModifier.PUBLIC, true);
      modifierList.setModifierProperty(PsiModifier.STATIC, true);
    }
  }

  private String buildClassBuilderText(final PsiClass clazz, final PsiField[] fields) {
    final StringBuilder sb = new StringBuilder(256);
    for (final PsiField field : fields) {
      sb.append("private ").append(field.getType().getCanonicalText())
        .append(" ").append(field.getName()).append(";\n");
    }

    for (final PsiField field : fields) {
      sb.append("public " + BUILDER_CLASS_NAME + " set").append(StringUtil.capitalize(field.getName()))
        .append("(final ")
        .append(field.getType().getCanonicalText())
        .append(" ")
        .append(field.getName())
        .append(") {\n")
        .append(" this.").append(field.getName())
        .append(" = ").append(field.getName()).append(";").append("\n")
        .append("return this;\n")
        .append("}").append("\n");
    }
    sb.append("public ").append(clazz.getName()).append(" build(){\n")
      .append("return new ").append(clazz.getName())
      .append("(");
    for (int i = 0; i < fields.length; i++) {
      sb.append("this.").append(fields[i].getName()).append(i == fields.length - 1 ? "" : ",");
    }
    sb.append(");\n");
    sb.append("}");

    return sb.toString();
  }

  private void appendConstructorIfNeed(final PsiClass aClass, final List<GenerationInfo> out, final PsiField[] fields) {
    final PsiMethod constructor = generateConstructorPrototype(aClass, null, false, fields);
    if (!methodExists(aClass, constructor)) {
      out.add(new PsiGenerationInfo<PsiMethod>(constructor));
    }
  }

  private static boolean methodExists(final PsiClass aClass, final PsiMethod template) {
    final PsiMethod existing = aClass.findMethodBySignature(template, false);
    return existing != null;
  }

//  private PsiField[] withoutStatic(final PsiField[] fields) {
//    final List<PsiField> out = new ArrayList<PsiField>(fields.length);
//    for (final PsiField field : fields) {
//      final PsiModifierList modifierList = field.getModifierList();
//      if (modifierList == null || !modifierList.hasModifierProperty("static")) {
//        out.add(field);
//      }
//    }
//    return out.toArray(new PsiField[out.size()]);
//  }
//
//  private ClassMember[] wrap(final PsiField[] fields) {
//    final List<ClassMember> out = new ArrayList<ClassMember>(fields.length);
//    for (final PsiField field : fields) {
//      out.add(new PsiFieldMember(field));
//    }
//    return out.toArray(new ClassMember[out.size()]);
//  }

  private PsiField[] unwrapFields(final ClassMember[] members) {
    final List<PsiField> out = new ArrayList<PsiField>(members.length);
    for (final ClassMember field : members) {
      if (field instanceof PsiFieldMember) {
        final PsiFieldMember psiFieldMember = (PsiFieldMember) field;
        out.add(psiFieldMember.getElement());
      }
    }
    return out.toArray(new PsiField[out.size()]);
  }
}
