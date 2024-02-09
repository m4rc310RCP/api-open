package br.com.m4rc310.gql.security;

import java.io.IOException;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.filter.OncePerRequestFilter;

import br.com.m4rc310.gql.dto.MUser;
import br.com.m4rc310.gql.jwt.MGraphQLJwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

/**
 * The Constant log.
 *
 * @author marcelo
 * @version $Id: $Id
 */
@Slf4j
@Configuration
public class MGraphQLSecurity {

	/** The jwt auth filter. */
	@SuppressWarnings("unused")
	private final OncePerRequestFilter jwtAuthFilter = getJwtAuthFilter();

//	/** The basic auth filter. */
//	private final OncePerRequestFilter basicAuthFilter = getBasicAuthFilter();
//
//	/** The bearer auth filter. */
//	private final OncePerRequestFilter bearerAuthFilter = getBearerAuthFilter();
//
//	/** The test filter. */
//	private final OncePerRequestFilter testFilter = getTestAuthFilter();
//
//	/** The test filter. */
	private final OncePerRequestFilter noneFilter = getNoneFilter();

	/** The auth user provider. */
	private IMAuthUserProvider authUserProvider;

	/** The dev. */
	@Value("${IS_DEV:true}")
	private boolean dev;

	/** The jwt. */
	private MGraphQLJwtService jwt;

	final String AUTHORIZATION = "Authorization";

	/**
	 * Gets the security filter chain.
	 *
	 * @param http             the http
	 * @param jwt              the jwt
	 * @param authUserProvider the auth user provider
	 * @return the security filter chain
	 * @throws java.lang.Exception the exception
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
						.requestMatchers(HttpMethod.GET, "/gui**").permitAll()
						.requestMatchers(HttpMethod.POST, "/graphql**", "/graphql").authenticated()
						.anyRequest().authenticated())
//				.addFilterBefore(testFilter, UsernamePasswordAuthenticationFilter.class)
//				.addFilterBefore(basicAuthFilter, UsernamePasswordAuthenticationFilter.class)
//				.addFilterBefore(bearerAuthFilter, UsernamePasswordAuthenticationFilter.class)
//				.addFilterBefore(noneFilter, UsernamePasswordAuthenticationFilter.class)
				.addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class).build();
	}

	private void setAuthentication(MUser user) throws Exception {
		if (!authUserProvider.isValidUser(user)) {
			return;
		}

		Authentication authentication = createAuthenticationToken(user);
		SecurityContextHolder.getContext().setAuthentication(authentication);
	}

	private Authentication createAuthenticationToken(MUser user) {
		UserPrincipal userPrincipal = UserPrincipal.create(user);
		return new UsernamePasswordAuthenticationToken(userPrincipal, null, userPrincipal.getAuthorities());
	}

//	/**
//	 * Gets the test auth filter.
//	 *
//	 * @return the test auth filter
//	 */
//	private OncePerRequestFilter getTestAuthFilter() {
//		return new OncePerRequestFilter() {
//			final MEnumToken typeToken = MEnumToken.TEST;
//
//			@Override
//			protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
//					FilterChain filterChain) throws ServletException, IOException {
//
//				String token = getToken(request);
//				log.info(token);
//
////				String stype = type.getDescription();
////				
////				if (MEnumToken.TEST.compareTo(type) != 0 || !authorizationHeader.startsWith(stype)) {
////					filterChain.doFilter(request, response);
////					return;				
////				}
//
////
////				if (Objects.isNull(authorizationHeader) || !authorizationHeader.startsWith(stype)) {
////					filterChain.doFilter(request, response);
////					return;
////				}
////
////				try {
////					String token = authorizationHeader.replace(stype, "").trim();
////					MUser user = authUserProvider.loadUser(jwt, typeToken, token);
////					setAuthentication(user);
////				} catch (Exception e) {
////	                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
////	                response.getWriter().write(e.getMessage());
////	                return;
////				}
//
//				filterChain.doFilter(request, response);
//			}
//		};
//	}

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

				try {

					MUser user = jwt.getMUser(request);

					if (Objects.isNull(user)) {
						throw new Exception("User not found");
					}
					
					log.info("# {}", user);

					
					setAuthentication(user);
					filterChain.doFilter(request, response);
				} catch (Exception e) {
					response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
					filterChain.doFilter(request, response);
				}

			}
		};
	}
	private OncePerRequestFilter getNoneFilter() {
		
		return new OncePerRequestFilter() {
			
			@Override
			protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
					FilterChain filterChain) throws ServletException, IOException {
				
				try {
					jwt.getMUser(request) ;
					
				} catch (Exception e) {
					response.getWriter().write(e.getMessage());
					filterChain.doFilter(request, response);
				}		
			}
		};
	}

}
