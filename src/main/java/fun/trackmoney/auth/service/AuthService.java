package fun.trackmoney.auth.service;

import fun.trackmoney.auth.dto.LoginRequestDTO;
import fun.trackmoney.auth.dto.LoginResponseDTO;
import fun.trackmoney.auth.dto.internal.AuthError;
import fun.trackmoney.auth.dto.internal.LoginFailure;
import fun.trackmoney.auth.dto.internal.LoginResult;
import fun.trackmoney.auth.dto.internal.LoginSuccess;
import fun.trackmoney.auth.dto.internal.UserRegisterResult;
import fun.trackmoney.auth.dto.internal.UserRegisterSuccess;
import fun.trackmoney.auth.infra.jwt.JwtService;
import fun.trackmoney.email.EmailService;
import fun.trackmoney.user.dtos.UserRequestDTO;
import fun.trackmoney.user.entity.UserEntity;
import fun.trackmoney.user.service.UserService;
import jakarta.mail.MessagingException;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.Optional;

@Service
public class AuthService {

  private final UserService userService;
  private final PasswordEncoder passwordEncoder;
  private final JwtService jwtService;
  private final EmailService emailService;
  private final CacheManager cacheManager;
  private static final SecureRandom SECURE_RANDOM = new SecureRandom();

  public AuthService(UserService userService,
                     PasswordEncoder passwordEncoder,
                     JwtService jwtservice,
                     EmailService emailService,
                     CacheManager cacheManager) {
    this.userService = userService;
    this.passwordEncoder = passwordEncoder;
    this.jwtService = jwtservice;
    this.emailService = emailService;
    this.cacheManager = cacheManager;
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
    if(!user.isActive()){
      return new LoginFailure(AuthError.EMAIL_NOT_VERIFIED);
    }
    if(!passwordEncoder.matches(loginDto.password(), user.getPassword())){
      return new LoginFailure(AuthError.INVALID_CREDENTIALS);
    }
    String accessToken = jwtService.generateToken(user.getEmail());
    String refreshToken = jwtService.generateToken(user.getEmail());

    LoginResponseDTO tokens = new LoginResponseDTO(accessToken, refreshToken);

    return new LoginSuccess(tokens);
  }


  protected Boolean saveCode(Integer code, String email) {
    Cache cache = cacheManager.getCache("EmailVerificationCodes");
    if(recoverCode(code) == null && cache != null){
      cache.put(code, email);
      return true;
    }
    return false;
  }

  protected String recoverCode(Integer code) {
    Cache cache = cacheManager.getCache("EmailVerificationCodes");
    if (cache == null) {
      return null;
    }
    Cache.ValueWrapper wrapper = cache.get(code);
    return wrapper != null ? (String) wrapper.get() : null;
  }

  protected Integer generateVerificationCode() {
    return SECURE_RANDOM.nextInt(9000) + 1000;
  }
}