package br.com.m4rc310.graphql.ws;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * The Class MInitMessage.
 */
public class MInitMessage extends MPayloadMessage<Map<String, Object>> {

	@JsonCreator
    public MInitMessage(@JsonProperty("id") String id, @JsonProperty("type") String type, @JsonProperty("payload") Map<String, Object> payload) {
        super(id, GQL_CONNECTION_INIT, payload);
    }
}
