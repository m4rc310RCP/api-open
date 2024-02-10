package br.com.m4rc310.gql.jwt;

import java.time.Instant;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import javax.crypto.Cipher;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

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
	
	@Autowired
	private PasswordEncoder encoder;

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
			password= encoder.encode(password);
			return authUserProvider.authUser(username, password);
		case BASIC:
			String sbasic = decrypt(token);
			i = sbasic.indexOf(":");
			username = sbasic.substring(0, i);
			password = sbasic.substring(i + 1);
			return authUserProvider.authUser(username, password);
		case BEARER:
			if (isTokenExpirate(token)) {
				throw new Exception("Token as expiraded.");
			}
			username = extractUsername(token);
			return authUserProvider.getUserFromUsername(username);
		default:
			return null;
		}
	}

	public MUser getMUser(HttpServletRequest request) throws Exception {
		String token = getToken(request);
		MEnumToken type = getMEnumToken(request);
		return getMUser(type, token);
	}
	
	/**
	 * Encrypt.
	 *
	 * @param text the text
	 * @return the string
	 * @throws Exception the exception
	 */
	public String encrypt(String text) throws Exception {
		return encrypt(text, jwtSigningKey);
	}

	/**
	 * Encrypt.
	 *
	 * @param text the text
	 * @param key  the key
	 * @return the string
	 * @throws Exception the exception
	 */
	public String encrypt(String text, String key) throws Exception {
		byte[] keyByte = getKeyByte(key);
		SecretKeySpec keyAES = new SecretKeySpec(keyByte, "AES");
		Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
		cipher.init(Cipher.ENCRYPT_MODE, keyAES);
		return Base64.getEncoder().encodeToString(cipher.doFinal(text.getBytes()));
	}

	/**
	 * Decrypt.
	 *
	 * @param text the text
	 * @return the string
	 * @throws Exception the exception
	 */
	public String decrypt(String text) throws Exception {
		return decrypt(text, jwtSigningKey);
	}

	/**
	 * Decrypt.
	 *
	 * @param text the text
	 * @param key  the key
	 * @return the string
	 * @throws Exception the exception
	 */
	public String decrypt(String text, String key) throws Exception {		
		byte[] keyByte = getKeyByte(key);
		byte[] textDecoded = Base64.getDecoder().decode(text);
		
		SecretKeySpec keyAES = new SecretKeySpec(keyByte, "AES");
		Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
		cipher.init(Cipher.DECRYPT_MODE, keyAES);
		byte[] textoDescriptografado = cipher.doFinal(textDecoded);
		return new String(textoDescriptografado);
	}

	/**
	 * Gets the key byte.
	 *
	 * @return the key byte
	 * @throws Exception the exception
	 */
	public byte[] getKeyByte() throws Exception {
		return getKeyByte(jwtSigningKey);
	}

	/**
	 * Gets the key byte.
	 *
	 * @param skey the skey
	 * @return the key byte
	 * @throws Exception the exception
	 */
	public byte[] getKeyByte(String skey) throws Exception {
		jwtSigningKey = skey;
		//jwtSalt = "m4rc310";
		//iterationCount = 10000;
		//keyLength = 128;

		PBEKeySpec spec = new PBEKeySpec(jwtSigningKey.toCharArray(), jwtSalt.getBytes(), iterationCount, keyLength);
		SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
		return factory.generateSecret(spec).getEncoded();
	}

}
