package br.com.m4rc310.graphql.ws;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import graphql.ExecutionResult;

/**
 * The Class MDataMessage.
 */
public class MDataMessage extends MPayloadMessage<Map<String, Object>> {

    /**
	 * Instantiates a new m data message.
	 *
	 * @param id      the id
	 * @param payload the payload
	 */
    @JsonCreator
    public MDataMessage(@JsonProperty("id") String id, @JsonProperty("payload") ExecutionResult payload) {
        super(id, GQL_DATA, payload.toSpecification());
    }
    
}
