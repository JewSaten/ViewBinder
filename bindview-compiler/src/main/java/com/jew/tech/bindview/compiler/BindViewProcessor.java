package com.jew.tech.bindview.compiler;

import com.google.auto.service.AutoService;
import com.jew.tech.bindview.annotations.BindView;
import com.jew.tech.bindview.annotations.OnClick;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.util.Elements;
import javax.tools.Diagnostic;

@AutoService(Processor.class)
public class BindViewProcessor extends AbstractProcessor {
    private Filer mFiler;
    private Messager mMessenger;
    private Elements mElementsUtils;
    private Map<String, ClassFile> mClazzMap = new HashMap<>();

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        mFiler = processingEnv.getFiler();
        mMessenger = processingEnv.getMessager();
        mElementsUtils = processingEnv.getElementUtils();
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> types = new LinkedHashSet<>();
        types.add(BindView.class.getCanonicalName());
        types.add(OnClick.class.getCanonicalName());
        return types;
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        mMessenger.printMessage(Diagnostic.Kind.NOTE, "====process====");
        mClazzMap.clear();

        try {
            processBindViews(roundEnv);
            processOnClicks(roundEnv);
        } catch (Exception e) {
            mMessenger.printMessage(Diagnostic.Kind.ERROR, "process element error: "+e.getMessage());
            return true;
        }

        for (ClassFile file : mClazzMap.values()) {
            try {
                file.generateFile().writeTo(mFiler);
            } catch (IOException e) {
                mMessenger.printMessage(Diagnostic.Kind.ERROR, "generateFile error: "+e.getMessage());
                return true;
            }
        }
        return true;
    }

    private void processBindViews(RoundEnvironment roundEnv) {
        for (Element element : roundEnv.getElementsAnnotatedWith(BindView.class)) {
            ClassFile classFile = getClassFile(element);
            classFile.addField((VariableElement) element);
        }
    }

    private void processOnClicks(RoundEnvironment roundEnv) {
        for (Element element : roundEnv.getElementsAnnotatedWith(OnClick.class)) {
            ClassFile classFile = getClassFile(element);
            classFile.addMethod((ExecutableElement) element);
        }
    }

    private ClassFile getClassFile(Element element) {
        TypeElement typeElement = (TypeElement) element.getEnclosingElement();
        String className = typeElement.getSimpleName().toString();
        ClassFile classFile = mClazzMap.get(className);
        if (classFile == null) {
            classFile = new ClassFile(typeElement, mElementsUtils);
            mClazzMap.put(className, classFile);
        }
        return classFile;
    }

}
