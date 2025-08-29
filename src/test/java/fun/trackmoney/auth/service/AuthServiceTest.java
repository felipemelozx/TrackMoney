package fun.trackmoney.auth.service;

import fun.trackmoney.auth.dto.internal.UserRegisterResult;
import fun.trackmoney.auth.dto.internal.UserRegisterSuccess;
import fun.trackmoney.auth.infra.jwt.JwtService;
import fun.trackmoney.email.EmailService;
import fun.trackmoney.user.dtos.UserRequestDTO;
import fun.trackmoney.user.dtos.UserResponseDTO;
import fun.trackmoney.user.service.UserService;
import jakarta.mail.MessagingException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

  @Mock
  private PasswordEncoder passwordEncoder;

  @Mock
  private UserService userService;

  @Mock
  private JwtService jwtService;

  @Mock
  private EmailService emailService;

  @Mock
  private Cache cache;

  @Mock
  private Cache.ValueWrapper valueWrapper;

  @Mock
  private CacheManager cacheManager;

  @InjectMocks
  @Spy
  private AuthService authService;

  @Test
  void shouldRegisterUserSuccessfully() throws MessagingException {
    UserRequestDTO registerDto = new UserRequestDTO("John Doe", "test@example.com", "Password1#");
    UserResponseDTO responseDTO = new UserResponseDTO(UUID.randomUUID(), "John Doe", "test@example.com");
    UserRegisterSuccess userRegisterSuccess = new UserRegisterSuccess(responseDTO);
    Integer code = 1234;

    when(userService.register(registerDto)).thenReturn(userRegisterSuccess);
    when(authService.generateVerificationCode()).thenReturn(code);
    when(authService.saveCode(code, responseDTO.email())).thenReturn(true);
    doNothing().when(emailService)
        .sendEmailToVerifyEmail(registerDto.email(), registerDto.name(), code);

    UserRegisterResult actualResponse = authService.register(registerDto);

    assertInstanceOf(UserRegisterSuccess.class, actualResponse);
  }

/*  @Test
  void shouldLoginSuccessfullyWhenCredentialsAreValid() {
    // Arrange
    LoginRequestDTO loginDto = new LoginRequestDTO("test@example.com", "Password1#");
    UUID userId = UUID.randomUUID();
    UserEntity user = new UserEntity(userId, "John Doe", "test@example.com", "encodedPassword");
    String expectedToken = "mocked-jwt-token";

    when(userService.findUserByEmail(loginDto)).thenReturn(user);
    when(passwordEncoder.matches(loginDto.password(), user.getPassword())).thenReturn(true);
    when(jwtService.generateToken(user.getEmail())).thenReturn(expectedToken);

    // Act
    LoginResponseDTO response = authService.login(loginDto);

    // Assert
    assertEquals(expectedToken, response.token());
  }*/

 /* @Test
  void shouldThrowLoginExceptionWhenPasswordDoesNotMatch() {
    // Arrange
    LoginRequestDTO loginDto = new LoginRequestDTO("test@example.com", "WrongPassword");
    UserEntity user = new UserEntity(UUID.randomUUID(), "John Doe", "test@example.com", "encodedPassword");

    when(userService.findUserByEmail(loginDto)).thenReturn(user);
    when(passwordEncoder.matches(loginDto.password(), user.getPassword())).thenReturn(false);

    // Act & Assert
    assertThrows(LoginException.class, () -> authService.login(loginDto));
  }*/
}
