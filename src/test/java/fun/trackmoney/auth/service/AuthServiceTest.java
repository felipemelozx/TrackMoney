package fun.trackmoney.auth.service;

import fun.trackmoney.auth.dto.LoginRequestDTO;
import fun.trackmoney.auth.dto.LoginResponseDTO;
import fun.trackmoney.auth.dto.internal.AuthError;
import fun.trackmoney.auth.dto.internal.email.verification.VerificationEmailFailure;
import fun.trackmoney.auth.dto.internal.email.verification.VerificationEmailResult;
import fun.trackmoney.auth.dto.internal.email.verification.VerificationEmailSuccess;
import fun.trackmoney.auth.dto.internal.login.LoginFailure;
import fun.trackmoney.auth.dto.internal.login.LoginResult;
import fun.trackmoney.auth.dto.internal.login.LoginSuccess;
import fun.trackmoney.auth.dto.internal.register.UserRegisterFailure;
import fun.trackmoney.auth.dto.internal.register.UserRegisterResult;
import fun.trackmoney.auth.dto.internal.register.UserRegisterSuccess;
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
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
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
    String result = authService.recoverEmail(code);
    assertNull(result);
  }

  @Test
  void shouldReturnEmail_whenVerificationCodeExistsInCache() {
    int code = 1234;
    String mockEmail = "mock@email.com";
    when(cacheManager.getCache("EmailVerificationCodes")).thenReturn(cache);
    when(cache.get(code)).thenReturn(valueWrapper);
    when(valueWrapper.get()).thenReturn(mockEmail);
    String result = authService.recoverEmail(code);
    assertEquals(mockEmail, result);
  }

  @Test
  void shouldReturnFalseWhenRecoverCodeReturnEmail(){
    int code = 1234;

    when(authService.recoverEmail(code)).thenReturn("fakeEmail@com.br");
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

  @Test
  void shouldReturnLoginSuccessWithVerificationTokenWhenEmailIsNotVerified() {
    LoginRequestDTO loginRequest = new LoginRequestDTO("test@example.com", "WrongPassword");
    UserEntity unverifiedUser = new UserEntity(UUID.randomUUID(), "John Doe", "test@example.com", "encodedPassword", false);
    String verificationToken = "mockCode";

    when(jwtService.generateVerificationToken(unverifiedUser.getEmail())).thenReturn(verificationToken);
    when(userService.findUserByEmail(loginRequest.email())).thenReturn(Optional.of(unverifiedUser));

    LoginResult result = authService.login(loginRequest);

    assertInstanceOf(LoginSuccess.class, result);
    LoginResponseDTO loginResponse = ((LoginSuccess) result).tokens();
    assertEquals(verificationToken, loginResponse.accessToken());
    assertNull(loginResponse.refreshToken());
  }

  @Test
  void shouldReturnFalseWhenActivatingUserIfRecoveredEmailIsNull() {
    int code = 1234;
    String email = "mock@email.com";

    when(authService.recoverEmail(code)).thenReturn(null);

    boolean result = authService.activateUser(code, email);

    assertFalse(result);
    verify(authService, times(1)).recoverEmail(code);
  }

  @Test
  void shouldReturnFalseWhenActivatingUserIfRecoveredEmailIsDifferent() {
    int code = 1234;
    String requestedEmail = "mock@email.com";
    String recoveredEmail = "other@email.com";

    when(authService.recoverEmail(code)).thenReturn(recoveredEmail);

    boolean result = authService.activateUser(code, requestedEmail);

    assertFalse(result);
    verify(authService, times(1)).recoverEmail(code);
  }

  @Test
  void shouldReturnTrueWhenActivatingUserIfEmailAndCodeAreValid() {
    int code = 1234;
    String email = "mock@email.com";

    when(authService.recoverEmail(code)).thenReturn(email);
    when(userService.activateUser(email)).thenReturn(true);

    boolean result = authService.activateUser(code, email);

    assertTrue(result);
    verify(authService, times(1)).recoverEmail(code);
    verify(userService, times(1)).activateUser(email);
  }

  @Test
  void shouldReturnFailureWhenUserIsAlreadyActive() {
    UserEntity user = new UserEntity(null, "John", "john@example.com", "123", true);

    VerificationEmailResult result = authService.resendVerificationEmail(user);

    assertInstanceOf(VerificationEmailFailure.class, result);
    VerificationEmailFailure failure = (VerificationEmailFailure) result;
    assertEquals(AuthError.USER_IS_VERIFIED, failure.error());
  }

  @Test
  void shouldReturnFailureWhenEmailSendingThrowsException() throws Exception {
    UserEntity user = new UserEntity(null, "John", "john@example.com", "123", false);
    int code = 1234;
    doReturn(true).when(authService).saveCode(code, user.getEmail());
    doReturn(code).when(authService).generateVerificationCode();
    doThrow(new MessagingException("fail")).when(emailService)
        .sendEmailToVerifyEmail(user.getEmail(), user.getName(), code);

    VerificationEmailResult result = authService.resendVerificationEmail(user);

    assertInstanceOf(VerificationEmailFailure.class, result);
    VerificationEmailFailure failure = (VerificationEmailFailure) result;
    assertEquals(AuthError.ERROR_SENDING_EMAIL, failure.error());
  }

  @Test
  void shouldResendVerificationEmailSuccessfully() throws Exception {
    UserEntity user = new UserEntity(null, "John", "john@example.com", "123", false);

    doReturn(true).when(authService).saveCode(anyInt(), eq(user.getEmail()));
    doNothing().when(emailService)
        .sendEmailToVerifyEmail(eq(user.getEmail()), eq(user.getName()), anyInt());

    VerificationEmailResult result = authService.resendVerificationEmail(user);

    assertInstanceOf(VerificationEmailSuccess.class, result);
  }

  @Test
  void shouldRetryUntilCodeIsSavedSuccessfully() throws Exception {
    UserEntity user = new UserEntity(null, "John", "john@example.com", "123", false);

    when(authService.saveCode(anyInt(), eq(user.getEmail())))
        .thenReturn(false)
        .thenReturn(true);

    doNothing().when(emailService)
        .sendEmailToVerifyEmail(eq(user.getEmail()), eq(user.getName()), anyInt());

    VerificationEmailResult result = authService.resendVerificationEmail(user);

    assertInstanceOf(VerificationEmailSuccess.class, result);

    verify(authService, times(2)).saveCode(anyInt(), eq(user.getEmail()));
  }
}
