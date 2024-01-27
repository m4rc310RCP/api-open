package br.com.m4rc310.graphql.security;

import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;

import br.com.m4rc310.graphql.security.dto.MUser;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

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
	
	private static final String KEY_AUTH = "authorities";
	
	
	public boolean isTokenExpirate(String token) {
		try {			
			Date exp = extractClaim(token, Claims::getExpiration);
			return exp.before(new Date());
		} catch (Exception e) {
			return false;
		}
	}
	
	public String generateToken(MUser user) {
		Map<String, Object> claim = new HashMap<>();
		return createToken(claim, user);
	}
	
	public String extractUsername(String token) {
		return extractClaim(token, Claims::getSubject);
	}
	
	public void validateToken(String token) throws Exception {
		assertMath(isTokenExpirate(token), "Token expirado!");
	}
	
	public <T> T extractClaim(String token, Function<Claims, T> resolver) {
		Claims claims =  Jwts.parser().setSigningKey(jwtSigningKey).parseClaimsJws(token).getBody();
		return resolver.apply(claims);
	}
	
	private void assertMath(boolean math, String message, Object...args) throws Exception {
		if(!math) {
			throw new Exception(message);
		}
	}
	
	private String createToken(Map<String,Object> claims, UserDetails details) {
		Date now = Date.from(Instant.now());
//		Date exp = new Date(now.getTime() + expiration);
		Date exp = new Date(now.getTime());
		
		JwtBuilder ret = Jwts.builder();
		ret.setClaims(claims);
		ret.setSubject(details.getUsername());
		ret.claim(KEY_AUTH, details.getAuthorities());
		ret.setIssuedAt(now);
		ret.setExpiration(exp);
		ret.signWith(SignatureAlgorithm.HS256, jwtSigningKey);
		
		return ret.compact();
	}
	
	
	
}
