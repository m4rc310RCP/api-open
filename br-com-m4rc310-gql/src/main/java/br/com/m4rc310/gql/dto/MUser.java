package br.com.m4rc310.gql.dto;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import io.leangen.graphql.annotations.GraphQLIgnore;
import io.leangen.graphql.annotations.GraphQLQuery;
import lombok.Data;

/**
 * <p>MUser class.</p>
 *
 * @author marcelo
 * @version $Id: $Id
 */
@Data
public class MUser implements UserDetails{
	
	private static final long serialVersionUID = -4778346728598710961L;
	
	private Long code;
	private String username;
	private String password;
	private String[] roles;
	private boolean accountNonExpired;
	private boolean accountNonLocked;
	private boolean credentialsNonExpired;
	private boolean enabled;

	/** {@inheritDoc} */
	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		Collection<GrantedAuthority> authorities = new ArrayList<>();
		if (Objects.nonNull(roles)) {
			for(String role : roles) {
				authorities.add(new SimpleGrantedAuthority(role));
			}
		}
		return authorities;
	}

}
