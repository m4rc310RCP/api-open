package br.com.m4rc310.gql;

import org.springframework.security.crypto.password.PasswordEncoder;

import br.com.m4rc310.gql.messages.i18n.M;
import br.com.m4rc310.gql.security.IMAuthUserProvider;
import lombok.Setter;

@Setter
public abstract class MUserProvider implements IMAuthUserProvider {
	protected M m;
	protected PasswordEncoder encoder;
}
