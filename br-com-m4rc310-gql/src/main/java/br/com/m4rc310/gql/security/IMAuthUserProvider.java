package br.com.m4rc310.gql.security;

import br.com.m4rc310.gql.dto.MAuthToken;
import br.com.m4rc310.gql.dto.MEnumToken;
import br.com.m4rc310.gql.dto.MUser;
import br.com.m4rc310.gql.jwt.MGraphQLJwtService;

/**
 * <p>IMAuthUserProvider interface.</p>
 *
 * @author marcelo
 * @version $Id: $Id
 */
public interface IMAuthUserProvider {

	/**
	 * <p>authUser.</p>
	 *
	 * @param username a {@link java.lang.String} object
	 * @param password a {@link java.lang.Object} object
	 * @return a {@link br.com.m4rc310.gql.dto.MUser} object
	 * @throws java.lang.Exception if any.
	 */
	MUser authUser(String username, Object password) throws Exception;
	
	/**
	 * <p>getUserFromUsername.</p>
	 *
	 * @param username a {@link java.lang.String} object
	 * @return a {@link br.com.m4rc310.gql.dto.MUser} object
	 */
	MUser getUserFromUsername(String username);
	
	/**
	 * <p>isValidUser.</p>
	 *
	 * @param user a {@link br.com.m4rc310.gql.dto.MUser} object
	 * @return a boolean
	 */
	boolean isValidUser(MUser user);

	/**
	 * <p>validUserAccess.</p>
	 *
	 * @param authToken a {@link br.com.m4rc310.gql.dto.MAuthToken} object
	 * @param roles an array of {@link java.lang.String} objects
	 * @throws java.lang.Exception if any.
	 */
	void validUserAccess(MAuthToken authToken, String[] roles) throws Exception;
}
