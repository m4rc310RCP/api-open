package br.com.m4rc310.gql.security;

import java.util.Collection;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import br.com.m4rc310.gql.dto.MUser;
import lombok.Data;

/**
 * <p>UserPrincipal class.</p>
 *
 * @author marcelo
 * @version $Id: $Id
 */
@Data
public class UserPrincipal {
	
	private String username;
    private String password;
    private Collection<? extends GrantedAuthority> authorities;
	
	/**
	 * <p>Constructor for UserPrincipal.</p>
	 *
	 * @param user a {@link br.com.m4rc310.gql.dto.MUser} object
	 */
	public UserPrincipal(MUser user) {
		this.username = user.getUsername();
		this.password = user.getPassword();
		if (Objects.nonNull(user.getRoles())) {
			this.authorities = user.getAuthorities().stream().map(role -> {
				return new SimpleGrantedAuthority(role.getAuthority());
			}).collect(Collectors.toList());
		}
	}
	
	/**
	 * <p>create.</p>
	 *
	 * @param user a {@link br.com.m4rc310.gql.dto.MUser} object
	 * @return a {@link br.com.m4rc310.gql.security.UserPrincipal} object
	 */
	public static UserPrincipal create(MUser user){
        return new UserPrincipal(user);
    }
	
	
}
