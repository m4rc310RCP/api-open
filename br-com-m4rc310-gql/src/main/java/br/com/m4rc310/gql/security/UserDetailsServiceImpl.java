package br.com.m4rc310.gql.security;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public class UserDetailsServiceImpl implements UserDetailsService {

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		return org.springframework.security.core.userdetails.User.builder()
		        .username("test@m4rc310.com.br")
		        .password("test123")
		        .authorities("Admin")
		        .build();
	}

}
