package br.com.m4rc310.graphql.ws;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * The Class MConnectionErrorMessage.
 */
public class MConnectionErrorMessage extends MPayloadMessage<Map<String, ?>> {
	
	/**
	 * Instantiates a new m connection error message.
	 *
	 * @param error the error
	 */
	@JsonCreator
	public MConnectionErrorMessage(@JsonProperty("payload") Map<String, ?> error) {
		super(null, GQL_CONNECTION_ERROR, error);
	}

}
