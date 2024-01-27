package br.com.m4rc310.graphql.ws;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * The Class MErrorMessage.
 */
public class MErrorMessage extends MPayloadMessage<List<Map<String, ?>>> {

    /**
	 * Instantiates a new m error message.
	 *
	 * @param id     the id
	 * @param errors the errors
	 */
    @JsonCreator
    public MErrorMessage(@JsonProperty("id") String id, @JsonProperty("payload") List<Map<String, ?>> errors) {
        super(id, GQL_ERROR, errors);
    }
}
