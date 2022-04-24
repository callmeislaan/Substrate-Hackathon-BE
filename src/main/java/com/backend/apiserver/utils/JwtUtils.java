package com.backend.apiserver.utils;

import com.backend.apiserver.configuration.CommonProperties;
import com.backend.apiserver.entity.Status;
import com.backend.apiserver.entity.User;
import com.backend.apiserver.repository.UserRepository;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.Objects;

@Component
@AllArgsConstructor
public class JwtUtils {

	/**
	 * Logger
	 */
	private static final Logger LOG = LoggerFactory.getLogger(JwtUtils.class);

	/**
	 * UserRepository using to call DB
	 */
	private UserRepository userRepository;

	/**
	 * CommonProperties using to get data from .properties
	 */
	private CommonProperties commonProperties;

	/**
	 * HttpServletRequest using to get token
	 */
	private HttpServletRequest httpServletRequest;


	/**
	 * Using to generate JWT Token
	 *
	 * @param authentication
	 * @return
	 */
	public String generateToken(Authentication authentication) {

		LOG.info("Exact user principal from authentication");
		User user = userRepository.findByUsernameAndStatus(authentication.getName(), Status.ACTIVE);

		//Calculate expire time
		Date now = new Date();
		Date expireDate = new Date(now.getTime() + commonProperties.getExpiration());

		LOG.info("Start generate the jwt token with necessary information");
		String jwtToken = Jwts.builder()
				.setSubject(user.getUsername())
				.setIssuedAt(now)
				.setExpiration(expireDate)
				.claim(Constants.CLAIM_USER_ID, user.getId())
				.signWith(SignatureAlgorithm.HS512, commonProperties.getSecret())
				.compact();
		LOG.info("End generate the jwt token with necessary information");

		return jwtToken;
	}

	public String generateToken(String value) {
		Date now = new Date();
		return Jwts.builder()
				.setSubject(value)
				.setIssuedAt(now)
				.setExpiration(new Date(now.getTime() + Constants.ACTIVATION_EXPIRE_TIME))
				.signWith(SignatureAlgorithm.HS512, commonProperties.getSecret())
				.compact();
	}

	/**
	 * Using to exact username from JWT token
	 *
	 * @param token
	 * @return
	 */
	public String getUsername(String token) {
		LOG.info("Start to exact username from jwt web token string");
		String username = Jwts.parser()
				.setSigningKey(commonProperties.getSecret())
				.parseClaimsJws(token)
				.getBody()
				.getSubject();
		LOG.info("End to exact username from jwt web token string");
		return username;
	}

	/**
	 * Using to exact username from httpServletRequest
	 *
	 * @return
	 */
	public String getUsername() {
		LOG.info("Start to parse jwt token from request");
		String token = parseJwt(httpServletRequest);
		LOG.info("End parse jwt token from request");

		LOG.info("Start to exact userId from jwt web token string");
		String username = Jwts.parser()
				.setSigningKey(commonProperties.getSecret())
				.parseClaimsJws(token)
				.getBody()
				.getSubject();
		LOG.info("End to exact userId from jwt web token string");
		return username;
	}

	/**
	 * Using to exact userid from httpServletRequest
	 *
	 * @return
	 */
	public Long getUserId() {
		LOG.info("Start to parse jwt token from request");
		String token = parseJwt(httpServletRequest);
		if (Objects.isNull(token)) return null;
		LOG.info("End parse jwt token from request");

		LOG.info("Start to exact userId from jwt web token string");
		String userId = Jwts.parser()
				.setSigningKey(commonProperties.getSecret())
				.parseClaimsJws(token)
				.getBody()
				.get(Constants.CLAIM_USER_ID)
				.toString();
		LOG.info("End to exact userId from jwt web token string");
		return Long.parseLong(userId);
	}

	public boolean hasJsonWebToken() {
		String bearerToken = httpServletRequest.getHeader("Authorization");
		// Checking whether the request has JWT token
		if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
			return true;
		}
		return false;
	}

	/**
	 * Validate JWT Token
	 *
	 * @param authToken
	 * @return
	 */
	public boolean validateToken(String authToken) {
		try {
			LOG.info("Start to validate json web token string");
			Jwts.parser()
					.setSigningKey(commonProperties.getSecret())
					.parseClaimsJws(authToken);
			LOG.info("Json web token string validated");
			return true;
		} catch (SignatureException e) {
			LOG.error("Invalid JWT signature: ", e.getMessage());
		} catch (MalformedJwtException e) {
			LOG.error("Invalid JWT token: ", e.getMessage());
		} catch (ExpiredJwtException e) {
			LOG.error("JWT token is expired: ", e.getMessage());
		} catch (UnsupportedJwtException e) {
			LOG.error("JWT token is unsupported: ", e.getMessage());
		} catch (IllegalArgumentException e) {
			LOG.error("JWT claims string is empty: ", e.getMessage());
		}

		return false;
	}

	public String parseJwt(HttpServletRequest request) {
		String bearerToken = request.getHeader("Authorization");
		// Checking whether the request has JWT token
		if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
			return bearerToken.substring(7);
		}
		return null;
	}
}