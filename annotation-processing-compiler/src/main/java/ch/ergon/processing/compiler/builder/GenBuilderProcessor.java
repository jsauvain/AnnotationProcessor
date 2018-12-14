package ch.ergon.processing.compiler.builder;

import ch.ergon.processing.api.annotation.GenBuilder;
import ch.ergon.processing.compiler.CustomProcessor;
import ch.ergon.processing.compiler.builder.GenBuilderData.FieldData;
import com.google.auto.service.AutoService;
import com.squareup.javapoet.ClassName;

import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static javax.lang.model.element.ElementKind.CLASS;

@AutoService(Processor.class)
public class GenBuilderProcessor extends CustomProcessor {

	@Override
	public Class<? extends Annotation> getSupportedAnnotation() {
		return GenBuilder.class;
	}

	// requires constructor with parameters in exact order
	@Override
	public boolean processing(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) throws Exception {
		List<GenBuilderData> annotatedClasses = new ArrayList<>();
		for (Element element : findElements(roundEnv, CLASS, getSupportedAnnotation())) {
			ClassName className = ClassName.get((TypeElement) element);
			List<FieldData> fieldData = findFieldsIn(element)
					.stream()
					.map(variableElement -> new FieldData(variableElement, asTypeElement(variableElement.asType())))
					.collect(Collectors.toList());
			annotatedClasses.add(new GenBuilderData(className, fieldData));
		}
		new GenBuilderBuilder(processingEnv.getFiler(), annotatedClasses).generate();
		return false;
	}
}
