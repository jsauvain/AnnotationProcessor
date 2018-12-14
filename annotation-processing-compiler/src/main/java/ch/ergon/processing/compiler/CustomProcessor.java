package ch.ergon.processing.compiler;

import com.google.common.collect.ImmutableSet;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;
import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public abstract class CustomProcessor extends AbstractProcessor {

	public Class<? extends Annotation> getSupportedAnnotation() {
		return null;
	}

	public abstract boolean processing(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) throws Exception;

	@Override
	public Set<String> getSupportedAnnotationTypes() {
		return ImmutableSet.of(getSupportedAnnotation().getCanonicalName());
	}

	@Override
	public SourceVersion getSupportedSourceVersion() {
		return SourceVersion.latestSupported();
	}

	@Override
	public final boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
		try {
			return processing(annotations, roundEnv);
		} catch (Exception e) {
			throw new RuntimeException("Error while processing annotations", e);
		}
	}

	protected final List<Element> findElements(RoundEnvironment roundEnv, ElementKind kind, Class<? extends Annotation> clazz) {
		return roundEnv.getElementsAnnotatedWith(clazz)
				.stream()
				.filter(element -> element.getKind() == kind)
				.collect(Collectors.toList());
	}

	protected final List<VariableElement> findFieldsIn(TypeElement typeElement, Class<? extends Annotation> clazz) {
		return typeElement.getEnclosedElements()
				.stream()
				.filter(element -> element.getKind() == ElementKind.FIELD)
				.map(element -> (VariableElement) element)
				.filter(variableElement -> variableElement.getAnnotation(clazz) != null)
				.collect(Collectors.toList());
	}

	protected final List<VariableElement> findFieldsIn(Element typeElement) {
		return typeElement.getEnclosedElements()
				.stream()
				.filter(element -> element.getKind() == ElementKind.FIELD)
				.map(element -> (VariableElement) element)
				.collect(Collectors.toList());
	}

	protected final void error(String message, Element element) {
		processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, message, element);
	}

	protected final void error(String message) {
		processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, message);
	}

	protected final TypeElement asTypeElement(TypeMirror typeMirror) {
		Types typeUtils = this.processingEnv.getTypeUtils();
		return (TypeElement) typeUtils.asElement(typeMirror);
	}

}
