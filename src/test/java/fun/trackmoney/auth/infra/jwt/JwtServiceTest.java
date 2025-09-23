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
  void shouldReturnSubjectWhenTokenIsValid() {
    String email = "test@example.com";
    String token = jwtService.generateAccessToken(email);
    String subject = jwtService.extractEmail(token);
    assertEquals(email, subject);
  }

  @Test
  void shouldReturnNullWhenTokenIsInvalid() {
    String invalidToken = "invalid.jwt.token";
    assertNull(jwtService.extractEmail(invalidToken));
  }

  @Test
  void shouldThrowJWTCreationExceptionWhenSecretIsNull() {
    ReflectionTestUtils.setField(jwtService, "secret", null);
    assertThrows(JWTCreationException.class, () -> jwtService.generateAccessToken("test@example.com"));
  }

  @Test
  void shouldThrowJWTCreationExceptionWhenSecretIsEmpty() {
    ReflectionTestUtils.setField(jwtService, "secret", "");
    assertThrows(JWTCreationException.class, () -> jwtService.generateAccessToken("test@example.com"));
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
  void validateTokenShouldReturnNullForInvalidToken() {
    assertNull(ReflectionTestUtils.invokeMethod(jwtService, "validateToken", "this.is.not.a.jwt"));
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
  void shouldThrowJWTCreationExceptionWhenGeneratingResetPasswordTokenAndSecretIsNull() {
    ReflectionTestUtils.setField(jwtService, "secret", null);
    assertThrows(JWTCreationException.class, () -> jwtService.generateResetPasswordToken("fail@example.com"));
  }

  @Test
  void shouldThrowJWTCreationExceptionWhenGeneratingResetPasswordTokenAndSecretIsEmpty() {
    ReflectionTestUtils.setField(jwtService, "secret", "");
    assertThrows(JWTCreationException.class, () -> jwtService.generateResetPasswordToken("fail@example.com"));
  }

  @Test
  void validateTokenShouldReturnDecodedJWTWhenTokenIsValid() {
    String email = "valid@example.com";
    String token = jwtService.generateAccessToken(email);

    DecodedJWT decoded = ReflectionTestUtils.invokeMethod(jwtService, "validateToken", token);

    assertNotNull(decoded);
    assertEquals(email, decoded.getSubject());
    assertEquals("trackmoney", decoded.getIssuer());
    assertEquals("ACCESS", decoded.getClaim("token_type").asString());
    assertEquals("USER_ROLES", decoded.getClaim("roles").asString());
  }

  @Test
  void validateTokenShouldReturnNullWhenTokenIsInvalid() {
    String invalidToken = "not.a.valid.jwt";
    DecodedJWT decoded = ReflectionTestUtils.invokeMethod(jwtService, "validateToken", invalidToken);
    assertNull(decoded);
  }

  @Test
  void getAccessTokenExpiryShouldReturnInstant15MinutesAhead() {
    Instant before = Instant.now().plusSeconds(14 * 60);
    Instant expiry = ReflectionTestUtils.invokeMethod(jwtService, "getAccessTokenExpiry");
    Instant after = Instant.now().plusSeconds(16 * 60);

    assertTrue(expiry.isAfter(before));
    assertTrue(expiry.isBefore(after));
  }

  @Test
  void getRestPasswordExpiryShouldReturnInstant15MinutesAhead() {
    Instant before = Instant.now().plusSeconds(14 * 60);
    Instant expiry = ReflectionTestUtils.invokeMethod(jwtService, "getRestPasswordExpiry");
    Instant after = Instant.now().plusSeconds(30 * 60);

    assertTrue(expiry.isAfter(before));
    assertTrue(expiry.isBefore(after));
  }

  @Test
  void generateAccessTokenShouldThrowJWTCreationExceptionWhenSecretInvalid() {
    ReflectionTestUtils.setField(jwtService, "secret", null);
    assertThrows(JWTCreationException.class, () -> jwtService.generateAccessToken("fail@example.com"));
  }

  @Test
  void generateRefreshTokenShouldThrowJWTCreationExceptionWhenSecretInvalid() {
    ReflectionTestUtils.setField(jwtService, "secret", null);
    assertThrows(JWTCreationException.class, () -> jwtService.generateRefreshToken("fail@example.com"));
  }

  @Test
  void generateResetPasswordTokenShouldThrowJWTCreationExceptionWhenSecretInvalid() {
    ReflectionTestUtils.setField(jwtService, "secret", null);
    assertThrows(JWTCreationException.class, () -> jwtService.generateResetPasswordToken("fail@example.com"));
  }

  @Test
  void generateVerificationTokenShouldThrowJWTCreationExceptionWhenSecretInvalid() {
    ReflectionTestUtils.setField(jwtService, "secret", null);
    assertThrows(JWTCreationException.class, () -> jwtService.generateVerificationToken("fail@example.com"));
  }

}
