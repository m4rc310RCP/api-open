package br.com.m4rc310.gql.dto;

import java.util.Arrays;
import java.util.stream.Collectors;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MAuthToken extends UsernamePasswordAuthenticationToken{

	private static final long serialVersionUID = 7895979137011266408L;

	public MAuthToken(MUser user) {
//		super(user, user.getUsername(),
//				Arrays.stream(user.getRoles()).map(SimpleGrantedAuthority::new).collect(Collectors.toList()));
//		super(user, user.getUsername(), user.getAuthorities());
//		super(user.getUsername(), user.getPassword(), user.getAuthorities());
		super(user.getUsername(), user.getPassword(),
				Arrays.stream(user.getRoles()).map(SimpleGrantedAuthority::new).collect(Collectors.toList()));
	}
	
//	private Collection<? extends GrantedAuthority>  getGrantedAuthorities(MUser user){
//		Collection<? extends GrantedAuthority> authorities = user.getAuthorities();
//	}


}
