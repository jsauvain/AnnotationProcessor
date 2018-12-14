package ch.ergon.processing.examples.mapper.to;

import ch.ergon.processing.api.annotation.GenMapper;
import ch.ergon.processing.api.annotation.Mapping;
import ch.ergon.processing.examples.mapper.Customer;
import lombok.Builder;
import lombok.Value;

@GenMapper(from = Customer.class)
@Value
@Builder
public class CustomerTO {

	private String phoneNumber;
	@Mapping(from = "contact")
	private ContactTO contactTO;
	private String a;
	private String b;
	private String c;
	private String d;
	private String e;
	private String f;
	private String g;
	private String h;

}
