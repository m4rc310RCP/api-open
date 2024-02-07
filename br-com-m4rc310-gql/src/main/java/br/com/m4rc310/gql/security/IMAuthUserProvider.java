package br.com.m4rc310.gql.security;

import br.com.m4rc310.gql.dto.MAuthToken;
import br.com.m4rc310.gql.dto.MEnumToken;
import br.com.m4rc310.gql.dto.MUser;
import br.com.m4rc310.gql.jwt.MGraphQLJwtService;

public interface IMAuthUserProvider {

	MUser authUser(String username, Object password) throws Exception;
	
	MUser getUserFromUsername(String username);
	
	MUser loadUser(MGraphQLJwtService jwt, MEnumToken type, String token) throws Exception;

	boolean isValidUser(MUser user);
	
	
//	void setMessage(M m);

	void validUserAccess(MAuthToken authToken, String[] roles) throws Exception;
}
