package br.com.m4rc310.graphql.handlers;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.scheduling.TaskScheduler;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.ConcurrentWebSocketSessionDecorator;

import br.com.m4rc310.graphql.security.MGraphQLJwtService;
import graphql.GraphQL;
import io.leangen.graphql.spqr.spring.web.apollo.PerConnectionApolloHandler;
import io.leangen.graphql.spqr.spring.web.mvc.websocket.GraphQLWebSocketExecutor;
import jakarta.annotation.PreDestroy;

/** The Constant log. */
public class MPerConnectionProtocolHandler extends PerConnectionApolloHandler {

	/** The graph QL. */
	private final GraphQL graphQL;
	
	/** The executor. */
	private final GraphQLWebSocketExecutor executor;
	
	/** The task scheduler. */
	private final TaskScheduler taskScheduler;
	
	/** The keep alive interval. */
	private final int keepAliveInterval;
	
	/** The send time limit. */
	private final int sendTimeLimit;
	
	/** The send buffer size limit. */
	private final int sendBufferSizeLimit;
	
	/** The handlers. */
	private final Map<WebSocketSession, HandlerProxy> handlers;
	
	/** The jwt service. */
	private final MGraphQLJwtService jwtService;
	
	/**
	 * Instantiates a new m per connection protocol handler.
	 *
	 * @param graphQL             the graph QL
	 * @param executor            the executor
	 * @param taskScheduler       the task scheduler
	 * @param keepAliveInterval   the keep alive interval
	 * @param sendTimeLimit       the send time limit
	 * @param sendBufferSizeLimit the send buffer size limit
	 * @param jwtService          the jwt service
	 */
	public MPerConnectionProtocolHandler(GraphQL graphQL, GraphQLWebSocketExecutor executor,
			TaskScheduler taskScheduler, int keepAliveInterval, int sendTimeLimit, int sendBufferSizeLimit, MGraphQLJwtService jwtService) {
		super(graphQL, executor, taskScheduler, keepAliveInterval, sendTimeLimit, sendBufferSizeLimit);
		this.graphQL = graphQL;
		this.executor = executor;
		this.taskScheduler = taskScheduler;
		this.keepAliveInterval = keepAliveInterval;
		this.sendTimeLimit = sendTimeLimit;
		this.sendBufferSizeLimit = sendBufferSizeLimit;
		this.handlers = new ConcurrentHashMap<>();
		this.jwtService = jwtService;
	}

	/**
	 * After connection established.
	 *
	 * @param session the session
	 * @throws Exception the exception
	 */
	@Override
	public void afterConnectionEstablished(WebSocketSession session) throws Exception {
		MTextWebSocketHandler handler = new MTextWebSocketHandler(graphQL, executor, taskScheduler, keepAliveInterval, jwtService);
		HandlerProxy proxy = new HandlerProxy(handler, decorateSession(session));

		this.handlers.put(session, proxy);
		proxy.afterConnectionEstablished();
	}

//	@Override
//	public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {
//		log.info("--> {}", message.toString());
//		super.handleMessage(session, message);
//	}
	
	/**
 * Handle message.
 *
 * @param session the session
 * @param message the message
 * @throws Exception the exception
 */
@Override
    public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {
        getHandler(session).handleMessage(message);
    }

    /**
	 * Handle transport error.
	 *
	 * @param session   the session
	 * @param exception the exception
	 */
    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) {
        getHandler(session).handleTransportError(exception);
    }

    /**
	 * After connection closed.
	 *
	 * @param session     the session
	 * @param closeStatus the close status
	 */
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) {
        try {
            getHandler(session).afterConnectionClosed(closeStatus);
        }
        finally {
            this.handlers.remove(session);
        }
    }

    /**
	 * Supports partial messages.
	 *
	 * @return true, if successful
	 */
    @Override
    public boolean supportsPartialMessages() {
        return false;
    }
	
    /**
	 * Decorate session.
	 *
	 * @param session the session
	 * @return the web socket session
	 */
    protected WebSocketSession decorateSession(WebSocketSession session) {
        return new ConcurrentWebSocketSessionDecorator(session, sendTimeLimit, sendBufferSizeLimit);
    }

    /**
	 * Gets the handler.
	 *
	 * @param session the session
	 * @return the handler
	 */
    private HandlerProxy getHandler(WebSocketSession session) {
        HandlerProxy handler = this.handlers.get(session);
        if (handler == null) {
            throw new IllegalStateException("WebSocketHandler not found for " + session);
        }
        return handler;
    }
    
    /**
	 * Cancel all.
	 */
    @PreDestroy
    public void cancelAll() {
        this.handlers.forEach((session, handler) -> {
            try {
                session.close(CloseStatus.GOING_AWAY);
            } catch (IOException ignored) {
            }
            handler.cancelAll();
        });
    }

    

	/**
	 * The Class HandlerProxy.
	 */
	private static class HandlerProxy {

		/** The handler. */
		private final MTextWebSocketHandler handler;
		
		/** The session. */
		private final WebSocketSession session;

		/**
		 * Instantiates a new handler proxy.
		 *
		 * @param handler the handler
		 * @param session the session
		 */
		HandlerProxy(MTextWebSocketHandler handler, WebSocketSession session) {
			this.handler = handler;
			this.session = session;
		}

		/**
		 * After connection established.
		 *
		 * @throws Exception the exception
		 */
		void afterConnectionEstablished() throws Exception {
			handler.afterConnectionEstablished(session);
		}

		/**
		 * Handle message.
		 *
		 * @param message the message
		 * @throws Exception the exception
		 */
		void handleMessage(WebSocketMessage<?> message) throws Exception {
			handler.handleMessage(session, message);
		}

		/**
		 * Handle transport error.
		 *
		 * @param exception the exception
		 */
		void handleTransportError(Throwable exception) {
			handler.handleTransportError(session, exception);
		}

		/**
		 * After connection closed.
		 *
		 * @param closeStatus the close status
		 */
		void afterConnectionClosed(CloseStatus closeStatus) {
			handler.afterConnectionClosed(session, closeStatus);
		}

		/**
		 * Cancel all.
		 */
		void cancelAll() {
			handler.cancelAll();
		}
	}

}
