package ch.ergon.processing.compiler.mapper;

import ch.ergon.processing.compiler.CustomBuilder;
import ch.ergon.processing.compiler.mapper.GenMapperData.FieldData;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;

import javax.annotation.processing.Filer;
import javax.lang.model.element.Modifier;
import java.util.Set;

public class GenMapperBuilder extends CustomBuilder<GenMapperData> {

	private Set<GenMapperData> data;

	public GenMapperBuilder(Filer filer, Set<GenMapperData> data) {
		super(filer);
		this.data = data;
	}

	@Override
	protected JavaFile generateCode(GenMapperData genMapperData) {
		String mapperName = genMapperData.getFrom().simpleName() + "To" + genMapperData.getTo().simpleName() + "Mapper";
		ClassName mapperClass = ClassName.get(genMapperData.getTo().packageName(), mapperName);
		TypeSpec.Builder typeSpec = TypeSpec
				.classBuilder(mapperClass)
				.addModifiers(Modifier.PUBLIC, Modifier.FINAL);
		MethodSpec.Builder mapperMethod = MethodSpec
				.methodBuilder("mapTo" + genMapperData.getTo().simpleName())
				.addModifiers(Modifier.PUBLIC)
				.addParameter(genMapperData.getFrom(), "from")
				.returns(genMapperData.getTo())
				.addCode("return $T.builder()\n", genMapperData.getTo());
		for (FieldData field : genMapperData.getFields()) {
			if (field.getMapper().isPresent()) {
				ClassName fieldMapperClass = field.getMapper().get();
				String mapperFieldName = transformToAllUpperCase(fieldMapperClass.simpleName());
				typeSpec.addField(FieldSpec
						.builder(fieldMapperClass, mapperFieldName)
						.addModifiers(Modifier.PRIVATE, Modifier.STATIC, Modifier.FINAL)
						.initializer("new $T()", fieldMapperClass)
						.build());
				mapperMethod.addCode("\t\t.$N($N.mapTo$N(from.get$N()))\n", field.getVariableElement().getSimpleName().toString(), mapperFieldName, field.getTypeElement().getSimpleName().toString(), transformToUpperCase(field.getFrom()));
			} else {
				mapperMethod.addCode("\t\t.$N(from.get$N())\n", field.getVariableElement().getSimpleName().toString(), transformToUpperCase(field.getFrom()));
			}
		}
		mapperMethod.addStatement("\t\t.build()");
		typeSpec.addMethod(mapperMethod.build());
		return JavaFile
				.builder(genMapperData.getTo().packageName(), typeSpec.build())
				.build();
	}

	@Override
	protected Iterable<GenMapperData> iterable() {
		return data;
	}

	private String transformToUpperCase(String value) {
		return value.substring(0, 1).toUpperCase() + value.substring(1);
	}

	private String transformToAllUpperCase(String value) {
		StringBuilder finalValue = new StringBuilder(value.substring(0, 1));
		for (char c : value.substring(1).toCharArray()) {
			if (Character.isUpperCase(c)) {
				finalValue.append("_");
			}
			finalValue.append(Character.toUpperCase(c));
		}
		return finalValue.toString();
	}
}
