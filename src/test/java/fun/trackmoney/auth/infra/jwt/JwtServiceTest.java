package fun.trackmoney.auth.infra.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class JwtServiceTest {

  @InjectMocks
  private JwtService jwtService;

  private final String secretKey = "testesecretkey123";

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    ReflectionTestUtils.setField(jwtService, "secret", secretKey);
  }

  @Test
  void shouldGenerateAccessTokenWhenEmailIsValid() {
    String email = "test@example.com";
    String token = jwtService.generateAccessToken(email);
    assertNotNull(token);

    DecodedJWT decoded = JWT.decode(token);
    assertEquals(email, decoded.getSubject());
    assertEquals("ACCESS", decoded.getClaim("token_type").asString());
    assertEquals("USER_ROLES", decoded.getClaim("roles").asString());
    assertTrue(decoded.getExpiresAt().after(new Date()));
  }

  @Test
  void shouldReturnSubjectWhenTokenIsValid() {
    String email = "test@example.com";
    String token = jwtService.generateAccessToken(email);
    String subject = jwtService.validateToken(token);
    assertEquals(email, subject);
  }

  @Test
  void shouldReturnNullWhenTokenIsInvalid() {
    String invalidToken = "invalid.jwt.token";
    String subject = jwtService.validateToken(invalidToken);
    assertNull(subject);
  }

  @Test
  void shouldReturnNullWhenTokenIsExpired() throws InterruptedException {
    String shortSecret = "shortlivedsecret";
    ReflectionTestUtils.setField(jwtService, "secret", shortSecret);

    String email = "expired@example.com";
    Algorithm algorithm = Algorithm.HMAC256(shortSecret);
    String token = JWT.create()
        .withIssuer("API-AUTH")
        .withClaim("roles", "USER_ROLES")
        .withSubject(email)
        .withExpiresAt(Date.from(LocalDateTime.now().plusSeconds(1).toInstant(ZoneOffset.ofHours(-3))))
        .sign(algorithm);

    Thread.sleep(1500);
    String subject = jwtService.validateToken(token);

    ReflectionTestUtils.setField(jwtService, "secret", secretKey);
    assertNull(subject);
  }

  @Test
  void shouldThrowJWTCreationExceptionWhenGeneratingAccessTokenAndSecretIsNull() {
    ReflectionTestUtils.setField(jwtService, "secret", null);
    String email = "test@example.com";
    assertThrows(JWTCreationException.class, () -> jwtService.generateAccessToken(email));
  }

  @Test
  void shouldThrowJWTCreationExceptionWhenGeneratingAccessTokenAndSecretIsEmpty() {
    ReflectionTestUtils.setField(jwtService, "secret", "");
    String email = "test@example.com";
    assertThrows(JWTCreationException.class, () -> jwtService.generateAccessToken(email));
    ReflectionTestUtils.setField(jwtService, "secret", secretKey);
  }

  @Test
  void shouldGenerateRefreshTokenWhenEmailIsValid() {
    String email = "refresh@example.com";
    String token = jwtService.generateRefreshToken(email);
    assertNotNull(token);

    DecodedJWT decoded = JWT.decode(token);
    assertEquals(email, decoded.getSubject());
    assertEquals("REFRESH", decoded.getClaim("token_type").asString());
    assertEquals("USER_ROLES", decoded.getClaim("roles").asString());
    assertTrue(decoded.getExpiresAt().after(new Date()));
  }

  @Test
  void shouldGenerateVerificationTokenWhenEmailIsValid() {
    String email = "verify@example.com";
    String token = jwtService.generateVerificationToken(email);
    assertNotNull(token);

    DecodedJWT decoded = JWT.decode(token);
    assertEquals(email, decoded.getSubject());
    assertEquals("USER_UNVERIFIED", decoded.getClaim("roles").asString());
    assertEquals(false, decoded.getClaim("IsVerify").asBoolean());
    assertTrue(decoded.getExpiresAt().after(new Date()));
  }

  @Test
  void shouldThrowJWTCreationExceptionWhenGeneratingRefreshTokenAndSecretIsNull() {
    ReflectionTestUtils.setField(jwtService, "secret", null);
    String email = "fail@example.com";
    assertThrows(JWTCreationException.class, () -> jwtService.generateRefreshToken(email));
  }

  @Test
  void shouldThrowJWTCreationExceptionWhenGeneratingVerificationTokenAndSecretIsNull() {
    ReflectionTestUtils.setField(jwtService, "secret", null);
    String email = "fail@example.com";
    assertThrows(JWTCreationException.class, () -> jwtService.generateVerificationToken(email));
  }
}
