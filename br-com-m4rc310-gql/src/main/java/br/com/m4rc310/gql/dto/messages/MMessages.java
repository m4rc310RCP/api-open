package br.com.m4rc310.gql.dto.messages;

import static io.leangen.graphql.spqr.spring.web.apollo.ApolloMessage.GQL_COMPLETE;
import static io.leangen.graphql.spqr.spring.web.apollo.ApolloMessage.GQL_CONNECTION_ACK;
import static io.leangen.graphql.spqr.spring.web.apollo.ApolloMessage.GQL_CONNECTION_KEEP_ALIVE;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.web.socket.TextMessage;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.m4rc310.gql.dto.messages.MMessage.MConnectionErrorMessage;
import br.com.m4rc310.gql.dto.messages.MMessage.MDataMessage;
import br.com.m4rc310.gql.dto.messages.MMessage.MErrorMessage;
import graphql.ErrorType;
import graphql.ExecutionResult;
import graphql.GraphQLError;

/**
 * <p>MMessages class.</p>
 *
 * @author marcelo
 * @version $Id: $Id
 */
public class MMessages {

	/** The Constant CONNECTION_ACK. */
	private static final MMessage CONNECTION_ACK = new MMessage(null, GQL_CONNECTION_ACK);
	
	/** The Constant KEEP_ALIVE. */
	private static final MMessage KEEP_ALIVE     = new MMessage(null, GQL_CONNECTION_KEEP_ALIVE);

	/** The Constant mapper. */
	private static final ObjectMapper mapper = new ObjectMapper()
			.setSerializationInclusion(JsonInclude.Include.NON_NULL);
	
	/**
	 * From.
	 *
	 * @param message the message
	 * @return the m message
	 * @throws java.io.IOException Signals that an I/O exception has occurred.
	 */
	public static MMessage from(TextMessage message) throws IOException{
		return mapper.readValue(message.getPayload(), MMessage.class);
	}

    /**
     * Connection ack.
     *
     * @return the text message
     * @throws com.fasterxml.jackson.core.JsonProcessingException the json processing exception
     */
    public static TextMessage connectionAck() throws JsonProcessingException {
        return jsonMessage(CONNECTION_ACK);
    }

    /**
     * Keep alive.
     *
     * @return the text message
     * @throws com.fasterxml.jackson.core.JsonProcessingException the json processing exception
     */
    public static TextMessage keepAlive() throws JsonProcessingException {
        return jsonMessage(KEEP_ALIVE);
    }
    
    /**
     * Connection error.
     *
     * @param message the message
     * @return the text message
     * @throws com.fasterxml.jackson.core.JsonProcessingException the json processing exception
     */
    public static TextMessage connectionError(final String message) throws JsonProcessingException {
        return jsonMessage(new MConnectionErrorMessage(Collections.singletonMap("message", message)));
    }
    
    /**
     * Connection error.
     *
     * @return the text message
     * @throws com.fasterxml.jackson.core.JsonProcessingException the json processing exception
     */
    public static TextMessage connectionError() throws JsonProcessingException {
        return connectionError("Invalid message");
    }

    
    /**
     * Data.
     *
     * @param id     the id
     * @param result the result
     * @return the text message
     * @throws com.fasterxml.jackson.core.JsonProcessingException the json processing exception
     */
    public static TextMessage data(String id, ExecutionResult result) throws JsonProcessingException {
        return jsonMessage(new MDataMessage(id, result));
    }

    /**
     * Complete.
     *
     * @param id the id
     * @return the text message
     * @throws com.fasterxml.jackson.core.JsonProcessingException the json processing exception
     */
    public static TextMessage complete(String id) throws JsonProcessingException {
        return jsonMessage(new MMessage(id, GQL_COMPLETE));
    }

    /**
     * Error.
     *
     * @param id     the id
     * @param errors the errors
     * @return the text message
     * @throws com.fasterxml.jackson.core.JsonProcessingException the json processing exception
     */
    public static TextMessage error(String id, List<GraphQLError> errors) throws JsonProcessingException {
        return jsonMessage(new MErrorMessage(id, errors.stream()
                .filter(error -> !error.getErrorType().equals(ErrorType.DataFetchingException))
                .map(GraphQLError::toSpecification)
                .collect(Collectors.toList())));
    }

    /**
     * Error.
     *
     * @param id        the id
     * @param exception the exception
     * @return the text message
     * @throws com.fasterxml.jackson.core.JsonProcessingException the json processing exception
     */
    public static TextMessage error(String id, Throwable exception) throws JsonProcessingException {
        return error(id, exception.getMessage());
    }

    /**
     * Error.
     *
     * @param id      the id
     * @param message the message
     * @return the text message
     * @throws com.fasterxml.jackson.core.JsonProcessingException the json processing exception
     */
    public static TextMessage error(String id, String message) throws JsonProcessingException {
        return jsonMessage(new MErrorMessage(id, Collections.singletonList(Collections.singletonMap("message", message))));
    }

    /**
	 * Json message.
	 *
	 * @param message the message
	 * @return the text message
	 * @throws JsonProcessingException the json processing exception
	 */
    private static TextMessage jsonMessage(MMessage message) throws JsonProcessingException {
        return new TextMessage(mapper.writeValueAsString(message));
    }
    


}
