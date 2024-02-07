package br.com.m4rc310.graphql.configs;

import java.util.Locale;
import java.util.concurrent.CompletableFuture;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationListener;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.stereotype.Component;

import br.com.m4rc310.auth.messages.MMessageBuilder;
import br.com.m4rc310.graphql.properties.MGraphQLProperty;
import br.com.m4rc310.graphql.security.MGraphQLJwtService;
import graphql.GraphQL;
import graphql.GraphQL.Builder;
import graphql.execution.AsyncExecutionStrategy;
import graphql.execution.DataFetcherExceptionHandlerParameters;
import graphql.execution.DataFetcherExceptionHandlerResult;
import graphql.execution.ResultPath;
import graphql.execution.SimpleDataFetcherExceptionHandler;
import graphql.language.SourceLocation;
import graphql.schema.GraphQLSchema;
import io.leangen.graphql.metadata.messages.MessageBundle;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@AutoConfiguration
@EnableConfigurationProperties(MGraphQLProperty.class)
@ConditionalOnProperty(name = MGraphQLProperty.ENABLE_GRAPHQL, havingValue = "true", matchIfMissing = false)
public class MGraphQLAutoConfiguration {

	@Value("${m4rc310.graphql.security.enable:true}")
	private boolean enableSecurity;

	@Value("${IS_DEV:false}")
	private boolean isDev;

	private MMessageBuilder messageBuilder;

	public MGraphQLAutoConfiguration() {
	}
	
	@Bean("status-graphql")
	void status() {
		log.info("~> Module '{}' has been loaded.", getClass().getName());
	}
	
	@Scope("singleton")
	@Bean("message_builder")
	MMessageBuilder getMessageBuilder() {
		return this.messageBuilder = new MMessageBuilder();
	}
	

	@Bean
	MGraphQLJwtService getMGraphQLJwtService() {
		return new MGraphQLJwtService();
	}

	@Bean
	GraphQL makeErrorInterceptor(GraphQLSchema schema) {
		Builder builder = GraphQL.newGraphQL(schema);

		AsyncExecutionStrategy aes = new AsyncExecutionStrategy(new SimpleDataFetcherExceptionHandler() {
			@Override
			public CompletableFuture<DataFetcherExceptionHandlerResult> handleException(
					DataFetcherExceptionHandlerParameters handlerParameters) {
				return CompletableFuture.completedFuture(handleExceptionImpl(handlerParameters));
			}

			private DataFetcherExceptionHandlerResult handleExceptionImpl(
					DataFetcherExceptionHandlerParameters handlerParameters) {
				Throwable exception = unwrap(handlerParameters.getException());
				SourceLocation sourceLocation = handlerParameters.getSourceLocation();
				ResultPath path = handlerParameters.getPath();

				MExceptionWhileDataFetching error = new MExceptionWhileDataFetching(path, exception, sourceLocation);

				return DataFetcherExceptionHandlerResult.newResult().error(error).build();
			}

		});

		builder.queryExecutionStrategy(aes);
		builder.mutationExecutionStrategy(aes);
		builder.subscriptionExecutionStrategy(aes);

		return builder.build();
	}

	@Bean
	SecurityFilterChain loadSecurity(HttpSecurity http, MGraphQLJwtService jwt) throws Exception {
		log.info("Add Security");
		if (!enableSecurity) {
			return http.authorizeHttpRequests((auth) -> auth.anyRequest().permitAll()).build();
		}
		return http.authorizeHttpRequests((auth) -> auth.anyRequest().permitAll()).build();
	}

	@Bean
	WebSecurityCustomizer webSecurityCustomizer() {
		if (!enableSecurity) {
			return (web) -> web.ignoring().requestMatchers("/*");
		}
		return null;
	}

	@Bean("messageSource")
	MessageSource getMessageSource() {
		ResourceBundleMessageSource source = new ResourceBundleMessageSource();
		source.setBasenames("messages/message");
		return source;
	}

	@Bean
	MessageBundle messageBundle() {
		return key -> getString(key);
	}

	public String getString(String pattern, Object... args) {
		String REGEX = "[^a-zA-Z0-9_]+";
		String ret = pattern.replaceAll(REGEX, "_");
		try {
			String message = getMessageSource().getMessage(pattern, args, Locale.forLanguageTag("pt-BR"));
			try {
				if (!pattern.startsWith("desc.")) {
					pattern = String.format("desc.%s", pattern);
					message = getMessageSource().getMessage(pattern, args, Locale.forLanguageTag("pt-BR"));
				}
			} catch (Exception e) {
				getMessageBuilder().appendText(pattern, ret);
			}
			return message;
		} catch (Exception e) {
			getMessageBuilder().appendText(pattern, ret);
			return ret;
		}
	}

	@Component
	public class MApplicationListener implements ApplicationListener<ApplicationReadyEvent> {

		@Override
		public void onApplicationEvent(ApplicationReadyEvent event) {
			if (isDev) {
				log.info("~> Process messages");
				getMessageBuilder().fixUnknowMessages();
			}
		}
	}

}
