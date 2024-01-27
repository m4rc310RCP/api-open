package br.com.m4rc310.graphql.ws;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type", visible = true, include = JsonTypeInfo.As.EXISTING_PROPERTY)
@JsonSubTypes({
        @JsonSubTypes.Type(value = MInitMessage.class, name = MMessage.GQL_CONNECTION_INIT),
        @JsonSubTypes.Type(value = MTerminateMessage.class, name = MMessage.GQL_CONNECTION_TERMINATE),
        @JsonSubTypes.Type(value = MStopMessage.class, name = MMessage.GQL_STOP),
        @JsonSubTypes.Type(value = MStartMessage.class, name = MMessage.GQL_START),
        @JsonSubTypes.Type(value = MErrorMessage.class, name = MMessage.GQL_CONNECTION_ERROR),
        @JsonSubTypes.Type(value = MErrorMessage.class, name = MMessage.GQL_ERROR),
})
public class MMessage {

	/** The id. */
	@JsonProperty("id") 
    private final String id;
	
	/** The type. */
	@JsonProperty("type")
    private final String type;

    /** The Constant GQL_CONNECTION_INIT. */
    //Client messages
    public static final String GQL_CONNECTION_INIT = "connection_init";
    
    /** The Constant GQL_CONNECTION_TERMINATE. */
    public static final String GQL_CONNECTION_TERMINATE = "connection_terminate";
    
    /** The Constant GQL_START. */
    public static final String GQL_START = "start";
    
    /** The Constant GQL_STOP. */
    public static final String GQL_STOP = "stop";

    /** The Constant GQL_CONNECTION_ACK. */
    //Server messages
    public static final String GQL_CONNECTION_ACK = "connection_ack";
    
    /** The Constant GQL_CONNECTION_ERROR. */
    public static final String GQL_CONNECTION_ERROR = "connection_error";
    
    /** The Constant GQL_CONNECTION_KEEP_ALIVE. */
    public static final String GQL_CONNECTION_KEEP_ALIVE = "ka";
    
    /** The Constant GQL_DATA. */
    public static final String GQL_DATA = "data";
    
    /** The Constant GQL_ERROR. */
    public static final String GQL_ERROR = "error";
    
    /** The Constant GQL_COMPLETE. */
    public static final String GQL_COMPLETE = "complete";
    
    /**
	 * Instantiates a new m message.
	 *
	 * @param id   the id
	 * @param type the type
	 */
    public MMessage(String id, String type) {
    	this.id = id;
    	this.type = type;
    }

}
