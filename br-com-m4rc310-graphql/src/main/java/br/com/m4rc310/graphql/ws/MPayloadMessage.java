package br.com.m4rc310.graphql.ws;

import lombok.Getter;

@Getter
public abstract class MPayloadMessage<T> extends MMessage {
	private final T payload;
    public MPayloadMessage(String id, String type, T payload) {
    	super(id, type);
    	this.payload = payload;
    }
    
}
