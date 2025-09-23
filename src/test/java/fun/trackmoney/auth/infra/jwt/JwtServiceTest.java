package fun.trackmoney.auth.infra.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Instant;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class JwtServiceTest {

  @InjectMocks
  @Spy
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
    assertEquals("trackmoney", decoded.getIssuer());
    assertTrue(decoded.getExpiresAt().after(new Date()));
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
    assertEquals("trackmoney", decoded.getIssuer());
    assertTrue(decoded.getExpiresAt().after(new Date()));
  }

  @Test
  void shouldGenerateResetPasswordTokenWhenEmailIsValid() {
    String email = "reset@example.com";
    String token = jwtService.generateResetPasswordToken(email);
    assertNotNull(token);

    DecodedJWT decoded = JWT.decode(token);
    assertEquals(email, decoded.getSubject());
    assertEquals("RESET_PASSWORD", decoded.getClaim("roles").asString());
    assertEquals("ACCESS", decoded.getClaim("token_type").asString());
    assertEquals("trackmoney", decoded.getIssuer());
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
    assertFalse(decoded.getClaim("IsVerify").asBoolean());
    assertEquals("trackmoney", decoded.getIssuer());
    assertTrue(decoded.getExpiresAt().after(new Date()));
  }

  @Test
  void verificationTokenShouldNotContainTokenType() {
    String email = "verify2@example.com";
    String token = jwtService.generateVerificationToken(email);
    DecodedJWT decoded = JWT.decode(token);
    assertNull(decoded.getClaim("token_type").asString());
  }

  @Test
  void shouldThrowJWTCreationExceptionWhenSecretIsNull() {
    ReflectionTestUtils.setField(jwtService, "secret", null);
    assertThrows(JWTCreationException.class, () -> jwtService.generateAccessToken("fail@example.com"));
  }

  @Test
  void shouldThrowJWTCreationExceptionWhenSecretIsEmpty() {
    ReflectionTestUtils.setField(jwtService, "secret", "");
    assertThrows(JWTCreationException.class, () -> jwtService.generateAccessToken("fail@example.com"));
  }

  @Test
  void shouldThrowJWTCreationExceptionWhenGeneratingResetPasswordTokenAndSecretInvalid() {
    ReflectionTestUtils.setField(jwtService, "secret", null);
    assertThrows(JWTCreationException.class, () -> jwtService.generateResetPasswordToken("fail@example.com"));
  }

  @Test
  void shouldThrowJWTCreationExceptionWhenGeneratingRefreshTokenAndSecretInvalid() {
    ReflectionTestUtils.setField(jwtService, "secret", null);
    assertThrows(JWTCreationException.class, () -> jwtService.generateRefreshToken("fail@example.com"));
  }

  @Test
  void shouldThrowJWTCreationExceptionWhenGeneratingVerificationTokenAndSecretInvalid() {
    ReflectionTestUtils.setField(jwtService, "secret", null);
    assertThrows(JWTCreationException.class, () -> jwtService.generateVerificationToken("fail@example.com"));
  }

  @Test
  void shouldReturnSubjectWhenTokenIsValid() {
    String email = "subject@example.com";
    String token = jwtService.generateAccessToken(email);
    String subject = jwtService.extractEmail(token);
    assertEquals(email, subject);
  }

  @Test
  void shouldReturnNullWhenTokenIsInvalid() {
    assertNull(jwtService.extractEmail("invalid.jwt.token"));
  }

  @Test
  void shouldReturnRolesWhenTokenIsValid() {
    String email = "roles@example.com";
    String token = jwtService.generateAccessToken(email);
    String role = jwtService.extractRole(token);
    assertEquals("USER_ROLES", role);
  }

  @Test
  void shouldReturnNullWhenExtractRolesWithInvalidToken() {
    assertNull(jwtService.extractRole("invalid.token.here"));
  }

  @Test
  void shouldReturnTrueForAccessToken() {
    String token = jwtService.generateAccessToken("access@example.com");
    assertTrue(jwtService.isAccessToken(token));
    assertFalse(jwtService.isRefreshToken(token));
  }

  @Test
  void shouldReturnTrueForRefreshToken() {
    String token = jwtService.generateRefreshToken("refresh@example.com");
    assertTrue(jwtService.isRefreshToken(token));
    assertFalse(jwtService.isAccessToken(token));
  }

  @Test
  void shouldReturnFalseForInvalidTokenTypes() {
    String invalidToken = "invalid.jwt.token";
    assertFalse(jwtService.isAccessToken(invalidToken));
    assertFalse(jwtService.isRefreshToken(invalidToken));
  }


  @Test
  void validateTokenShouldReturnDecodedJWTWhenTokenIsValid() {
    String email = "valid@example.com";
    String token = jwtService.generateAccessToken(email);
    DecodedJWT decoded = ReflectionTestUtils.invokeMethod(jwtService, "validateToken", token);
    assertNotNull(decoded);
    assertEquals(email, decoded.getSubject());
    assertEquals("ACCESS", decoded.getClaim("token_type").asString());
    assertEquals("USER_ROLES", decoded.getClaim("roles").asString());
    assertEquals("trackmoney", decoded.getIssuer());
  }

  @Test
  void validateTokenShouldReturnNullWhenTokenIsInvalid() {
    assertNull(ReflectionTestUtils.invokeMethod(jwtService, "validateToken", "not.a.valid.jwt"));
  }

  @Test
  void getAccessTokenExpiryShouldReturnInstant15MinutesAhead() {
    Instant now = Instant.now();
    Instant expiry = ReflectionTestUtils.invokeMethod(jwtService, "getAccessTokenExpiry");
    long diffSeconds = expiry.getEpochSecond() - now.getEpochSecond();
    assertTrue(diffSeconds >= 14 * 60 && diffSeconds <= 16 * 60);
  }

  @Test
  void getRestPasswordExpiryShouldReturnInstant30MinutesAhead() {
    Instant now = Instant.now();
    Instant expiry = ReflectionTestUtils.invokeMethod(jwtService, "getRestPasswordExpiry");
    long diffSeconds = expiry.getEpochSecond() - now.getEpochSecond();
    assertTrue(diffSeconds >= 29 * 60 && diffSeconds <= 31 * 60);
  }

  @Test
  void getRefreshTokenExpiryShouldReturnInstant7DaysAhead() {
    Instant now = Instant.now();
    Instant expiry = ReflectionTestUtils.invokeMethod(jwtService, "getRefreshTokenExpiry");
    long diffSeconds = expiry.getEpochSecond() - now.getEpochSecond();
    assertTrue(diffSeconds >= 6 * 24 * 3600 && diffSeconds <= 8 * 24 * 3600);
  }
}
