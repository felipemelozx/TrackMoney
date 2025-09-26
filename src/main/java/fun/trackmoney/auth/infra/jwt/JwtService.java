package fun.trackmoney.auth.infra.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import java.time.Instant;

import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class JwtService {

  @Value("${api.secret.key}")
  private String secret;

  private static final String CLAIM_ROLES = "roles";
  private static final String ISSUER = "trackmoney";

  private static final String REFRESH = "REFRESH";

  public String generateAccessToken(String email) {
    try {
      Algorithm algorithm = Algorithm.HMAC256(secret);
      return JWT.create()
          .withIssuer(ISSUER)
          .withClaim(CLAIM_ROLES, "USER_ROLES")
          .withSubject(email)
          .withExpiresAt(getAccessTokenExpiry())
          .sign(algorithm);
    } catch (Exception e) {
      throw new JWTCreationException("Error while generating JWT token.", e);
    }
  }

  public String generateRefreshToken(String email) {
    try {
      Algorithm algorithm = Algorithm.HMAC256(secret);
      return JWT.create()
          .withIssuer(ISSUER)
          .withClaim(CLAIM_ROLES, REFRESH)
          .withSubject(email)
          .withExpiresAt(getRefreshTokenExpiry())
          .sign(algorithm);
    } catch (Exception e) {
      throw new JWTCreationException("Error while generating JWT token.", e);
    }
  }

  public String generateResetPasswordToken(String email) {
    try {
      Algorithm algorithm = Algorithm.HMAC256(secret);
      return JWT.create()
          .withIssuer(ISSUER)
          .withClaim(CLAIM_ROLES, "RESET_PASSWORD")
          .withSubject(email)
          .withExpiresAt(getRestPasswordExpiry())
          .sign(algorithm);
    } catch (Exception e) {
      throw new JWTCreationException("Error while generating JWT token.", e);
    }
  }

  protected DecodedJWT validateToken(String token) {
    try {
      Algorithm algorithm = Algorithm.HMAC256(secret);
      return JWT.require(algorithm)
          .withIssuer(ISSUER)
          .build()
          .verify(token);
    } catch (JWTVerificationException e) {
      return null;
    }
  }

  protected Instant getAccessTokenExpiry() {
    return Instant.now().plusSeconds(15L * 60);
  }

  protected Instant getRestPasswordExpiry() {
    return Instant.now().plusSeconds(30L * 60);
  }

  protected Instant getRefreshTokenExpiry() {
    return Instant.now().plusSeconds(7L * 24 * 3600);
  }

  public String generateVerificationToken(String email) {
    try {
      Algorithm algorithm = Algorithm.HMAC256(secret);
      return JWT.create()
          .withIssuer(ISSUER)
          .withClaim(CLAIM_ROLES, "USER_UNVERIFIED")
          .withSubject(email)
          .withClaim("IsVerify", false)
          .withExpiresAt(getAccessTokenExpiry())
          .sign(algorithm);
    } catch (Exception e) {
      throw new JWTCreationException("Error while generating JWT token.", e);
    }
  }

  public String extractEmail(String token) {
    DecodedJWT jwt = validateToken(token);
    return jwt != null ? jwt.getSubject() : null;
  }

  public String extractRole(String token) {
    DecodedJWT jwt = validateToken(token);
    return jwt != null ? jwt.getClaim(CLAIM_ROLES).asString() : null;
  }
}