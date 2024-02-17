package br.com.m4rc310.gql.security;

import java.io.IOException;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.filter.OncePerRequestFilter;

import br.com.m4rc310.gql.dto.MUser;
import br.com.m4rc310.gql.jwt.MGraphQLJwtService;
import br.com.m4rc310.gql.services.MFluxService;
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
//@Configuration
//@EnableWebSecurity
public class MGraphQLSecurity {

	@Value("${br.com.m4rc310.gql.security.enable:false}")
	private boolean enableSecurity;

//	@Autowired
//	private UserDetailsServiceImpl userDetailsService;

	private final OncePerRequestFilter jwtAuthFilter = getJWTFilter();

	private MGraphQLJwtService jwt;

	private MFluxService flux;

	public SecurityFilterChain getSecurityFilterChain(HttpSecurity http, MGraphQLJwtService jwt,
			IMAuthUserProvider provider, MFluxService flux) throws Exception {

		this.jwt = jwt;
		this.flux = flux;
		
		log.debug("enableSecurity -> {}", enableSecurity);

		if (!enableSecurity) {
			http = http.cors(AbstractHttpConfigurer::disable);
			http = http.csrf(AbstractHttpConfigurer::disable);
			http = http.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
					.authorizeHttpRequests(auth -> auth.anyRequest().permitAll());
			return http.build();
		}

		http = http.cors(AbstractHttpConfigurer::disable);
		http = http.csrf(AbstractHttpConfigurer::disable);
		http = http.anonymous(AbstractHttpConfigurer::disable);
		http = http.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
		http = http.securityContext(c -> c.requireExplicitSave(false));
		http = http.addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);
		http = http.authorizeHttpRequests(auth -> {
			auth.requestMatchers(HttpMethod.GET, "/gui/**", "/graphql/**").permitAll();
			auth.requestMatchers(HttpMethod.POST, "/graphql/**").authenticated();
			auth.anyRequest().denyAll();
		});
		
		log.debug("getSecurityFilterChain -> {}", http);

		return http.build();

	}

	private OncePerRequestFilter getJWTFilter() {
		return new OncePerRequestFilter() {

			@Override
			protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
					FilterChain filterChain) throws ServletException, IOException {

				try {
					
					resetAuthenticate();

					MUser user = jwt.getMUser(request);
					
					if (Objects.isNull(user)) {
						throw new Exception("User not found.");
					}

					authenticate(user);

					filterChain.doFilter(request, response);
				} catch (Exception e) {
					log.debug(e.getMessage(), e);
					resetAuthenticate();
					filterChain.doFilter(request, response);
				}
			}
		};
	}

	public void authenticate(MUser user) {
		UserPrincipal principal = UserPrincipal.create(user);

		UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(principal, null,
				principal.getAuthorities());

		SecurityContext context = SecurityContextHolder.createEmptyContext();
		context.setAuthentication(authentication);
		SecurityContextHolder.setContext(context);

		flux.setUser(user);
	}

	public void resetAuthenticate() {
		flux.setUser(null);
		SecurityContextHolder.setContext(SecurityContextHolder.createEmptyContext());
	}

//	@Bean
//	PasswordEncoder passwordEncoder() {
//		return new BCryptPasswordEncoder();
//	}

//	@Bean
//	AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
//		AuthenticationManagerBuilder authenticationManagerBuilder = http
//				.getSharedObject(AuthenticationManagerBuilder.class);
//		authenticationManagerBuilder.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
//		return authenticationManagerBuilder.build();
//	}

}
