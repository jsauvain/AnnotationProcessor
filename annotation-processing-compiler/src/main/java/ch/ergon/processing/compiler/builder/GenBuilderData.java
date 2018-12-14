package ch.ergon.processing.compiler.builder;

import com.squareup.javapoet.ClassName;

import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import java.util.List;

public class GenBuilderData {

	private ClassName className;
	private List<FieldData> fields;

	public GenBuilderData(ClassName className, List<FieldData> fields) {
		this.className = className;
		this.fields = fields;
	}

	public ClassName getClassName() {
		return className;
	}

	public List<FieldData> getFields() {
		return fields;
	}

	public static class FieldData {

		private VariableElement variableElement;
		private TypeElement typeElement;

		public FieldData(VariableElement variableElement, TypeElement typeElement) {
			this.variableElement = variableElement;
			this.typeElement = typeElement;
		}

		public VariableElement getVariableElement() {
			return variableElement;
		}

		public TypeElement getTypeElement() {
			return typeElement;
		}
	}
}
