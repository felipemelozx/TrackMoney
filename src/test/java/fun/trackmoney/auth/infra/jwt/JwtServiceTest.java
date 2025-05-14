package fun.trackmoney.auth.infra.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.util.ReflectionTestUtils;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;
import static org.junit.jupiter.api.Assertions.*;


class JwtServiceTest {

  @InjectMocks
  private JwtService jwtService;

  @Value("${api.secret.key}")
  private String secretKey = "testesecretkey123";

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    ReflectionTestUtils.setField(jwtService, "secret", secretKey);
  }

  @Test
  void generateTokenShouldReturnAValidJWT() {
    String email = "test@example.com";
    String token = jwtService.generateToken(email);
    assertNotNull(token);
  }

  @Test
  void validateTokenShouldReturnSubjectForAValidToken() {
    String email = "test@example.com";
    String token = jwtService.generateToken(email);
    String subject = jwtService.validateToken(token);
    assertEquals(email, subject);
  }
  @Test
  void validateTokenShouldReturnNullForAnInvalidToken() {
    String invalidToken = "invalid.jwt.token";
    String subject = jwtService.validateToken(invalidToken);
    assertNull(subject);
  }

  @Test
  void validateTokenShouldReturnNullForAnExpiredToken() {
    ReflectionTestUtils.setField(jwtService, "secret", "shortlivedsecret");
    JwtService shortLivedJwtService = new JwtService();
    ReflectionTestUtils.setField(shortLivedJwtService, "secret", "shortlivedsecret");

    String email = "expired@example.com";
    Algorithm algorithm = Algorithm.HMAC256("shortlivedsecret");
    String token = JWT.create()
        .withIssuer("API-AUTH")
        .withClaim("roles", "USER_ROLES")
        .withSubject(email)
        .withExpiresAt(Date.from(LocalDateTime.now().plusSeconds(1).toInstant(ZoneOffset.ofHours(-3))))
        .sign(algorithm);


    ReflectionTestUtils.setField(jwtService, "secret", secretKey);
    String subject = jwtService.validateToken(token);
    assertNull(subject);
  }

  @Test
  void generateTokenShouldThrowJWTCreationExceptionWhenSecretIsNull() {
    ReflectionTestUtils.setField(jwtService, "secret", null);
    String email = "test@example.com";
    assertThrows(JWTCreationException.class, () -> jwtService.generateToken(email), "Error while generating JWT token.");
  }

  @Test
  void generateTokenShouldThrowJWTCreationExceptionWhenSecretIsInvalid() {
    ReflectionTestUtils.setField(jwtService, "secret", "");
    String email = "test@example.com";
    assertThrows(JWTCreationException.class, () -> jwtService.generateToken(email), "Error while generating JWT token.");
    ReflectionTestUtils.setField(jwtService, "secret", "testesecretkey123"); // Restore for other tests if needed
  }
}
