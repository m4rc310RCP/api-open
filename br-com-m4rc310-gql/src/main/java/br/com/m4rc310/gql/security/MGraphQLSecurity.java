package br.com.m4rc310.gql.security;

import java.io.IOException;
import java.util.Enumeration;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.filter.OncePerRequestFilter;

import br.com.m4rc310.gql.dto.MAuthToken;
import br.com.m4rc310.gql.dto.MEnumToken;
import br.com.m4rc310.gql.dto.MUser;
import br.com.m4rc310.gql.jwt.MGraphQLJwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

/** The Constant log. */
@Slf4j
@Configuration
public class MGraphQLSecurity {

	/** The jwt auth filter. */
	@SuppressWarnings("unused")
	private final OncePerRequestFilter jwtAuthFilter = getJwtAuthFilter();

	/** The basic auth filter. */
	private final OncePerRequestFilter basicAuthFilter = getBasicAuthFilter();

	/** The bearer auth filter. */
	private final OncePerRequestFilter bearerAuthFilter = getBearerAuthFilter();

	/** The test filter. */
	private final OncePerRequestFilter testFilter = getTestAuthFilter();

	/** The auth user provider. */
	private IMAuthUserProvider authUserProvider;

	/** The dev. */
	@Value("${IS_DEV:true}")
	private boolean dev;

	/** The jwt. */
	private MGraphQLJwtService jwt;

	/**
	 * Gets the security filter chain.
	 *
	 * @param http             the http
	 * @param jwt              the jwt
	 * @param authUserProvider the auth user provider
	 * @return the security filter chain
	 * @throws Exception the exception
	 */
	public SecurityFilterChain getSecurityFilterChain(HttpSecurity http, MGraphQLJwtService jwt,
			IMAuthUserProvider authUserProvider) throws Exception {
		this.jwt = jwt;
		this.authUserProvider = authUserProvider;

//		return http.cors(cors -> cors.disable()).csrf(csrf -> csrf.disable())
//				.sessionManagement(custom -> custom.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
//				.authenticationProvider(getAuthenticationProvider)
//				.addFilterBefore(testFilter, UsernamePasswordAuthenticationFilter.class)
////				.addFilterBefore(basicAuthFilter, UsernamePasswordAuthenticationFilter.class)
////				.addFilterBefore(bearerAuthFilter, UsernamePasswordAuthenticationFilter.class)
//				.authorizeHttpRequests(auth -> {
//					auth.requestMatchers(HttpMethod.GET, "/graphql/**", "/gui/**").permitAll();
//					auth.requestMatchers(HttpMethod.POST, "/graphql/**").authenticated();
//					auth.anyRequest().denyAll();
//				}).build();

		return http.csrf(csrf -> csrf.disable())
				.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
				.authorizeHttpRequests((request) -> request
					.requestMatchers(HttpMethod.GET, "/gui/**", "/graphql/**").permitAll()
					.requestMatchers(HttpMethod.POST, "/graphql/**").permitAll()
						.anyRequest().authenticated())
				.addFilterBefore(testFilter, UsernamePasswordAuthenticationFilter.class).build();
	}

	/**
	 * Gets the test auth filter.
	 *
	 * @return the test auth filter
	 */
	private OncePerRequestFilter getTestAuthFilter() {
		return new OncePerRequestFilter() {
			final String AUTHORIZATION = "Authorization";
			final MEnumToken typeToken = MEnumToken.TEST;

			@Override
			protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
					FilterChain filterChain) throws ServletException, IOException {

				final String authorizationHeader = request.getHeader(AUTHORIZATION);
				String stype = typeToken.getDescription();

				if (Objects.isNull(authorizationHeader) || !authorizationHeader.startsWith(stype)) {
					filterChain.doFilter(request, response);
					return;
				}

				try {
					String token = authorizationHeader.replace(stype, "").trim();
					MUser user = authUserProvider.loadUser(jwt, typeToken, token);
					setAuthentication(user);
				} catch (Exception e) {
	                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
	                response.getWriter().write(e.getMessage());
	                return;
				}

				filterChain.doFilter(request, response);
			}
		};
	}

	private void setAuthentication(MUser user) throws Exception {
		if (!authUserProvider.isValidUser(user)) {
			throw new UnsupportedOperationException("Inválid user");
		}
		
		Authentication authentication = createAuthenticationToken(user);
		SecurityContextHolder.getContext().setAuthentication(authentication);
	}

	private Authentication createAuthenticationToken(MUser user) {
		UserPrincipal userPrincipal = UserPrincipal.create(user);
		return new UsernamePasswordAuthenticationToken(userPrincipal, null, userPrincipal.getAuthorities());
	}

	/**
	 * Gets the basic auth filter.
	 *
	 * @return the basic auth filter
	 */
	private OncePerRequestFilter getBasicAuthFilter() {
		final String AUTHORIZATION = "Authorization";
		final MEnumToken typeToken = MEnumToken.BASIC;
		final String stype = typeToken.getDescription();

		// final String BASIC = "Basic";

		return new OncePerRequestFilter() {
			@Override
			protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
					FilterChain filterChain) throws ServletException, IOException {

				final String authorizationHeader = request.getHeader(AUTHORIZATION);
				if (Objects.isNull(authorizationHeader) || !authorizationHeader.startsWith(stype)) {
					filterChain.doFilter(request, response);
					return;
				}

				try {
					String token = authorizationHeader.replace(stype, "").trim();
					MUser user = authUserProvider.loadUser(jwt, typeToken, token);
					SecurityContextHolder.getContext().setAuthentication(new MAuthToken(user));
				} catch (Exception e) {
					SecurityContextHolder.getContext().setAuthentication(null);
				}

				filterChain.doFilter(request, response);
			}
		};
	}

	/**
	 * Gets the bearer auth filter.
	 *
	 * @return the bearer auth filter
	 */
	private OncePerRequestFilter getBearerAuthFilter() {
		final String AUTHORIZATION = "Authorization";
		final MEnumToken typeToken = MEnumToken.BEARER;
		final String stype = typeToken.getDescription();

		return new OncePerRequestFilter() {
			@Override
			protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
					FilterChain filterChain) throws ServletException, IOException {

				final String authorizationHeader = request.getHeader(AUTHORIZATION);

				if (authorizationHeader == null || !authorizationHeader.startsWith(stype)) {
					filterChain.doFilter(request, response);
					return;
				}

				try {
					if (SecurityContextHolder.getContext().getAuthentication() == null) {
						final String token = authorizationHeader.replace(stype, "").trim();
						MUser user = authUserProvider.loadUser(jwt, typeToken, token);
						SecurityContextHolder.getContext().setAuthentication(new MAuthToken(user));

					}
					filterChain.doFilter(request, response);

				} catch (Exception e) {
					log.error(e.getMessage());
				}

				filterChain.doFilter(request, response);
			}
		};
	}

	/**
	 * Gets the m graph QL jwt service.
	 *
	 * @return the m graph QL jwt service
	 */
	@Bean
	MGraphQLJwtService getMGraphQLJwtService() {
		return new MGraphQLJwtService();
	}

	/**
	 * Gets the jwt auth filter.
	 *
	 * @return the jwt auth filter
	 */
	private OncePerRequestFilter getJwtAuthFilter() {

		return new OncePerRequestFilter() {

			@Override
			protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
					FilterChain filterChain) throws ServletException, IOException {

				final String AUTHORIZATION = "Authorization";

				final MEnumToken typeToken = MEnumToken.BEARER;
				final String stype = typeToken.getDescription();

				Enumeration<String> headers = request.getHeaderNames();
				while (headers.hasMoreElements()) {
					log.info(headers.nextElement());
				}

				log.info(">>> {}", request.getHeader(AUTHORIZATION));
				final String authorizationHeader = request.getHeader(AUTHORIZATION);
				if (authorizationHeader == null || !authorizationHeader.startsWith(stype)) {
					filterChain.doFilter(request, response);
					return;
				}

				log.info(authorizationHeader);

//				final String username;

				try {
					if (SecurityContextHolder.getContext().getAuthentication() == null) {
						final String token = authorizationHeader.replace(stype, "").trim();
						MUser user = authUserProvider.loadUser(jwt, typeToken, token);
						SecurityContextHolder.getContext().setAuthentication(new MAuthToken(user));

//					String username = jwt.extractUsername(token);					
						// MUserDetails user = MUserDetails.
						// .to(userDetails.getUserFromUsername(username));
//					if (jwtService.isTokenValid(token, user)) {
//						Date expiration = jwtService.extractExpiration(token);
//						user.getUser().setAccessValidUntil(expiration);
//						
//						UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
//								user, null, user.getAuthorities());
//		                authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
//		                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
//					}
					}
				} catch (Exception e) {
					log.error(e.getMessage());
				}

				filterChain.doFilter(request, response);
			}
		};
	}

	/** The get authentication provider. */
	private AuthenticationProvider getAuthenticationProvider = new AuthenticationProvider() {

		@Override
		public boolean supports(Class<?> authentication) {
			return true;
		}

		@Override
		public Authentication authenticate(Authentication authentication) throws AuthenticationException {

			log.info("Authentication : {}", authentication.getDetails());

			return null;
		}
	};

}
