package br.com.m4rc310.graphql.security;

import br.com.m4rc310.graphql.exceptions.MException;
import br.com.m4rc310.graphql.messages.i18n.M;
import br.com.m4rc310.graphql.security.dto.MUser;

/**
 * The Interface IMAuthUserProvider.
 */
public interface IMAuthUserProvider {
	
	/**
	 * Auth user.
	 *
	 * @param username the username
	 * @param password the password
	 * @return the m user
	 * @throws Exception the exception
	 */
	MUser authUser(String username, Object password) throws Exception;
	
	/**
	 * Gets the user from username.
	 *
	 * @param username the username
	 * @return the user from username
	 */
	MUser getUserFromUsername(String username);
	
	/**
	 * Load user.
	 *
	 * @param jwt   the jwt
	 * @param type  the type
	 * @param token the token
	 * @return the m user
	 * @throws MException the m exception
	 */
	MUser loadUser(MGraphQLJwtService jwt, MEnumToken type, String token) throws MException;

	/**
	 * Checks if is valid user.
	 *
	 * @param user the user
	 * @return true, if is valid user
	 */
	boolean isValidUser(MUser user);
	
	
	void setMessage(M m);

	/**
	 * Valid user access.
	 *
	 * @param authToken the auth token
	 * @param roles     the roles
	 * @throws MException the m exception
	 */
	void validUserAccess(MAuthToken authToken, String[] roles) throws MException;
	
	
}
