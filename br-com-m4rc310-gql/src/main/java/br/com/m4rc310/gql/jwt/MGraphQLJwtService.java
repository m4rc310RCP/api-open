package br.com.m4rc310.gql.jwt;

import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;

import br.com.m4rc310.gql.dto.MEnumToken;
import br.com.m4rc310.gql.dto.MUser;
import br.com.m4rc310.gql.security.IMAuthUserProvider;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.servlet.http.HttpServletRequest;
import lombok.Data;

/**
 * <p>
 * MGraphQLJwtService class.
 * </p>
 *
 * @author marcelo
 * @version $Id: $Id
 */
@Data
public class MGraphQLJwtService {

	@Value("${AUTH_SECURITY_SIGNING}")
	private String jwtSigningKey;

	@Value("${AUTH_SECURITY_SALT}")
	private String jwtSalt;

	@Value("${AUTH_SECURITY_ITERATION:10000}")
	private int iterationCount;

	@Value("${AUTH_SECURITY_KEY_LENGTH:128}")
	private int keyLength;

	@Value("${IS_DEV:false}")
	private boolean isDev;

	@Value("${m4rc310.graphql.security.expiration:864000000}")
	private Long expiration;

	@Autowired(required = false)
	private IMAuthUserProvider authUserProvider;

	private final String AUTHORIZATION = "Authorization";

	private static final String KEY_AUTH = "authorities";

	
	/**
	 * <p>
	 * isTokenExpirate.
	 * </p>
	 *
	 * @param token a {@link java.lang.String} object
	 * @return a boolean
	 */
	public boolean isTokenExpirate(String token) {
		try {
			Date exp = extractClaim(token, Claims::getExpiration);
			return exp.before(new Date());
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * <p>
	 * generateToken.
	 * </p>
	 *
	 * @param user a {@link br.com.m4rc310.gql.dto.MUser} object
	 * @return a {@link java.lang.String} object
	 */
	public String generateToken(MUser user) {
		Map<String, Object> claim = new HashMap<>();
		return createToken(claim, user);
	}

	/**
	 * <p>
	 * extractUsername.
	 * </p>
	 *
	 * @param token a {@link java.lang.String} object
	 * @return a {@link java.lang.String} object
	 */
	public String extractUsername(String token) {
		return extractClaim(token, Claims::getSubject);
	}

	/**
	 * <p>
	 * validateToken.
	 * </p>
	 *
	 * @param token a {@link java.lang.String} object
	 * @throws java.lang.Exception if any.
	 */
	public void validateToken(String token) throws Exception {
		assertMath(isTokenExpirate(token), "Token expirado!");
	}

	/**
	 * <p>
	 * extractClaim.
	 * </p>
	 *
	 * @param token    a {@link java.lang.String} object
	 * @param resolver a {@link java.util.function.Function} object
	 * @param <T>      a T class
	 * @return a T object
	 */
	public <T> T extractClaim(String token, Function<Claims, T> resolver) {
		Claims claims = Jwts.parser().setSigningKey(jwtSigningKey).parseClaimsJws(token).getBody();
		return resolver.apply(claims);
	}

	private void assertMath(boolean math, String message, Object... args) throws Exception {
		if (!math) {
			throw new Exception(message);
		}
	}

	private String createToken(Map<String, Object> claims, UserDetails details) {
		Date now = Date.from(Instant.now());
		Date exp = new Date(now.getTime() + expiration);

		JwtBuilder ret = Jwts.builder();
		ret.setClaims(claims);
		ret.setSubject(details.getUsername());
		ret.claim(KEY_AUTH, details.getAuthorities());
		ret.setIssuedAt(now);
		ret.setExpiration(exp);
		ret.signWith(SignatureAlgorithm.HS256, jwtSigningKey);

		return ret.compact();
	}

	/**
	 * <p>
	 * loadUserFromToken.
	 * </p>
	 *
	 * @param token a {@link java.lang.String} object
	 * @param type  a {@link br.com.m4rc310.gql.dto.MEnumToken} object
	 * @return a {@link br.com.m4rc310.gql.dto.MUser} object
	 * @throws java.lang.Exception if any.
	 */
	public MUser loadUserFromToken(String token, MEnumToken type) throws Exception {
		return getMUser(type, token);
//		return null;
	}

	/**
	 * <p>
	 * validateUser.
	 * </p>
	 *
	 * @param user a {@link br.com.m4rc310.gql.dto.MUser} object
	 * @return a boolean
	 */
	public boolean validateUser(MUser user) {
		return authUserProvider.isValidUser(user);
	}

	private MEnumToken getMEnumToken(HttpServletRequest request) {

		String authorizationHeader = request.getHeader(AUTHORIZATION);
		if (authorizationHeader == null) {
			return MEnumToken.NONE;
		}

		authorizationHeader = authorizationHeader.replace(AUTHORIZATION, "").trim();

		for (MEnumToken et : MEnumToken.values()) {
			if (authorizationHeader.startsWith(et.getDescription())) {
				return et;
			}
		}

		return MEnumToken.NONE;
	}

	private String getToken(HttpServletRequest request) {

		MEnumToken type = getMEnumToken(request);
		if (type.compareTo(MEnumToken.NONE) == 0) {
			return null;
		}

		String authorizationHeader = request.getHeader(AUTHORIZATION).replace(AUTHORIZATION, "");
		return authorizationHeader.replace(type.getDescription(), "").trim();
	}

	public MUser getMUser(MEnumToken type, String token) throws Exception {
		switch (type) {
		case TEST:
			int i = token.indexOf(":");
			String username = token.substring(0, i);
			String password = token.substring(i + 1);
			return authUserProvider.authUser(username, password);
		case BASIC:
		case BEARER:
		default:
			return null;
		}
	}

	public MUser getMUser(HttpServletRequest request) throws Exception {

		String token = getToken(request);
		MEnumToken type = getMEnumToken(request);
		return getMUser(type, token);
	}

}
