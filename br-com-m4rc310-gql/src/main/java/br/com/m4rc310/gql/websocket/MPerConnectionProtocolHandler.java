package br.com.m4rc310.gql.websocket;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.scheduling.TaskScheduler;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.ConcurrentWebSocketSessionDecorator;

import br.com.m4rc310.gql.jwt.MGraphQLJwtService;
import graphql.GraphQL;
import io.leangen.graphql.spqr.spring.web.apollo.PerConnectionApolloHandler;
import io.leangen.graphql.spqr.spring.web.mvc.websocket.GraphQLWebSocketExecutor;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MPerConnectionProtocolHandler extends PerConnectionApolloHandler {
	private final GraphQL graphQL;
	private final GraphQLWebSocketExecutor executor;
	private final TaskScheduler taskScheduler;
	private final int keepAliveInterval;
	private final int sendTimeLimit;
	private final int sendBufferSizeLimit;
	private final Map<WebSocketSession, HandlerProxy> handlers;
	private final MGraphQLJwtService jwtService;

	public MPerConnectionProtocolHandler(GraphQL graphQL, GraphQLWebSocketExecutor executor,
			TaskScheduler taskScheduler, int keepAliveInterval, int sendTimeLimit, int sendBufferSizeLimit,
			MGraphQLJwtService jwtService) {
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

	@Override
	public void afterConnectionEstablished(WebSocketSession session) throws Exception {
		MTextWebSocketHandler handler = new MTextWebSocketHandler(graphQL, executor, taskScheduler, keepAliveInterval,
				jwtService);
		HandlerProxy proxy = new HandlerProxy(handler, decorateSession(session));

		this.handlers.put(session, proxy);
		proxy.afterConnectionEstablished();
	}

	@Override
	public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {
		getHandler(session).handleMessage(message);
	}

	@Override
	public void handleTransportError(WebSocketSession session, Throwable exception) {
		getHandler(session).handleTransportError(exception);
	}

	@Override
	public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) {
		try {
			getHandler(session).afterConnectionClosed(closeStatus);
		} finally {
			this.handlers.remove(session);
		}
	}

	@Override
	public boolean supportsPartialMessages() {
		return false;
	}

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

	protected WebSocketSession decorateSession(WebSocketSession session) {
		return new ConcurrentWebSocketSessionDecorator(session, sendTimeLimit, sendBufferSizeLimit);
	}

	private HandlerProxy getHandler(WebSocketSession session) {
		HandlerProxy handler = this.handlers.get(session);
		if (handler == null) {
			//throw new IllegalStateException("WebSocketHandler not found for " + session);
			log.error("WebSocketHandler not found for {}", session);
		}
		return handler;
	}

	private static class HandlerProxy {

		private final MTextWebSocketHandler handler;

		private final WebSocketSession session;

		HandlerProxy(MTextWebSocketHandler handler, WebSocketSession session) {
			this.handler = handler;
			this.session = session;
		}

		void afterConnectionEstablished() throws Exception {
			handler.afterConnectionEstablished(session);
		}

		void handleMessage(WebSocketMessage<?> message) throws Exception {
			handler.handleMessage(session, message);
		}

		void handleTransportError(Throwable exception) {
			handler.handleTransportError(session, exception);
		}

		void afterConnectionClosed(CloseStatus closeStatus) {
			handler.afterConnectionClosed(session, closeStatus);
		}

		void cancelAll() {
			handler.cancelAll();
		}
	}

}
