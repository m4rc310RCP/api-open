package br.com.m4rc310.gql.dto.messages;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import br.com.m4rc310.gql.dto.messages.MMessage.MErrorMessage;
import br.com.m4rc310.gql.dto.messages.MMessage.MInitMessage;
import br.com.m4rc310.gql.dto.messages.MMessage.MStartMessage;
import br.com.m4rc310.gql.dto.messages.MMessage.MStopMessage;
import br.com.m4rc310.gql.dto.messages.MMessage.MTerminateMessage;
import graphql.ExecutionResult;
import io.leangen.graphql.spqr.spring.web.dto.GraphQLRequest;
import lombok.Data;
import lombok.Getter;

/**
 * <p>MMessage class.</p>
 *
 * @author marcelo
 * @version $Id: $Id
 */
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


	/** Constant <code>GQL_CONNECTION_INIT="connection_init"</code> */
	public static final String GQL_CONNECTION_INIT = "connection_init";

	/** Constant <code>GQL_CONNECTION_TERMINATE="connection_terminate"</code> */
	public static final String GQL_CONNECTION_TERMINATE = "connection_terminate";

	/** Constant <code>GQL_START="start"</code> */
	public static final String GQL_START = "start";

	/** Constant <code>GQL_STOP="stop"</code> */
	public static final String GQL_STOP = "stop";

	/** Constant <code>GQL_CONNECTION_ACK="connection_ack"</code> */
	public static final String GQL_CONNECTION_ACK = "connection_ack";

	/** Constant <code>GQL_CONNECTION_ERROR="connection_error"</code> */
	public static final String GQL_CONNECTION_ERROR = "connection_error";

	/** Constant <code>GQL_CONNECTION_KEEP_ALIVE="ka"</code> */
	public static final String GQL_CONNECTION_KEEP_ALIVE = "ka";

	/** Constant <code>GQL_DATA="data"</code> */
	public static final String GQL_DATA = "data";

	/** Constant <code>GQL_ERROR="error"</code> */
	public static final String GQL_ERROR = "error";

	/** Constant <code>GQL_COMPLETE="complete"</code> */
	public static final String GQL_COMPLETE = "complete";

	private final String id;

	private final String type;

	/**
	 * <p>Constructor for MMessage.</p>
	 *
	 * @param id a {@link java.lang.String} object
	 * @param type a {@link java.lang.String} object
	 */
	public MMessage(String id, String type) {
		this.id = id;
		this.type = type;
	}
	
	@Getter
	public static abstract class MPayloadMessage<T> extends MMessage {
		private final T payload;
	    public MPayloadMessage(String id, String type, T payload) {
	    	super(id, type);
	    	this.payload = payload;
	    }
	}
	
	public static class MDataMessage extends MPayloadMessage<Map<String, Object>> {

	    @JsonCreator
	    public MDataMessage(@JsonProperty("id") String id, @JsonProperty("payload") ExecutionResult payload) {
	        super(id, GQL_DATA, payload.toSpecification());
	    }
	    
	}
	
	public static class MConnectionErrorMessage extends MPayloadMessage<Map<String, ?>> {
		
		@JsonCreator
		public MConnectionErrorMessage(@JsonProperty("payload") Map<String, ?> error) {
			super(null, GQL_CONNECTION_ERROR, error);
		}

	}

	public static class MErrorMessage extends MPayloadMessage<List<Map<String, ?>>> {
	    @JsonCreator
	    public MErrorMessage(@JsonProperty("id") String id, @JsonProperty("payload") List<Map<String, ?>> errors) {
	        super(id, GQL_ERROR, errors);
	    }
	}
	
	public static class MInitMessage extends MPayloadMessage<Map<String, Object>> {

	    @JsonCreator
	    public MInitMessage(@JsonProperty("id") String id, @JsonProperty("type") String type, @JsonProperty("payload") Map<String, Object> payload) {
	        super(id, GQL_CONNECTION_INIT, payload);
	    }
	}
	
	public static class MTerminateMessage extends MPayloadMessage<Map<String, Object>> {

	    @JsonCreator
	    public MTerminateMessage(@JsonProperty("id") String id, @JsonProperty("type") String type, @JsonProperty("payload") Map<String, Object> payload) {
	        super(id, GQL_CONNECTION_TERMINATE, payload);
	    }
	}

	public static class MStopMessage extends MPayloadMessage<GraphQLRequest> {

		@JsonCreator
	    public MStopMessage(@JsonProperty("id") String id, @JsonProperty("type") String type, @JsonProperty("payload") GraphQLRequest payload) {
	        super(id, GQL_STOP, payload);
	    }
	}
	
	public static class MStartMessage extends MPayloadMessage<GraphQLRequest> {

	    @JsonCreator
	    public MStartMessage(@JsonProperty("id") String id, @JsonProperty("type") String type, @JsonProperty("payload") GraphQLRequest payload) {
	        super(id, GQL_START, payload);
	    }
	}

	 

	

}
