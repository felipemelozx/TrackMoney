package fun.trackmoney.auth.infra.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class JwtService {

  @Value("${api.secret.key}")
  private String secret;

  public String generateAccessToken(String email) {
    try {
      Algorithm algorithm = Algorithm.HMAC256(secret);
      return JWT.create()
          .withIssuer("API-AUTH")
          .withClaim("roles", "USER_ROLES")
          .withClaim("token_type", "ACCESS")
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
          .withIssuer("API-AUTH")
          .withClaim("roles", "USER_ROLES")
          .withClaim("token_type", "REFRESH")
          .withSubject(email)
          .withExpiresAt(getRefreshTokenExpiry())
          .sign(algorithm);
    } catch (Exception e) {
      throw new JWTCreationException("Error while generating JWT token.", e);
    }
  }

  public String validateToken(String token) {
    try {
      Algorithm algorithm = Algorithm.HMAC256(secret);
      return JWT.require(algorithm)
          .withIssuer("API-AUTH")
          .build()
          .verify(token)
          .getSubject();
    } catch (JWTVerificationException e) {
      return null;
    }
  }

  private Instant getAccessTokenExpiry() {
    return LocalDateTime.now()
        .plusMinutes(15)
        .toInstant(ZoneOffset.ofHours(-3));
  }

  private Instant getRefreshTokenExpiry() {
    return LocalDateTime.now()
        .plusDays(7)
        .toInstant(ZoneOffset.ofHours(-3));
  }

  public String generateVerificationToken(String email) {
    try {
      Algorithm algorithm = Algorithm.HMAC256(secret);
      return JWT.create()
          .withIssuer("API-AUTH")
          .withClaim("roles", "USER_UNVERIFIED")
          .withSubject(email)
          .withClaim("IsVerify", false)
          .withExpiresAt(getAccessTokenExpiry())
          .sign(algorithm);
    } catch (Exception e) {
      throw new JWTCreationException("Error while generating JWT token.", e);
    }
  }
}