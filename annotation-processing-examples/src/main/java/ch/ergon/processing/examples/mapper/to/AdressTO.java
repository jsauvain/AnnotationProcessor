package ch.ergon.processing.examples.mapper.to;

import ch.ergon.processing.api.annotation.GenMapper;
import ch.ergon.processing.api.annotation.Mapping;
import ch.ergon.processing.examples.mapper.Adress;
import lombok.Builder;
import lombok.Value;

@GenMapper(from = Adress.class)
@Builder
@Value
public class AdressTO {

	private String street;
	@Mapping(from = "zipCode")
	private Integer zip;

}
