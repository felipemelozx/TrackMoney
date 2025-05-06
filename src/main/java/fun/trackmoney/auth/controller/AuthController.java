package fun.trackmoney.auth.controller;

import fun.trackmoney.auth.service.AuthService;
import fun.trackmoney.user.dtos.UserRequestDTO;
import fun.trackmoney.user.dtos.UserResponseDTO;
import fun.trackmoney.utils.response.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller class to handle authentication-related requests.
 * Provides endpoints for user registration.
 */
@RestController
@RequestMapping("auth/")
public class AuthController {

  private final AuthService authService;

  /**
   * Constructor to initialize the AuthController with an AuthService instance.
   *
   * @param authService The service used for handling authentication operations.
   */
  public AuthController(AuthService authService) {
    this.authService = authService;
  }

  /**
   * Endpoint to register a new user.
   *
   * @param userDto The user data transfer object containing the user's registration information.
   * @return A ResponseEntity containing an ApiResponse
   *                                    with the result of the registration operation.
   */
  @PostMapping("register")
  public ResponseEntity<ApiResponse<UserResponseDTO>> register(
      @RequestBody @Valid UserRequestDTO userDto) {
    return ResponseEntity.ok().body(
        new ApiResponse<>(
            true,
            "User register!",
            authService.register(userDto),
            null
        )
    );
  }
}
