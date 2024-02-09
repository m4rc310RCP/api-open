package br.com.m4rc310.gql;

import java.util.Optional;
import java.util.concurrent.ThreadPoolExecutor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;

import br.com.m4rc310.gql.jwt.MGraphQLJwtService;
import br.com.m4rc310.gql.websocket.MPerConnectionProtocolHandler;
import graphql.GraphQL;
import graphql.schema.GraphQLSchema;
import io.leangen.graphql.spqr.spring.autoconfigure.DataLoaderRegistryFactory;
import io.leangen.graphql.spqr.spring.autoconfigure.SpqrProperties;
import io.leangen.graphql.spqr.spring.autoconfigure.WebSocketAutoConfiguration;
import io.leangen.graphql.spqr.spring.web.apollo.PerConnectionApolloHandler;
import io.leangen.graphql.spqr.spring.web.mvc.websocket.GraphQLWebSocketExecutor;
import lombok.extern.slf4j.Slf4j;

/**
 * <p>MGraphQLWsAutoConfiguration class.</p>
 *
 * @author marcelo
 * @version $Id: $Id
 */
@Slf4j
@AutoConfiguration
@EnableWebSocket
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
@ConditionalOnClass(WebSocketConfigurer.class)
@ConditionalOnProperty(name = "graphql.spqr.ws.enabled", havingValue = "false", matchIfMissing = true)
//@ConditionalOnBean(GraphQLSchema.class)
public class MGraphQLWsAutoConfiguration extends WebSocketAutoConfiguration  {

	private GraphQL graphQL;
	private SpqrProperties config;
	
	@Autowired
	private MGraphQLJwtService jwtService;

	/**
	 * <p>Constructor for MGraphQLWsAutoConfiguration.</p>
	 *
	 * @param graphQL a {@link graphql.GraphQL} object
	 * @param config a {@link io.leangen.graphql.spqr.spring.autoconfigure.SpqrProperties} object
	 * @param dataLoaderRegistryFactory a {@link java.util.Optional} object
	 */
	public MGraphQLWsAutoConfiguration(GraphQL graphQL, SpqrProperties config,
			Optional<DataLoaderRegistryFactory> dataLoaderRegistryFactory) {
		super(graphQL, config, dataLoaderRegistryFactory);
		
		this.graphQL = graphQL;
		this.config = config;
	}

	@Bean("br.com.m4rc310.gql.ws.enable")
	void init() {
		log.info("~> Loading {}...", getClass().getName());
	}
	
	/** {@inheritDoc} */
	@Override
	public PerConnectionApolloHandler webSocketHandler(GraphQLWebSocketExecutor executor) {
		
		boolean keepAliveEnabled = config.getWs().getKeepAlive().isEnabled();
		int keepAliveInterval = config.getWs().getKeepAlive().getIntervalMillis();
		int sendTimeLimit = config.getWs().getSendTimeLimit();
		int sendBufferSizeLimit = config.getWs().getSendBufferSizeLimit();
		
		return new MPerConnectionProtocolHandler(graphQL, executor, keepAliveEnabled ? defaultTaskScheduler() : null,
				keepAliveInterval, sendTimeLimit, sendBufferSizeLimit, jwtService);
	}
	
	private TaskScheduler defaultTaskScheduler() {
		ThreadPoolTaskScheduler threadPoolScheduler = new ThreadPoolTaskScheduler();
		threadPoolScheduler.setThreadNamePrefix("GraphQLWSKeepAlive-");
		threadPoolScheduler.setPoolSize(Runtime.getRuntime().availableProcessors());
		threadPoolScheduler.setRemoveOnCancelPolicy(true);
		threadPoolScheduler.setRejectedExecutionHandler(new ThreadPoolExecutor.DiscardPolicy());
		threadPoolScheduler.initialize();
		return threadPoolScheduler;
	}
	
} 
