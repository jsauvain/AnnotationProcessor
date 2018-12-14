package ch.ergon.processing.examples.mapper;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class Adress {

	private String street;
	private Integer zipCode;
	private String unusedField;
}
