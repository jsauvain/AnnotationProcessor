package ch.ergon.processing.examples.mapper;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class Contact {

	private String firstName;
	private String lastName;
	private Adress adress;

}
