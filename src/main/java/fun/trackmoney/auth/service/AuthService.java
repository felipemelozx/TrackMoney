package fun.trackmoney.auth.service;

import fun.trackmoney.auth.dto.LoginRequestDTO;
import fun.trackmoney.auth.dto.LoginResponseDTO;
import fun.trackmoney.auth.dto.internal.UserRegisterResult;
import fun.trackmoney.auth.exception.LoginException;
import fun.trackmoney.auth.infra.jwt.JwtService;
import fun.trackmoney.email.EmailService;
import fun.trackmoney.user.dtos.UserRequestDTO;
import fun.trackmoney.user.entity.UserEntity;
import fun.trackmoney.user.service.UserService;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.concurrent.ThreadLocalRandom;

@Service
public class AuthService {

  private final UserService userService;
  private final PasswordEncoder passwordEncoder;
  private final JwtService jwtservice;
  private final EmailService emailService;
  private final CacheManager cacheManager;

  public AuthService(UserService userService,
                     PasswordEncoder passwordEncoder,
                     JwtService jwtservice,
                     EmailService emailService, CacheManager cacheManager) {
    this.userService = userService;
    this.passwordEncoder = passwordEncoder;
    this.jwtservice = jwtservice;
    this.emailService = emailService;
    this.cacheManager = cacheManager;
  }

  public UserRegisterResult register(UserRequestDTO userDto) {
    saveCode(generateVerificationCode(), userDto.email());
    return userService.register(userDto);
  }

  public LoginResponseDTO login(LoginRequestDTO loginDto) {
    UserEntity user = userService.findUserByEmail(loginDto.email()).get();
    if(!passwordEncoder.matches(loginDto.password(), user.getPassword())){
      throw new LoginException("Password is incorrect.");
    }
    return new LoginResponseDTO(jwtservice.generateToken(user.getEmail()));
  }


  protected void saveCode(Integer code, String email) {
    Cache cache = cacheManager.getCache("EmailVerificationCodes");
    if (cache != null) cache.put(code, email);
  }

  protected String recoverCode(String token) {
    Cache cache = cacheManager.getCache("EmailVerificationCodes");
    if (cache == null) return null;
    Cache.ValueWrapper wrapper = cache.get(token);
    return wrapper != null ? (String) wrapper.get() : null;
  }
  protected Integer generateVerificationCode(){
    // 1000 to 10000, generate a code with 4 digits
    return ThreadLocalRandom.current().nextInt(1000, 10000);
  }
}