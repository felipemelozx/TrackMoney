package fun.trackmoney.auth.controller;

import fun.trackmoney.auth.dto.LoginRequestDTO;
import fun.trackmoney.auth.dto.LoginResponseDTO;
import fun.trackmoney.auth.service.AuthService;
import fun.trackmoney.user.dtos.UserRequestDTO;
import fun.trackmoney.user.dtos.UserResponseDTO;
import fun.trackmoney.utils.response.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("auth")
public class AuthController {

  private final AuthService authService;

  public AuthController(AuthService authService) {
    this.authService = authService;
  }

  @PostMapping("/register")
  public ResponseEntity<ApiResponse<UserResponseDTO>> register(@RequestBody @Valid UserRequestDTO userDto) {
    return ResponseEntity.ok().body(
        ApiResponse.<UserResponseDTO>success()
            .data(authService.register(userDto))
            .message("User register with success")
            .build()
    );
  }

  @PostMapping("/login")
  public ResponseEntity<ApiResponse<LoginResponseDTO>> login(@RequestBody LoginRequestDTO loginDto) {
    return ResponseEntity.ok().body(
        ApiResponse.<LoginResponseDTO>success()
            .message("Login successful")
            .data(authService.login(loginDto))
            .build()
    );
  }

  @GetMapping("/verify")
  public ResponseEntity<ApiResponse<Boolean>> verify() {
    return ResponseEntity.ok().body(
        ApiResponse.<Boolean>success()
            .message("Token is valid!")
            .data(true)
            .build()
    );
  }
}
