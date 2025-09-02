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
    String subject = jwtService.extractEmail(invalidToken);
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

  @Test
  void shouldReturnRolesWhenTokenIsValid() {
    String email = "roles@example.com";
    String token = jwtService.generateAccessToken(email);
    String role = jwtService.extractRole(token);
    assertNotNull(role);
    assertEquals("USER_ROLES", role);
  }

  @Test
  void shouldReturnNullWhenExtractRolesWithInvalidToken() {
    String invalidToken = "invalid.token.here";
    String roles = jwtService.extractRole(invalidToken);
    assertNull(roles);
  }

  @Test
  void shouldReturnTrueForAccessToken() {
    String email = "access@example.com";
    String token = jwtService.generateAccessToken(email);
    assertTrue(jwtService.isAccessToken(token));
    assertFalse(jwtService.isRefreshToken(token));
  }

  @Test
  void shouldReturnTrueForRefreshToken() {
    String email = "refresh@example.com";
    String token = jwtService.generateRefreshToken(email);
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
    String invalidToken = "this.is.not.a.jwt";
    String decoded = ReflectionTestUtils.invokeMethod(jwtService, "validateToken", invalidToken);
    assertNull(decoded);
  }
}
