package ch.ergon.processing.compiler.mapper;

import ch.ergon.processing.api.annotation.GenMapper;
import ch.ergon.processing.api.annotation.Mapping;
import ch.ergon.processing.compiler.CustomProcessor;
import ch.ergon.processing.compiler.mapper.GenMapperData.FieldData;
import com.google.auto.service.AutoService;
import com.google.common.collect.ImmutableSet;
import com.squareup.javapoet.ClassName;

import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;
import java.lang.annotation.Annotation;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static javax.lang.model.element.ElementKind.CLASS;

@AutoService(Processor.class)
public class GenMapperProcessor extends CustomProcessor {

	@Override
	public Set<String> getSupportedAnnotationTypes() {
		return ImmutableSet.of(GenMapper.class.getCanonicalName(), Mapping.class.getCanonicalName());
	}

	@Override
	public boolean processing(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) throws Exception {
		Set<GenMapperData> genMapperData = new HashSet<>();
		for (Element element : findElements(roundEnv, CLASS, GenMapper.class)) {
			TypeElement fromClass = getAnnotationValue(element, "from", GenMapper.class);
			if (fromClass != null) {
				List<FieldData> fields = findFieldsIn(element)
						.stream()
						.map(variableElement -> new FieldData(variableElement, asTypeElement(variableElement.asType()), getMappingFromName(variableElement), findFieldMapper(variableElement).orElse(null)))
						.collect(Collectors.toList());
				genMapperData.add(new GenMapperData(ClassName.get(fromClass), ClassName.get((TypeElement) element), fields));
			}
		}
		new GenMapperBuilder(processingEnv.getFiler(), genMapperData).generate();
		return false;
	}


	private static AnnotationMirror getAnnotationMirror(Element typeElement, Class<?> clazz) {
		String clazzName = clazz.getName();
		for (AnnotationMirror m : typeElement.getAnnotationMirrors()) {
			if (m.getAnnotationType().toString().equals(clazzName)) {
				return m;
			}
		}
		return null;
	}

	private static AnnotationValue getAnnotationValue(AnnotationMirror annotationMirror, String key) {
		for (Map.Entry<? extends ExecutableElement, ? extends AnnotationValue> entry : annotationMirror.getElementValues().entrySet()) {
			if (entry.getKey().getSimpleName().toString().equals(key)) {
				return entry.getValue();
			}
		}
		return null;
	}


	private TypeElement getAnnotationValue(Element element, String key, Class<? extends Annotation> clazz) {
		AnnotationMirror am = getAnnotationMirror(element, clazz);
		if (am == null) {
			return null;
		}
		AnnotationValue av = getAnnotationValue(am, key);
		if (av == null) {
			return null;
		} else {
			return asTypeElement((TypeMirror) av.getValue());
		}
	}

	private Optional<ClassName> findFieldMapper(VariableElement field) {
		TypeElement to = asTypeElement(field.asType());
		TypeElement from = getAnnotationValue(to, "from", GenMapper.class);
		if (from == null)
			return Optional.empty();
		return Optional.of(ClassName.get(ClassName.get(to).packageName(), from.getSimpleName() + "To" + to.getSimpleName() + "Mapper"));
	}

	private String getMappingFromName(VariableElement variableElement) {
		Mapping mapping = variableElement.getAnnotation(Mapping.class);
		if (mapping != null)
			return mapping.from();
		return variableElement.getSimpleName().toString();
	}

}
