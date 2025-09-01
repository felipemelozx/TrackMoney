package fun.trackmoney.auth.service;

import fun.trackmoney.auth.dto.LoginRequestDTO;
import fun.trackmoney.auth.dto.LoginResponseDTO;
import fun.trackmoney.auth.dto.internal.AuthError;
import fun.trackmoney.auth.dto.internal.LoginFailure;
import fun.trackmoney.auth.dto.internal.LoginResult;
import fun.trackmoney.auth.dto.internal.LoginSuccess;
import fun.trackmoney.auth.dto.internal.UserRegisterFailure;
import fun.trackmoney.auth.dto.internal.UserRegisterResult;
import fun.trackmoney.auth.dto.internal.UserRegisterSuccess;
import fun.trackmoney.auth.infra.jwt.JwtService;
import fun.trackmoney.email.EmailService;
import fun.trackmoney.user.dtos.UserRequestDTO;
import fun.trackmoney.user.dtos.UserResponseDTO;
import fun.trackmoney.user.entity.UserEntity;
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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.security.SecureRandom;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

  @Mock
  private SecureRandom secureRandom;

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
  void shouldRegisterUserAndSendVerificationEmail_whenUserRequestIsValid() throws MessagingException {
    UserRequestDTO registerDto = new UserRequestDTO("John Doe", "test@example.com", "Password1#");
    UserResponseDTO responseDTO = new UserResponseDTO(UUID.randomUUID(), "John Doe", "test@example.com");
    UserRegisterSuccess userRegisterSuccess = new UserRegisterSuccess(responseDTO);
    Integer code = 1234;

    when(userService.register(registerDto)).thenReturn(userRegisterSuccess);
    doReturn(code).when(authService).generateVerificationCode();
    doReturn(true).when(authService).saveCode(code, registerDto.email());
    doNothing().when(emailService)
        .sendEmailToVerifyEmail(registerDto.email(), registerDto.name(), code);

    UserRegisterResult actualResponse = authService.register(registerDto);

    assertInstanceOf(UserRegisterSuccess.class, actualResponse);
    verify(authService, times(1)).saveCode(code, responseDTO.email());
    verify(emailService, times(1)).sendEmailToVerifyEmail(responseDTO.email(), responseDTO.name(), code);
    verify(authService, times(1)).generateVerificationCode();
    assertEquals(responseDTO, ((UserRegisterSuccess) actualResponse).user());
  }

  @Test
  void shouldRetryGeneratingVerificationCode_whenCodeSavingFails() throws MessagingException {
    UserRequestDTO registerDto = new UserRequestDTO("John Doe", "test@example.com", "Password1#");
    UserResponseDTO responseDTO = new UserResponseDTO(UUID.randomUUID(), "John Doe", "test@example.com");
    UserRegisterSuccess userRegisterSuccess = new UserRegisterSuccess(responseDTO);
    Integer code = 1234;

    when(userService.register(registerDto)).thenReturn(userRegisterSuccess);
    doReturn(code).when(authService).generateVerificationCode();
    doReturn(false,true).when(authService).saveCode(code, registerDto.email());
    doNothing().when(emailService)
        .sendEmailToVerifyEmail(registerDto.email(), registerDto.name(), code);

    UserRegisterResult actualResponse = authService.register(registerDto);

    assertInstanceOf(UserRegisterSuccess.class, actualResponse);
    verify(authService, times(2)).saveCode(code, responseDTO.email());
    verify(emailService, times(1)).sendEmailToVerifyEmail(responseDTO.email(), responseDTO.name(), code);
    verify(authService, times(2)).generateVerificationCode();
    assertEquals(responseDTO, ((UserRegisterSuccess) actualResponse).user());
  }

  @Test
  void shouldReturnSuccessfulRegistration_whenEmailSendingFails() throws MessagingException, IOException {
    UserRequestDTO registerDto = new UserRequestDTO("John Doe", "test@example.com", "Password1#");
    UserResponseDTO responseDTO = new UserResponseDTO(UUID.randomUUID(), "John Doe", "test@example.com");
    UserRegisterSuccess userRegisterSuccess = new UserRegisterSuccess(responseDTO);
    Integer code = 1234;

    when(userService.register(registerDto)).thenReturn(userRegisterSuccess);
    doReturn(code).when(authService).generateVerificationCode();
    doReturn(true).when(authService).saveCode(code, registerDto.email());
    doThrow(new MessagingException()).when(emailService)
        .sendEmailToVerifyEmail(registerDto.email(), registerDto.name(), code);

    PrintStream out = System.out;
    String outPut;
    UserRegisterResult actualResponse;
    try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
      System.setOut(new PrintStream(outputStream));
      actualResponse = authService.register(registerDto);
      outPut = outputStream.toString();
    } finally {
      System.setOut(out);
    }
    assertEquals("Email not send.",outPut.trim());
    assertInstanceOf(UserRegisterSuccess.class, actualResponse);
    verify(authService, times(1)).saveCode(code, responseDTO.email());
    verify(emailService, times(1)).sendEmailToVerifyEmail(responseDTO.email(), responseDTO.name(), code);
    verify(authService, times(1)).generateVerificationCode();
    assertEquals(responseDTO, ((UserRegisterSuccess) actualResponse).user());
  }

  @Test
  void shouldNotProcessVerificationCode_whenRegistrationFails() throws MessagingException  {
    UserRequestDTO registerDto = new UserRequestDTO("John Doe", "test@example.com", "Password1#");
    UserRegisterFailure userRegisterSuccess = new UserRegisterFailure(AuthError.EMAIL_ALREADY_EXISTS);

    when(userService.register(registerDto)).thenReturn(userRegisterSuccess);

    UserRegisterResult actualResponse = authService.register(registerDto);

    assertInstanceOf(UserRegisterFailure.class, actualResponse);
    verify(authService, times(0)).saveCode(any(), any());
    verify(emailService, times(0)).sendEmailToVerifyEmail(any(), any(), any());
    verify(authService, times(0)).generateVerificationCode();
    verify(userService, times(1)).register(registerDto);
    assertEquals(userRegisterSuccess.errorList().getMessage(), ((UserRegisterFailure) actualResponse).errorList().getMessage());
  }

  @Test
  void shouldGenerateCodeBetween1000And9999_whenGeneratingVerificationCode() {
    Integer code = authService.generateVerificationCode();
    assertTrue(code >= 1000 && code <= 9999);
  }

  @Test
  void shouldReturnFalse_whenCacheIsNotAvailable() {
    int code = 1234;
    when(cacheManager.getCache("EmailVerificationCodes")).thenReturn(null);
    Boolean result = authService.saveCode(code, "someEmail@com.br");
    assertFalse(result);
  }

  @Test
  void shouldReturnTrue_whenCodeIsSavedInCache() {
    int code = 1234;
    String emailMock = "someEmail@com.br";
    when(cacheManager.getCache("EmailVerificationCodes")).thenReturn(cache);
    Boolean result = authService.saveCode(code, emailMock);
    assertTrue(result);
  }

  @Test
  void shouldReturnNull_whenVerificationCodeNotFoundInCache() {
    int code = 1234;
    when(cacheManager.getCache("EmailVerificationCodes")).thenReturn(cache);
    when(cache.get(code)).thenReturn(null);
    String result = authService.recoverCode(code);
    assertNull(result);
  }

  @Test
  void shouldReturnEmail_whenVerificationCodeExistsInCache() {
    int code = 1234;
    String mockEmail = "mock@email.com";
    when(cacheManager.getCache("EmailVerificationCodes")).thenReturn(cache);
    when(cache.get(code)).thenReturn(valueWrapper);
    when(valueWrapper.get()).thenReturn(mockEmail);
    String result = authService.recoverCode(code);
    assertEquals(mockEmail, result);
  }

  @Test
  void shouldReturnFalseWhenRecoverCodeReturnCode(){
    int code = 1234;

    when(authService.recoverCode(code)).thenReturn("fakeEmail@com.br");
    when(cacheManager.getCache("EmailVerificationCodes")).thenReturn(cache);

    boolean response = authService.saveCode(code, "fake");
    assertFalse(response);
  }

  @Test
  void shouldLoginSuccessfullyWhenCredentialsAreValid() {
    LoginRequestDTO loginDto = new LoginRequestDTO("test@example.com", "Password1#");
    UUID userId = UUID.randomUUID();
    UserEntity user = new UserEntity(userId, "John Doe", "test@example.com", "encodedPassword", true);
    String expectedAccessToken = "mocked-access-jwt-token";
    String expectedRefreshToken = "mocked-refresh-jwt-token";
    LoginResponseDTO loginResponseDTO = new LoginResponseDTO(expectedAccessToken, expectedRefreshToken);

    when(userService.findUserByEmail(loginDto.email())).thenReturn(Optional.of(user));
    when(passwordEncoder.matches(loginDto.password(), user.getPassword())).thenReturn(true);
    when(jwtService.generateAccessToken(user.getEmail())).thenReturn(expectedAccessToken);
    when(jwtService.generateRefreshToken(user.getEmail())).thenReturn(expectedRefreshToken);

    LoginResult response = authService.login(loginDto);

    assertInstanceOf(LoginSuccess.class, response);
    LoginResponseDTO actualLoginResponse = ((LoginSuccess) response).tokens();
    assertEquals(loginResponseDTO, actualLoginResponse);
  }

  @Test
  void shouldReturnLoginFailureWhenPasswordIsInvalid() {
    LoginRequestDTO loginDto = new LoginRequestDTO("test@example.com", "WrongPassword");
    UserEntity user = new UserEntity(UUID.randomUUID(), "John Doe", "test@example.com", "encodedPassword", true);

    when(userService.findUserByEmail(loginDto.email())).thenReturn(Optional.of(user));
    when(passwordEncoder.matches(loginDto.password(), user.getPassword())).thenReturn(false);

    LoginResult response = authService.login(loginDto);

    assertInstanceOf(LoginFailure.class, response);
    AuthError actualAuthErrorMessage = ((LoginFailure) response).error();
    assertEquals(AuthError.INVALID_CREDENTIALS.getMessage(), actualAuthErrorMessage.getMessage());
    verify(jwtService, times(0)).generateAccessToken(any());
    verify(jwtService, times(0)).generateRefreshToken(any());
  }

  @Test
  void shouldReturnLoginFailureWhenUserIsNotRegistered() {
    LoginRequestDTO loginDto = new LoginRequestDTO("test@example.com", "WrongPassword");

    when(userService.findUserByEmail(loginDto.email())).thenReturn(Optional.empty());

    LoginResult response = authService.login(loginDto);

    assertInstanceOf(LoginFailure.class, response);
    AuthError actualAuthErrorMessage = ((LoginFailure) response).error();
    assertEquals(AuthError.USER_NOT_REGISTER.getMessage(), actualAuthErrorMessage.getMessage());
    verify(jwtService, times(0)).generateAccessToken(any());
    verify(jwtService, times(0)).generateRefreshToken(any());
  }

/*  @Test
  void shouldReturnLoginFailureWhenEmailIsNotVerified() {
    LoginRequestDTO loginDto = new LoginRequestDTO("test@example.com", "WrongPassword");
    UserEntity user = new UserEntity(UUID.randomUUID(), "John Doe", "test@example.com", "encodedPassword", false);

    when(userService.findUserByEmail(loginDto.email())).thenReturn(Optional.of(user));

    LoginResult response = authService.login(loginDto);

    assertInstanceOf(LoginFailure.class, response);
    AuthError actualAuthErrorMessage = ((LoginFailure) response).error();
    assertEquals(AuthError.EMAIL_NOT_VERIFIED.getMessage(), actualAuthErrorMessage.getMessage());
    verify(jwtService, times(0)).generateAccessToken(any());
    verify(jwtService, times(0)).generateRefreshToken(any());
  }*/
}
