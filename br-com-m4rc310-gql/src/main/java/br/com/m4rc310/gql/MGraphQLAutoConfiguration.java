package br.com.m4rc310.gql;

import java.lang.reflect.Constructor;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationListener;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.context.annotation.Scope;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.core.annotation.Order;
import org.springframework.core.type.filter.RegexPatternTypeFilter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import br.com.m4rc310.gql.exceptions.MException;
import br.com.m4rc310.gql.exceptions.MExceptionWhileDataFetching;
import br.com.m4rc310.gql.jwt.MGraphQLJwtService;
import br.com.m4rc310.gql.mappers.annotations.MAuth;
import br.com.m4rc310.gql.mappers.annotations.MMapper;
import br.com.m4rc310.gql.messages.MMessageBuilder;
import br.com.m4rc310.gql.properties.MGraphQLProperty;
import br.com.m4rc310.gql.security.IMAuthUserProvider;
import br.com.m4rc310.gql.security.MGraphQLSecurity;
import br.com.m4rc310.gql.security.UserDetailsServiceImpl;
import br.com.m4rc310.gql.security.UserPrincipal;
import br.com.m4rc310.gql.services.MFluxService;
import graphql.GraphQL;
import graphql.GraphQL.Builder;
import graphql.execution.AsyncExecutionStrategy;
import graphql.execution.DataFetcherExceptionHandlerParameters;
import graphql.execution.DataFetcherExceptionHandlerResult;
import graphql.execution.ResultPath;
import graphql.execution.SimpleDataFetcherExceptionHandler;
import graphql.language.SourceLocation;
import graphql.schema.GraphQLSchema;
import io.leangen.graphql.ExtensionProvider;
import io.leangen.graphql.GeneratorConfiguration;
import io.leangen.graphql.execution.InvocationContext;
import io.leangen.graphql.execution.ResolverInterceptor;
import io.leangen.graphql.execution.ResolverInterceptorFactory;
import io.leangen.graphql.generator.mapping.TypeMapper;
import io.leangen.graphql.metadata.messages.MessageBundle;
import io.leangen.graphql.spqr.spring.util.GlobalResolverInterceptorFactory;
import lombok.extern.slf4j.Slf4j;

/**
 * <p>
 * MGraphQLAutoConfiguration class.
 * </p>
 *
 * @author marcelo
 * @version $Id: $Id
 */
@Slf4j
@AutoConfiguration
@EnableWebSecurity
@EnableConfigurationProperties(MGraphQLProperty.class)
@ConditionalOnProperty(name = MGraphQLProperty.ENABLE_GRAPHQL, havingValue = "true", matchIfMissing = false)
public class MGraphQLAutoConfiguration {

	@Value("${m4rc310.graphql.security.enable:true}")
	private boolean enableSecurity;

	@Value("${IS_DEV:false}")
	private boolean isDev;

	@Autowired(required = false)
	private IMAuthUserProvider provider;

	@Bean("br.com.m4rc310.gql.enable")
	void init() {
		log.info("~> Loading {}...", getClass().getName());
	}

	@Bean
	MGraphQLJwtService loadMGraphQLJwtService() {
		return new MGraphQLJwtService();
	}

	@Bean
	MFluxService loadMFluxService() {
		return new MFluxService();
	}

	@Bean
	PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Scope("singleton")
	@Bean("message_builder")
	@Order(0)
	MMessageBuilder getMessageBuilder() {
		return new MMessageBuilder();
	}

	@Bean("messageSource")
	MessageSource getMessageSource() {
		ResourceBundleMessageSource source = new ResourceBundleMessageSource();
		source.setBasenames("messages/message");
		log.info("Load message source {}", source);
		return source;
	}

	@Bean
	MessageBundle messageBundle(MMessageBuilder messageBuilder, MessageSource messageSource) {
		return key -> getString(messageBuilder, messageSource, key);
	}

	/**
	 * <p>
	 * getString.
	 * </p>
	 *
	 * @param messageBuilder a {@link br.com.m4rc310.gql.messages.MMessageBuilder}
	 *                       object
	 * @param messageSource  a {@link org.springframework.context.MessageSource}
	 *                       object
	 * @param pattern        a {@link java.lang.String} object
	 * @param args           a {@link java.lang.Object} object
	 * @return a {@link java.lang.String} object
	 */
	public String getString(MMessageBuilder messageBuilder, MessageSource messageSource, String pattern,
			Object... args) {
		String REGEX = "[^a-zA-Z0-9_]+";
		String ret = pattern.replaceAll(REGEX, "_");
		try {
			String message = messageSource.getMessage(pattern, args, Locale.forLanguageTag("pt-BR"));
			try {
				if (!pattern.startsWith("desc.")) {
					pattern = String.format("desc.%s", pattern);
					messageSource.getMessage(pattern, args, Locale.forLanguageTag("pt-BR"));
				}
			} catch (Exception e) {
				messageBuilder.appendText(pattern, ret);
			}
			return message;
		} catch (Exception e) {
			messageBuilder.appendText(pattern, ret);
			return ret;
		}
	}

	@Component
	public class MApplicationListener implements ApplicationListener<ApplicationReadyEvent> {

		@Autowired
		MMessageBuilder messageBuilder;

		@Override
		public void onApplicationEvent(ApplicationReadyEvent event) {
			if (isDev) {
				log.info("~> Process messages");
				messageBuilder.fixUnknowMessages();
			}
		}
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
//		builder.subscriptionExecutionStrategy(aes);
		return builder.build();
	}



	@Bean
	ExtensionProvider<GeneratorConfiguration, TypeMapper> pageableInputField() {
		log.info("~> Starting '{}'...", "Custom Mappers");

		return (config, defaults) -> {

			final ClassPathScanningCandidateComponentProvider provider = new ClassPathScanningCandidateComponentProvider(
					false);
			provider.addIncludeFilter(new RegexPatternTypeFilter(Pattern.compile(".*")));

			final Set<BeanDefinition> classes = provider.findCandidateComponents("br");
			for (BeanDefinition bean : classes) {
				try {
					Class<?> clazz = Class.forName(bean.getBeanClassName());
					if (clazz.isAnnotationPresent(MMapper.class)) {
						Constructor<?> constructor = clazz.getDeclaredConstructor();
						TypeMapper mapper = (TypeMapper) constructor.newInstance();
						defaults.prepend(mapper);
						log.info("~~> Prepend mapper: {}", mapper);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			return defaults;
		};
	}

//	@Bean
//	GraphQLSchema graphQLSchema(GraphQLSchemaGenerator schemaGenerator) {
//		schemaGenerator.withResolverInterceptors(new AuthInterceptor());
//
////		schemaGenerator.withResolverBuilders(
////				new AnnotatedResolverBuilder().withOperationInfoGenerator(new MOperationInfoGenerator()));
//		return schemaGenerator.generate();
//	}

	@Bean
	ExtensionProvider<GeneratorConfiguration, ResolverInterceptorFactory> customInterceptors() {
		List<ResolverInterceptor> authInterceptor = Collections.singletonList(new AuthInterceptor());
		return (config, interceptors) -> interceptors.append(new GlobalResolverInterceptorFactory(authInterceptor));
	}

//	Security
	private class AuthInterceptor implements ResolverInterceptor {

		@Override
		public Object aroundInvoke(InvocationContext context, Continuation continuation) throws Exception {
			Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
			if (Objects.nonNull(authentication)) {
				UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();
				MAuth auth = context.getResolver().getExecutable().getDelegate().getAnnotation(MAuth.class);
				if (Objects.nonNull(auth)) {
					boolean isAuth = principal == null ? false
							: principal.getAuthorities().stream()
									.anyMatch(ga -> Arrays.asList(auth.roles()).contains(ga.getAuthority()));
					if (!isAuth) {
						throw getWebException(401, "Access unauthorizade.");
					}
				}
			}
			return continuation.proceed(context);
		}
	}

	private MException getWebException(int code, String message, Object... args) {
		message = MessageFormat.format(message, args);
		return MException.to(code, message);
	}

	@Bean
	WebMvcConfigurer corsConfigurer() {
		return new WebMvcConfigurer() {
			@Override
			public void addCorsMappings(CorsRegistry registry) {
				log.info("~> Add cors configurer");
				registry.addMapping("/graphql").allowedOrigins("*");
			}
		};
	}

	@Bean
	MGraphQLSecurity loadMGraphQLSecurity() {
		return new MGraphQLSecurity();
	}

	@Bean
	SecurityFilterChain securityFilterChain(HttpSecurity http, MGraphQLJwtService jwt, MGraphQLSecurity graphQLSecurity,
			MFluxService flux) throws Exception {
		if (Objects.isNull(provider)) {
			String error = "Implement a %s in main project.";
			error = String.format(error, IMAuthUserProvider.class.getName());
			throw new UnsupportedOperationException(error);
		}
		return graphQLSecurity.getSecurityFilterChain(http, jwt, provider, flux);
	}

	@Bean
	UserDetailsServiceImpl loadUserDetailsServiceImpl() {
		return new UserDetailsServiceImpl();
	}

}
