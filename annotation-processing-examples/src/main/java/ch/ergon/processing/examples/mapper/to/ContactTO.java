package ch.ergon.processing.examples.mapper.to;

import ch.ergon.processing.api.annotation.GenMapper;
import ch.ergon.processing.examples.mapper.Contact;
import lombok.Builder;

@GenMapper(from = Contact.class)
@Builder
public class ContactTO {

	private String firstName;
	private String lastName;
	private AdressTO adress;

}
