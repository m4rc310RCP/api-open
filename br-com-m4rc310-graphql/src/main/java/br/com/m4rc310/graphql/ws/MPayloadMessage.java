package br.com.m4rc310.graphql.ws;

import lombok.Getter;

@Getter
public abstract class MPayloadMessage<T> extends MMessage {

	/** The payload. */
	private final T payload;

    /**
	 * Instantiates a new m payload message.
	 *
	 * @param id      the id
	 * @param type    the type
	 * @param payload the payload
	 */
    public MPayloadMessage(String id, String type, T payload) {
    	super(id, type);
    	this.payload = payload;
    }
    
}
