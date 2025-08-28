package fun.trackmoney.auth.controller;

import fun.trackmoney.auth.dto.LoginRequestDTO;
import fun.trackmoney.auth.dto.LoginResponseDTO;
import fun.trackmoney.auth.service.AuthService;
import fun.trackmoney.user.dtos.UserRequestDTO;
import fun.trackmoney.user.dtos.UserResponseDTO;
import fun.trackmoney.utils.response.ApiResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class AuthControllerTest {

  @Mock
  AuthService authService;
  @InjectMocks
  AuthController authController;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

 /* @Test
  void shouldReturnUserOnRegister() {
    UserRequestDTO userRequest = new UserRequestDTO("John","john@example.com", "123");
    UserResponseDTO userResponse = new UserResponseDTO(UUID.randomUUID(), "John", "john@example.com");

    when(authService.register(userRequest)).thenReturn(userResponse);

    ResponseEntity<ApiResponse<UserResponseDTO>> response = authController.register(userRequest);

    assertTrue(response.getBody().isSuccess());
    assertEquals("User register with success", response.getBody().getMessage());
    assertEquals("John", response.getBody().getData().name());
  }*/

/*  @Test
  void shouldReturnTokenOnLogin() {
    LoginRequestDTO loginRequest = new LoginRequestDTO("john@example.com", "123");
    LoginResponseDTO loginResponse = new LoginResponseDTO("jwt-token");

    when(authService.login(loginRequest)).thenReturn(loginResponse);

    ResponseEntity<ApiResponse<LoginResponseDTO>> response = authController.login(loginRequest);

    assertTrue(response.getBody().isSuccess());
    assertEquals("Login successful", response.getBody().getMessage());
    assertEquals("jwt-token", response.getBody().getData().token());
  }*/

  @Test
  void shouldTrueWhenJwtIsValid() {
    var response = authController.verify();

    assertTrue(response.getBody().isSuccess());
    assertTrue(response.getBody().getData());
  }
}
