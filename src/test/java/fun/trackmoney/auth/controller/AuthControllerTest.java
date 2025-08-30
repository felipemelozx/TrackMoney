package fun.trackmoney.auth.controller;

import fun.trackmoney.auth.dto.LoginRequestDTO;
import fun.trackmoney.auth.dto.LoginResponseDTO;
import fun.trackmoney.auth.dto.internal.AuthError;
import fun.trackmoney.auth.dto.internal.UserRegisterFailure;
import fun.trackmoney.auth.dto.internal.UserRegisterSuccess;
import fun.trackmoney.auth.service.AuthService;
import fun.trackmoney.user.dtos.UserRequestDTO;
import fun.trackmoney.user.dtos.UserResponseDTO;
import fun.trackmoney.utils.CustomFieldError;
import fun.trackmoney.utils.response.ApiResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

  @Mock
  AuthService authService;
  @InjectMocks
  AuthController authController;

  @Test
  void shouldRegisterUserSuccessfullyWhenUserRequestIsValid() {
    UserRequestDTO userRequest = new UserRequestDTO("John","john@example.com", "123");
    UserResponseDTO userResponse = new UserResponseDTO(UUID.randomUUID(), "John", "john@example.com");
    UserRegisterSuccess userRegisterSuccess = new UserRegisterSuccess(userResponse);

    when(authService.register(userRequest)).thenReturn(userRegisterSuccess);

    ResponseEntity<ApiResponse<UserResponseDTO>> response = authController.register(userRequest);

    assertNotNull(response.getBody());
    assertTrue(response.getBody().isSuccess());
    assertEquals("User register with success", response.getBody().getMessage());
    assertEquals("John", response.getBody().getData().name());
    assertTrue(response.getBody().getErrors().isEmpty());
  }

  @Test
  void shouldReturnBadRequestWhenUserEmailAlreadyExists() {
    UserRequestDTO userRequest = new UserRequestDTO("John","john@example.com", "123");
    UserRegisterFailure userRegisterFailure = new UserRegisterFailure(AuthError.EMAIL_ALREADY_EXISTS);
    CustomFieldError customFieldError = new CustomFieldError("Email", AuthError.EMAIL_ALREADY_EXISTS.getMessage());

    when(authService.register(userRequest)).thenReturn(userRegisterFailure);

    ResponseEntity<ApiResponse<UserResponseDTO>> response = authController.register(userRequest);

    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    assertNotNull(response.getBody());
    assertFalse(response.getBody().isSuccess());
    assertEquals("Unregistered user.", response.getBody().getMessage());
    assertNull(response.getBody().getData());
    assertEquals(customFieldError.getField(), response.getBody().getErrors().get(0).getField());
    assertEquals(customFieldError.getMessage(), response.getBody().getErrors().get(0).getMessage());
  }


  @Test
  void shouldReturnJwtTokenWhenCredentialsAreValid() {
    LoginRequestDTO loginRequest = new LoginRequestDTO("john@example.com", "123");
    LoginResponseDTO loginResponse = new LoginResponseDTO("jwt-token");

    when(authService.login(loginRequest)).thenReturn(loginResponse);

    ResponseEntity<ApiResponse<LoginResponseDTO>> response = authController.login(loginRequest);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getBody());
    assertTrue(response.getBody().isSuccess());
    assertEquals("Login successful", response.getBody().getMessage());
    assertEquals("jwt-token", response.getBody().getData().token());
    assertTrue(response.getBody().getErrors().isEmpty());
  }

  @Test
  void shouldReturnTrueWhenJwtTokenIsValid() {
    var response = authController.verify();

    assertNotNull(response.getBody());
    assertTrue(response.getBody().isSuccess());
    assertTrue(response.getBody().getData());
    assertTrue(response.getBody().getErrors().isEmpty());
  }
}
