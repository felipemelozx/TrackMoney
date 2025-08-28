package fun.trackmoney.auth.service;

import fun.trackmoney.auth.dto.LoginRequestDTO;
import fun.trackmoney.auth.dto.LoginResponseDTO;
import fun.trackmoney.auth.exception.LoginException;
import fun.trackmoney.auth.infra.jwt.JwtService;
import fun.trackmoney.user.dtos.UserRequestDTO;
import fun.trackmoney.user.dtos.UserResponseDTO;
import fun.trackmoney.user.entity.UserEntity;
import fun.trackmoney.user.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class AuthServiceTest {

  @Mock
  private PasswordEncoder passwordEncoder;

  @Mock
  private UserService userService;

  @Mock
  private JwtService jwtService;

  @InjectMocks
  private AuthService authService;

  @BeforeEach
  void setup() {
    MockitoAnnotations.openMocks(this);
  }

  /*@Test
  void shouldRegisterUserSuccessfully() {
    // Arrange
    UserRequestDTO registerDto = new UserRequestDTO("John Doe", "test@example.com", "Password1#");
    UserResponseDTO expectedResponse = new UserResponseDTO(UUID.randomUUID(), "John Doe", "test@example.com");

    when(userService.register(registerDto)).thenReturn(expectedResponse);

    // Act
    UserResponseDTO actualResponse = authService.register(registerDto);

    // Assert
    assertEquals(expectedResponse.name(), actualResponse.name());
    assertEquals(expectedResponse.email(), actualResponse.email());
  }*/

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
