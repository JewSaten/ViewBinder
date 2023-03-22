package com.jew.tech.bindview.compiler;

import com.jew.tech.bindview.annotations.BindView;
import com.jew.tech.bindview.annotations.OnClick;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import java.util.ArrayList;
import java.util.List;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;

public class ClassFile {
    private TypeElement annotationClazz;
    private Elements elementsUtils;
    private List<VariableElement> bindViewFields;
    private List<ExecutableElement> onClickMethods;

    public ClassFile(TypeElement annotationClazz, Elements elementsUtils) {
        this.annotationClazz = annotationClazz;
        this.elementsUtils = elementsUtils;
        this.bindViewFields = new ArrayList<>();
        this.onClickMethods = new ArrayList<>();
    }

    public void addField(VariableElement e) {
        this.bindViewFields.add(e);
    }

    public void addMethod(ExecutableElement e) {
        this.onClickMethods.add(e);
    }

    public JavaFile generateFile() {
        MethodSpec.Builder injectMethod = MethodSpec.methodBuilder("inject").addModifiers(Modifier.PUBLIC)
                .addAnnotation(Override.class)
                .returns(TypeName.VOID).addParameter(TypeName.get(annotationClazz.asType()), "target");

        for (VariableElement variableElement : bindViewFields) {
            String variableName = variableElement.getSimpleName().toString();
            int id = variableElement.getAnnotation(BindView.class).value();
            TypeMirror typeMirror = variableElement.asType();
//            injectMethod.addStatement("target."+ variableName+"=("+typeMirror+")target.findViewById("+id+")",
            injectMethod.addStatement("target.$N = ($T) target.findViewById($L)",
                    variableName, typeMirror, id);
        }
        if (onClickMethods.size() > 0) {
            injectMethod.addStatement("$T listener", ClassName.get("android.view.View", "OnClickListener"));
        }

        for (ExecutableElement executableElement : onClickMethods) {

            TypeSpec onClickListener = TypeSpec.anonymousClassBuilder("")
                    .addSuperinterface(ClassName.get("android.view.View", "OnClickListener"))
                    .addMethod(MethodSpec.methodBuilder("onClick")
                            .addAnnotation(Override.class)
                            .addModifiers(Modifier.PUBLIC)
                            .returns(TypeName.VOID)
                            .addParameter(ClassName.get("android.view", "View"), "v")
                            .addStatement("target.$N()", executableElement.getSimpleName())
                            .build()).build();

            injectMethod.addStatement("listener = $L", onClickListener);

            int[] ids = executableElement.getAnnotation(OnClick.class).value();
            for (int id : ids) {
                injectMethod.addStatement("target.findViewById($L).setOnClickListener(listener)", id);
            }
        }

        TypeSpec typeClass = TypeSpec.classBuilder(annotationClazz.getSimpleName().toString() + "_ViewBinding")
//                .superclass(ClassName.bestGuess())
                .addSuperinterface(ParameterizedTypeName.get(ClassName.get("com.jew.tech.bindview", "IBindView"), TypeName.get(annotationClazz.asType())))
                .addModifiers(Modifier.PUBLIC).addMethod(injectMethod.build()).build();

        return JavaFile.builder(elementsUtils.getPackageOf(annotationClazz).toString(), typeClass).build();
    }

}
