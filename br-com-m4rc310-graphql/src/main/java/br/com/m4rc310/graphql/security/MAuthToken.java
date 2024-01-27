package br.com.m4rc310.graphql.security;

import java.util.Arrays;
import java.util.stream.Collectors;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import br.com.m4rc310.graphql.security.dto.MUser;

/**
 * The Class MAuthToken.
 */
public class MAuthToken extends UsernamePasswordAuthenticationToken {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

	/**
	 * Instantiates a new m auth token.
	 *
	 * @param user the user
	 */
	public MAuthToken(MUser user) {
		super(user, user.getCode(),
				Arrays.stream(user.getRoles()).map(SimpleGrantedAuthority::new).collect(Collectors.toList()));
	}

}
