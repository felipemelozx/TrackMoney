package fun.trackmoney.service;
import fun.trackmoney.service.AuthService;

import fun.trackmoney.dto.auth.LoginRequestDTO;
import fun.trackmoney.dto.auth.LoginResponseDTO;
import fun.trackmoney.dto.auth.internal.AuthError;
import fun.trackmoney.dto.auth.internal.ForgotPasswordFailure;
import fun.trackmoney.dto.auth.internal.ForgotPasswordResult;
import fun.trackmoney.dto.auth.internal.ForgotPasswordSuccess;
import fun.trackmoney.dto.auth.internal.email.verification.VerificationEmailFailure;
import fun.trackmoney.dto.auth.internal.email.verification.VerificationEmailResult;
import fun.trackmoney.dto.auth.internal.email.verification.VerificationEmailSuccess;
import fun.trackmoney.dto.auth.internal.login.LoginFailure;
import fun.trackmoney.dto.auth.internal.login.LoginResult;
import fun.trackmoney.dto.auth.internal.login.LoginSuccess;
import fun.trackmoney.dto.auth.internal.register.UserRegisterFailure;
import fun.trackmoney.dto.auth.internal.register.UserRegisterResult;
import fun.trackmoney.dto.auth.internal.register.UserRegisterSuccess;
import fun.trackmoney.infra.jwt.JwtService;
import fun.trackmoney.infra.email.EmailService;
import fun.trackmoney.infra.redis.CacheManagerService;
import fun.trackmoney.dto.user.UserRequestDTO;
import fun.trackmoney.dto.user.UserResponseDTO;
import fun.trackmoney.entity.UserEntity;
import fun.trackmoney.service.UserService;
import jakarta.mail.MessagingException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

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
  private CacheManagerService cacheManagerService;

  @InjectMocks
  @Spy
  private AuthService authService;

  @BeforeEach
  void setupFrontUrl() {
    ReflectionTestUtils.setField(authService, "frontUrl", "http://localhost");
  }

  private static final String CACHE_NAME = "EmailVerificationCodes";

  @Test
  void shouldRegisterUserAndSendVerificationEmail_whenUserRequestIsValid() throws MessagingException {
    UserRequestDTO registerDto = new UserRequestDTO("John Doe", "test@example.com", "Password1#");
    UserResponseDTO responseDTO = new UserResponseDTO(UUID.randomUUID(), "John Doe", "test@example.com");
    UserRegisterSuccess userRegisterSuccess = new UserRegisterSuccess(responseDTO);

    when(userService.register(registerDto)).thenReturn(userRegisterSuccess);
    when(cacheManagerService.put(eq(CACHE_NAME), anyInt(), eq(registerDto.email()))).thenReturn(true);
    doNothing().when(emailService)
        .sendEmailToVerifyEmail(eq(registerDto.email()), eq(registerDto.name()), anyInt());

    UserRegisterResult actualResponse = authService.register(registerDto);

    assertInstanceOf(UserRegisterSuccess.class, actualResponse);
    verify(emailService, times(1)).sendEmailToVerifyEmail(eq(responseDTO.email()), eq(responseDTO.name()), anyInt());
    verify(cacheManagerService, times(1)).put(eq(CACHE_NAME), anyInt(), eq(responseDTO.email()));
    assertEquals(responseDTO, ((UserRegisterSuccess) actualResponse).user());
  }

  @Test
  void shouldRetryGeneratingVerificationCode_whenCodeSavingFails() throws MessagingException {
    UserRequestDTO registerDto = new UserRequestDTO("John Doe", "test@example.com", "Password1#");
    UserResponseDTO responseDTO = new UserResponseDTO(UUID.randomUUID(), "John Doe", "test@example.com");
    UserRegisterSuccess userRegisterSuccess = new UserRegisterSuccess(responseDTO);

    when(userService.register(registerDto)).thenReturn(userRegisterSuccess);
    when(cacheManagerService.put(eq(CACHE_NAME), anyInt(), eq(registerDto.email()))).thenReturn(false, true);
    doNothing().when(emailService)
        .sendEmailToVerifyEmail(eq(registerDto.email()), eq(registerDto.name()), anyInt());

    UserRegisterResult actualResponse = authService.register(registerDto);

    assertInstanceOf(UserRegisterSuccess.class, actualResponse);
    verify(emailService, times(1)).sendEmailToVerifyEmail(eq(responseDTO.email()), eq(responseDTO.name()), anyInt());
    verify(cacheManagerService, times(2)).put(eq(CACHE_NAME), anyInt(), eq(responseDTO.email()));
    assertEquals(responseDTO, ((UserRegisterSuccess) actualResponse).user());
  }

  @Test
  void shouldReturnSuccessfulRegistration_whenEmailSendingFails() throws MessagingException, IOException {
    UserRequestDTO registerDto = new UserRequestDTO("John Doe", "test@example.com", "Password1#");
    UserResponseDTO responseDTO = new UserResponseDTO(UUID.randomUUID(), "John Doe", "test@example.com");
    UserRegisterSuccess userRegisterSuccess = new UserRegisterSuccess(responseDTO);

    when(userService.register(registerDto)).thenReturn(userRegisterSuccess);
    when(cacheManagerService.put(eq(CACHE_NAME), anyInt(), eq(registerDto.email()))).thenReturn(true);
    doThrow(new MessagingException()).when(emailService)
        .sendEmailToVerifyEmail(eq(registerDto.email()), eq(registerDto.name()), anyInt());

    UserRegisterResult actualResponse;

    actualResponse = authService.register(registerDto);

    assertInstanceOf(UserRegisterSuccess.class, actualResponse);
    verify(emailService, times(1)).sendEmailToVerifyEmail(eq(responseDTO.email()), eq(responseDTO.name()), anyInt());
    verify(cacheManagerService, times(1)).put(eq(CACHE_NAME), anyInt(), eq(responseDTO.email()));
    assertEquals(responseDTO, ((UserRegisterSuccess) actualResponse).user());
  }

  @Test
  void shouldNotProcessVerificationCode_whenRegistrationFails() throws MessagingException  {
    UserRequestDTO registerDto = new UserRequestDTO("John Doe", "test@example.com", "Password1#");
    UserRegisterFailure userRegisterSuccess = new UserRegisterFailure(AuthError.EMAIL_ALREADY_EXISTS);

    when(userService.register(registerDto)).thenReturn(userRegisterSuccess);

    UserRegisterResult actualResponse = authService.register(registerDto);

    assertInstanceOf(UserRegisterFailure.class, actualResponse);
    verify(emailService, times(0)).sendEmailToVerifyEmail(any(), any(), any());
    verify(userService, times(1)).register(registerDto);
    assertEquals(userRegisterSuccess.errorList().getMessage(), ((UserRegisterFailure) actualResponse).errorList().getMessage());
  }

  @Test
  void shouldGenerateCodeBetween1000And9999_whenGeneratingVerificationCode() {
    Integer code = ReflectionTestUtils.invokeMethod(authService, "generateVerificationCode");
    assertTrue(code >= 1000 && code <= 9999);
  }

  @Test
  void shouldReturnNull_whenVerificationCodeNotFoundInCache() {
    int code = 1234;
    when(cacheManagerService.get(CACHE_NAME, code, String.class)).thenReturn(null);
    String result = ReflectionTestUtils.invokeMethod(authService, "getEmailByCode", code);
    assertNull(result);
  }

  @Test
  void shouldReturnEmail_whenVerificationCodeExistsInCache() {
    int code = 1234;
    String mockEmail = "mock@email.com";
    when(cacheManagerService.get(CACHE_NAME, code, String.class)).thenReturn(mockEmail);
    String result = ReflectionTestUtils.invokeMethod(authService, "getEmailByCode", code);
    assertEquals(mockEmail, result);
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
    when(passwordEncoder.matches(loginRequest.password(), unverifiedUser.getPassword())).thenReturn(true);
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

    when(cacheManagerService.get(CACHE_NAME, code, String.class)).thenReturn(null);

    boolean result = authService.activateUser(code, email);

    assertFalse(result);
    verify(cacheManagerService, times(1)).get(CACHE_NAME, code, String.class);
  }

  @Test
  void shouldReturnFalseWhenActivatingUserIfRecoveredEmailIsDifferent() {
    int code = 1234;
    String requestedEmail = "mock@email.com";
    String recoveredEmail = "other@email.com";

    when(cacheManagerService.get(CACHE_NAME, code, String.class)).thenReturn(recoveredEmail);

    boolean result = authService.activateUser(code, requestedEmail);

    assertFalse(result);
    verify(cacheManagerService, times(1)).get(CACHE_NAME, code, String.class);
  }

  @Test
  void shouldReturnTrueWhenActivatingUserIfEmailAndCodeAreValid() {
    int code = 1234;
    String email = "mock@email.com";

    when(cacheManagerService.get(CACHE_NAME, code, String.class)).thenReturn(email);
    when(userService.activateUser(email)).thenReturn(true);

    boolean result = authService.activateUser(code, email);

    assertTrue(result);
    verify(cacheManagerService, times(1)).get(CACHE_NAME, code, String.class);
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

    when(cacheManagerService.put(eq(CACHE_NAME), anyInt(), eq(user.getEmail()))).thenReturn(true);
    doThrow(new MessagingException("fail")).when(emailService)
        .sendEmailToVerifyEmail(eq(user.getEmail()), eq(user.getName()), anyInt());

    VerificationEmailResult result = authService.resendVerificationEmail(user);

    assertInstanceOf(VerificationEmailFailure.class, result);
    VerificationEmailFailure failure = (VerificationEmailFailure) result;
    assertEquals(AuthError.ERROR_SENDING_EMAIL, failure.error());
  }

  @Test
  void shouldResendVerificationEmailSuccessfully() throws Exception {
    UserEntity user = new UserEntity(null, "John", "john@example.com", "123", false);

    when(cacheManagerService.put(eq(CACHE_NAME), anyInt(), eq(user.getEmail()))).thenReturn(true);
    doNothing().when(emailService)
        .sendEmailToVerifyEmail(eq(user.getEmail()), eq(user.getName()), anyInt());

    VerificationEmailResult result = authService.resendVerificationEmail(user);

    assertInstanceOf(VerificationEmailSuccess.class, result);
  }

  @Test
  void shouldRetryUntilCodeIsSavedSuccessfully() throws Exception {
    UserEntity user = new UserEntity(null, "John", "john@example.com", "123", false);

    when(cacheManagerService.put(eq(CACHE_NAME), anyInt(), eq(user.getEmail())))
        .thenReturn(false)
        .thenReturn(true);

    doNothing().when(emailService)
        .sendEmailToVerifyEmail(eq(user.getEmail()), eq(user.getName()), anyInt());

    VerificationEmailResult result = authService.resendVerificationEmail(user);

    assertInstanceOf(VerificationEmailSuccess.class, result);

    verify(cacheManagerService, times(2)).put(eq(CACHE_NAME), anyInt(), eq(user.getEmail()));
  }

  @Test
  void shouldReturnFailure_whenUserIsNotRegistered() throws MessagingException {
    String email = "notfound@example.com";
    when(userService.findUserByEmail(email)).thenReturn(Optional.empty());

    ForgotPasswordResult result = authService.forgotPassword(email);

    assertInstanceOf(ForgotPasswordFailure.class, result);
    ForgotPasswordFailure failure = (ForgotPasswordFailure) result;
    assertEquals(AuthError.USER_NOT_REGISTER, failure.error());
    verify(jwtService, times(0)).generateResetPasswordToken(any());
    verify(emailService, times(0)).sendEmailToResetPassword(any(), any(), any());
  }

  @Test
  void shouldReturnFailure_whenEmailSendingThrowsException() throws Exception {
    String email = "test@example.com";
    UserEntity user = new UserEntity(UUID.randomUUID(), "John Doe", email, "encodedPassword", true);
    String jwtCode = "mock-reset-token";
    String expectedLink = "http://localhost/reset-password?token=" + jwtCode;

    when(userService.findUserByEmail(email)).thenReturn(Optional.of(user));
    when(jwtService.generateResetPasswordToken(email)).thenReturn(jwtCode);
    doThrow(new MessagingException("fail"))
        .when(emailService).sendEmailToResetPassword(email, user.getName(), expectedLink);

    ForgotPasswordResult result = authService.forgotPassword(email);

    assertInstanceOf(ForgotPasswordFailure.class, result);
    ForgotPasswordFailure failure = (ForgotPasswordFailure) result;
    assertEquals(AuthError.ERROR_SENDING_EMAIL, failure.error());
    verify(emailService, times(1)).sendEmailToResetPassword(email, user.getName(), expectedLink);
  }

  @Test
  void shouldReturnSuccess_whenEmailIsSentSuccessfully() throws Exception {
    String email = "test@example.com";
    UserEntity user = new UserEntity(UUID.randomUUID(), "John Doe", email, "encodedPassword", true);
    String jwtCode = "mock-reset-token";
    String expectedLink = "http://localhost/reset-password?token=" + jwtCode;

    when(userService.findUserByEmail(email)).thenReturn(Optional.of(user));
    when(jwtService.generateResetPasswordToken(email)).thenReturn(jwtCode);
    doNothing().when(emailService).sendEmailToResetPassword(email, user.getName(), expectedLink);

    ForgotPasswordResult result = authService.forgotPassword(email);

    assertInstanceOf(ForgotPasswordSuccess.class, result);
    verify(emailService, times(1)).sendEmailToResetPassword(email, user.getName(), expectedLink);
    verify(jwtService, times(1)).generateResetPasswordToken(email);
  }

  @Test
  void shouldReturnSuccessWhenUpdatePassword() {
    String mockEmail = "someEmail@email.com";
    String newPassword = "somePassword";
    UserEntity mockUser = new UserEntity(UUID.randomUUID(), "soma name", mockEmail, "oldPassword", true);

    when(userService.findUserByEmail(mockEmail)).thenReturn(Optional.of(mockUser));
    when(passwordEncoder.encode(newPassword)).thenReturn(newPassword);
    doNothing().when(userService).update(any());

    ForgotPasswordResult response = authService.resetPassword(mockEmail, newPassword);

    assertInstanceOf(ForgotPasswordSuccess.class, response);
    verify(userService, times(1)).update(any());
    verify(userService, times(1)).findUserByEmail(mockEmail);
  }

  @Test
  void shouldReturnFailureWhenUserNotFound() {
    String mockEmail = "someEmail@email.com";
    String newPassword = "somePassword";

    when(userService.findUserByEmail(mockEmail)).thenReturn(Optional.empty());

    ForgotPasswordResult response = authService.resetPassword(mockEmail, newPassword);
    assertInstanceOf(ForgotPasswordFailure.class, response);
    verify(userService, times(0)).update(any());
    verify(userService, times(1)).findUserByEmail(mockEmail);
  }

  @Test
  void shouldReturnNullWhenUserNotFoundOnRefreshAccessToken() {
    String email = "notfound@example.com";
    when(userService.findUserByEmail(email)).thenReturn(Optional.empty());

    String result = authService.refreshAccessToken(email);

    assertNull(result);
    verify(jwtService, times(0)).generateAccessToken(any());
    verify(userService, times(1)).findUserByEmail(email);
  }

  @Test
  void shouldReturnNewAccessTokenWhenUserExists() {
    String email = "test@example.com";
    UserEntity user = new UserEntity(UUID.randomUUID(), "John Doe", email, "encodedPassword", true);
    String expectedAccessToken = "mocked-new-access-token";

    when(userService.findUserByEmail(email)).thenReturn(Optional.of(user));
    when(jwtService.generateAccessToken(email)).thenReturn(expectedAccessToken);

    String result = authService.refreshAccessToken(email);

    assertEquals(expectedAccessToken, result);
    verify(jwtService, times(1)).generateAccessToken(email);
    verify(userService, times(1)).findUserByEmail(email);
  }
}
