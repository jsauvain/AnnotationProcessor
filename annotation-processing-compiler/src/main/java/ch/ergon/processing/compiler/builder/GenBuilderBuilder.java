package ch.ergon.processing.compiler.builder;

import ch.ergon.processing.compiler.CustomBuilder;
import ch.ergon.processing.compiler.builder.GenBuilderData.FieldData;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;

import javax.annotation.processing.Filer;
import javax.lang.model.element.Modifier;
import java.util.ArrayList;
import java.util.List;

public class GenBuilderBuilder extends CustomBuilder<GenBuilderData> {

	private List<GenBuilderData> genBuilderData;

	public GenBuilderBuilder(Filer filer, List<GenBuilderData> genBuilderData) {
		super(filer);
		this.genBuilderData = genBuilderData;
	}


	@Override
	protected JavaFile generateCode(GenBuilderData genBuilderData) {
		String builderName = genBuilderData.getClassName().simpleName() + "Builder";
		ClassName builderClass = ClassName.get(genBuilderData.getClassName().packageName(), builderName);
		TypeSpec.Builder typeSpecBuilder = TypeSpec.classBuilder(builderName)
				.addModifiers(Modifier.PUBLIC, Modifier.FINAL)
				.addMethod(MethodSpec.methodBuilder("builder")
						.addModifiers(Modifier.PUBLIC, Modifier.STATIC)
						.addStatement("return new $N()", builderName)
						.returns(builderClass)
						.build());
		List<MethodSpec> methods = new ArrayList<>();
		for (FieldData field : genBuilderData.getFields()) {
			String fieldName = field.getVariableElement().getSimpleName().toString();
			ClassName fieldClassName = ClassName.get(field.getTypeElement());
			typeSpecBuilder.addField(fieldClassName, fieldName, Modifier.PRIVATE);
			MethodSpec methodSpec = MethodSpec
					.methodBuilder(fieldName)
					.addModifiers(Modifier.PUBLIC)
					.addParameter(fieldClassName, fieldName)
					.addStatement("this.$N = $N", fieldName, fieldName)
					.addStatement("return this")
					.returns(builderClass)
					.build();
			methods.add(methodSpec);
		}
		typeSpecBuilder.addMethods(methods);
		Object[] args = getArgArray(genBuilderData.getClassName(), methods);
		String constructor = getConstructorMethod(methods);
		typeSpecBuilder.addMethod(MethodSpec.methodBuilder("build")
				.addModifiers(Modifier.PUBLIC)
				.addStatement(constructor, args)
				.returns(genBuilderData.getClassName()).build());
		return JavaFile
				.builder(genBuilderData.getClassName().packageName(), typeSpecBuilder.build())
				.build();
	}

	@Override
	protected Iterable<GenBuilderData> iterable() {
		return genBuilderData;
	}

	private Object[] getArgArray(ClassName className, List<MethodSpec> methods) {
		Object[] argArray = new Object[methods.size() + 1];
		argArray[0] = className;
		for (int i = 0; i < methods.size(); i++) {
			argArray[i + 1] = methods.get(i);
		}
		return argArray;
	}

	private String getConstructorMethod(List<MethodSpec> methods) {
		StringBuilder constructor = new StringBuilder("return new $T(");
		for (int i = 0; i < methods.size(); i++) {
			constructor.append("$N, ");
		}
		return constructor.substring(0, constructor.length() - 2) + ")";
	}
}
