package br.com.m4rc310.graphql.ws;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import io.leangen.graphql.spqr.spring.web.dto.GraphQLRequest;

/**
 * The Class MStartMessage.
 */
public class MStartMessage extends MPayloadMessage<GraphQLRequest> {

    /**
	 * Instantiates a new m start message.
	 *
	 * @param id      the id
	 * @param type    the type
	 * @param payload the payload
	 */
    @JsonCreator
    public MStartMessage(@JsonProperty("id") String id, @JsonProperty("type") String type, @JsonProperty("payload") GraphQLRequest payload) {
        super(id, GQL_START, payload);
    }
}
