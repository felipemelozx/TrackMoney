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

  public String generateToken(String email) {
    try {
      Algorithm algorithm = Algorithm.HMAC256(secret);
      return JWT.create()
          .withIssuer("API-AUTH")
          .withClaim("roles", "USER_ROLES")
          .withSubject(email)
          .withExpiresAt(getExpires())
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

  private Instant getExpires() {
    return LocalDateTime.now()
        .plusHours(3)
        .toInstant(ZoneOffset.ofHours(-3));
  }
}