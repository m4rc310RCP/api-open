package br.com.m4rc310.graphql.configs;

import java.util.Optional;
import java.util.concurrent.ThreadPoolExecutor;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;

import br.com.m4rc310.graphql.handlers.MPerConnectionProtocolHandler;
import br.com.m4rc310.graphql.security.MGraphQLJwtService;
import graphql.GraphQL;
import graphql.schema.GraphQLSchema;
import io.leangen.graphql.spqr.spring.autoconfigure.DataLoaderRegistryFactory;
import io.leangen.graphql.spqr.spring.autoconfigure.SpqrProperties;
import io.leangen.graphql.spqr.spring.autoconfigure.WebSocketAutoConfiguration;
import io.leangen.graphql.spqr.spring.web.apollo.PerConnectionApolloHandler;
import io.leangen.graphql.spqr.spring.web.mvc.websocket.GraphQLWebSocketExecutor;
import lombok.extern.slf4j.Slf4j;

/** The Constant log. */
@Slf4j
@AutoConfiguration
@EnableWebSocket
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
@ConditionalOnClass(WebSocketConfigurer.class)
@ConditionalOnProperty(name = "graphql.spqr.ws.enabled", havingValue = "false", matchIfMissing = true)
@ConditionalOnBean(GraphQLSchema.class)
public class MGraphQLWebSocketAutoConfiguration extends WebSocketAutoConfiguration {

	/** The config. */
	private SpqrProperties config;
	
	/** The graph QL. */
	private GraphQL graphQL;
	
	/** The jwt service. */
	private MGraphQLJwtService jwtService;
	
	/**
	 * Instantiates a new m graph QL web socket auto configuration.
	 *
	 * @param graphQL                   the graph QL
	 * @param config                    the config
	 * @param dataLoaderRegistryFactory the data loader registry factory
	 * @param jwtService                the jwt service
	 */
	public MGraphQLWebSocketAutoConfiguration(GraphQL graphQL, SpqrProperties config,
			Optional<DataLoaderRegistryFactory> dataLoaderRegistryFactory, MGraphQLJwtService jwtService) {
		super(graphQL, config, dataLoaderRegistryFactory);

		this.graphQL = graphQL;
		this.config = config;
		this.jwtService = jwtService;
	}

	/**
	 * Status.
	 */
	@Bean(name = "init.MGraphQLWebSocketAutoConfiguration")
	void status() {
		log.info("***** Enable WS Security ****");
	}

	/**
	 * Web socket handler.
	 *
	 * @param executor the executor
	 * @return the per connection apollo handler
	 */
	@Override
	public PerConnectionApolloHandler webSocketHandler(GraphQLWebSocketExecutor executor) {

		boolean keepAliveEnabled = config.getWs().getKeepAlive().isEnabled();
		int keepAliveInterval = config.getWs().getKeepAlive().getIntervalMillis();
		int sendTimeLimit = config.getWs().getSendTimeLimit();
		int sendBufferSizeLimit = config.getWs().getSendBufferSizeLimit();

		return new MPerConnectionProtocolHandler(graphQL, executor, keepAliveEnabled ? defaultTaskScheduler() : null,
				keepAliveInterval, sendTimeLimit, sendBufferSizeLimit, jwtService);
	}

	/**
	 * Default task scheduler.
	 *
	 * @return the task scheduler
	 */
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
