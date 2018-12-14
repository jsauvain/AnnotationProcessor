package ch.ergon.processing.compiler.mapper;

import com.squareup.javapoet.ClassName;

import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class GenMapperData {

	private ClassName from;
	private ClassName to;
	private List<FieldData> fields;

	public GenMapperData(ClassName from, ClassName to, List<FieldData> fields) {
		this.from = from;
		this.to = to;
		this.fields = fields;
	}

	public ClassName getFrom() {
		return from;
	}

	public ClassName getTo() {
		return to;
	}

	public List<FieldData> getFields() {
		return fields;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		GenMapperData that = (GenMapperData) o;
		return Objects.equals(from, that.from) &&
				Objects.equals(to, that.to);
	}

	public static class FieldData {

		private VariableElement variableElement;
		private TypeElement typeElement;
		private String from;
		private ClassName mapper;

		public FieldData(VariableElement variableElement, TypeElement typeElement, String from, ClassName mapper) {
			this.variableElement = variableElement;
			this.typeElement = typeElement;
			this.from = from;
			this.mapper = mapper;
		}

		public VariableElement getVariableElement() {
			return variableElement;
		}

		public TypeElement getTypeElement() {
			return typeElement;
		}

		public String getFrom() {
			return from;
		}

		public Optional<ClassName> getMapper() {
			return Optional.ofNullable(mapper);
		}
	}
}
