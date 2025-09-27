package fun.trackmoney.auth.service;

import fun.trackmoney.auth.dto.LoginRequestDTO;
import fun.trackmoney.auth.dto.LoginResponseDTO;
import fun.trackmoney.auth.dto.internal.AuthError;
import fun.trackmoney.auth.dto.internal.ForgotPasswordFailure;
import fun.trackmoney.auth.dto.internal.ForgotPasswordResult;
import fun.trackmoney.auth.dto.internal.ForgotPasswordSuccess;
import fun.trackmoney.auth.dto.internal.email.verification.VerificationEmailFailure;
import fun.trackmoney.auth.dto.internal.email.verification.VerificationEmailResult;
import fun.trackmoney.auth.dto.internal.email.verification.VerificationEmailSuccess;
import fun.trackmoney.auth.dto.internal.login.LoginFailure;
import fun.trackmoney.auth.dto.internal.login.LoginResult;
import fun.trackmoney.auth.dto.internal.login.LoginSuccess;
import fun.trackmoney.auth.dto.internal.register.UserRegisterResult;
import fun.trackmoney.auth.dto.internal.register.UserRegisterSuccess;
import fun.trackmoney.auth.infra.jwt.JwtService;
import fun.trackmoney.email.EmailService;
import fun.trackmoney.redis.CacheManagerService;
import fun.trackmoney.user.dtos.UserRequestDTO;
import fun.trackmoney.user.entity.UserEntity;
import fun.trackmoney.user.service.UserService;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.Optional;

@Service
public class AuthService {

  @Value("${front.url}")
  private String frontUrl;
  private final UserService userService;
  private final PasswordEncoder passwordEncoder;
  private final JwtService jwtService;
  private final EmailService emailService;
  private final CacheManagerService cacheManagerService;
  private static final SecureRandom SECURE_RANDOM = new SecureRandom();

  public AuthService(UserService userService,
                     PasswordEncoder passwordEncoder,
                     JwtService jwtservice,
                     EmailService emailService,
                     CacheManagerService cacheManagerService) {
    this.userService = userService;
    this.passwordEncoder = passwordEncoder;
    this.jwtService = jwtservice;
    this.emailService = emailService;
    this.cacheManagerService = cacheManagerService;
  }

  public UserRegisterResult register(UserRequestDTO userDto)  {
    UserRegisterResult result = userService.register(userDto);
    if(result instanceof UserRegisterSuccess) {
      Integer code;
      boolean codeIsSaved;
      do {
        code = generateVerificationCode();
        codeIsSaved = saveCode(code, userDto.email());
      } while (!codeIsSaved);

      try{
        emailService.sendEmailToVerifyEmail(userDto.email(), userDto.name(), code);
      } catch (MessagingException ignored) {
        // TODO: Implements a log system.
        System.out.println("Email not send.");
      }
    }
    return result;
  }

  public LoginResult login(LoginRequestDTO loginDto) {
    Optional<UserEntity> optionalUser = userService.findUserByEmail(loginDto.email());
    if(optionalUser.isEmpty()) {
      return new LoginFailure(AuthError.USER_NOT_REGISTER);
    }
    UserEntity user = optionalUser.get();

    if(!passwordEncoder.matches(loginDto.password(), user.getPassword())){
      return new LoginFailure(AuthError.INVALID_CREDENTIALS);
    }

    if(!user.isActive()){
      String verificationToken = jwtService.generateVerificationToken(user.getEmail());
      String refreshToken = jwtService.generateRefreshToken(user.getEmail());

      LoginResponseDTO tokens = new LoginResponseDTO(verificationToken, refreshToken);
      return new LoginSuccess(tokens);
    }

    String accessToken = jwtService.generateAccessToken(user.getEmail());
    String refreshToken = jwtService.generateRefreshToken(user.getEmail());

    LoginResponseDTO tokens = new LoginResponseDTO(accessToken, refreshToken);

    return new LoginSuccess(tokens);
  }

  public Boolean activateUser(Integer code, String email) {
    String recoverEmail = getEmailByCode(code);
    if(recoverEmail == null) {
      return false;
    }
    if(!recoverEmail.equals(email)) {
      return false;
    }
    removeCode(code);
    return userService.activateUser(email);
  }

  public VerificationEmailResult resendVerificationEmail(UserEntity user) {
    if(user.isActive()) {
      return new VerificationEmailFailure(AuthError.USER_IS_VERIFIED);
    }
    int code;
    boolean codeIsSaved;
    do {
      code = generateVerificationCode();
      codeIsSaved = saveCode(code, user.getEmail());
    } while (!codeIsSaved);
    try{
      emailService.sendEmailToVerifyEmail(user.getEmail(), user.getName(), code);
    } catch (MessagingException ignored) {
      // TODO: Implements a log system.
      System.out.println("Email not send.");
      return new VerificationEmailFailure(AuthError.ERROR_SENDING_EMAIL);
    }
    return new VerificationEmailSuccess();
  }

  public ForgotPasswordResult forgotPassword(String email) {
    UserEntity optionalUser = userService.findUserByEmail(email).orElse(null);
    if(optionalUser == null) {
      return new ForgotPasswordFailure(AuthError.USER_NOT_REGISTER);
    }
    String jwtCode = jwtService.generateResetPasswordToken(email);
    String link = frontUrl + "/reset-password?token=" +  jwtCode;
    try {
      emailService.sendEmailToResetPassword(email, optionalUser.getName(), link);
    } catch (RuntimeException | MessagingException ex) {
      return new ForgotPasswordFailure(AuthError.ERROR_SENDING_EMAIL);
    }
    return new ForgotPasswordSuccess();
  }

  public ForgotPasswordResult resetPassword(String email, String newPassword) {
    UserEntity user = userService.findUserByEmail(email).orElse(null);
    if(user == null) {
      return new ForgotPasswordFailure(AuthError.USER_NOT_REGISTER);
    }
    String password = passwordEncoder.encode(newPassword);
    user.setPassword(password);
    userService.update(user);
    return new ForgotPasswordSuccess();
  }

  public String refreshAccessToken(String email) {
    UserEntity user = userService.findUserByEmail(email).orElse(null);

    if(user == null) {
      return null;
    }
    return jwtService.generateAccessToken(email);
  }

  protected Boolean saveCode(Integer code, String email) {
    return cacheManagerService.put("EmailVerificationCodes", code, email);
  }

  protected String getEmailByCode(Integer code) {
    return cacheManagerService.get("EmailVerificationCodes", code, String.class);
  }

  protected void removeCode(Integer code) {
    cacheManagerService.evict("EmailVerificationCodes", code);
  }

  protected Integer generateVerificationCode() {
    return SECURE_RANDOM.nextInt(9000) + 1000;
  }
}
