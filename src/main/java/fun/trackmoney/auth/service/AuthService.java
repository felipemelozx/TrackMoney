package fun.trackmoney.auth.service;

import fun.trackmoney.auth.dto.LoginRequestDTO;
import fun.trackmoney.auth.dto.LoginResponseDTO;
import fun.trackmoney.auth.exception.LoginException;
import fun.trackmoney.auth.infra.jwt.JwtService;
import fun.trackmoney.user.dtos.UserRequestDTO;
import fun.trackmoney.user.dtos.UserResponseDTO;
import fun.trackmoney.user.entity.UserEntity;
import fun.trackmoney.user.service.UserService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

  private final UserService userService;
  private final PasswordEncoder passwordEncoder;
  private final JwtService jwtservice;

  public AuthService(UserService userService, PasswordEncoder passwordEncoder, JwtService jwtservice) {
    this.userService = userService;
    this.passwordEncoder = passwordEncoder;
    this.jwtservice = jwtservice;
  }

  public UserResponseDTO register(UserRequestDTO userDto) {
    return userService.register(userDto);
  }

  public LoginResponseDTO login(LoginRequestDTO loginDto) {
    UserEntity user = userService.findUserByEmail(loginDto);
    if(!passwordEncoder.matches(loginDto.password(), user.getPassword())){
      throw new LoginException("Password is incorrect.");
    }
    return new LoginResponseDTO(jwtservice.generateToken(user.getEmail()));
  }
}