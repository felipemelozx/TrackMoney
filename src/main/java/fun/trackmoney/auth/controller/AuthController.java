package fun.trackmoney.auth.controller;

import fun.trackmoney.auth.dto.LoginRequestDTO;
import fun.trackmoney.auth.dto.LoginResponseDTO;
import fun.trackmoney.auth.dto.internal.AuthError;
import fun.trackmoney.auth.dto.internal.LoginFailure;
import fun.trackmoney.auth.dto.internal.LoginResult;
import fun.trackmoney.auth.dto.internal.LoginSuccess;
import fun.trackmoney.auth.dto.internal.UserRegisterFailure;
import fun.trackmoney.auth.dto.internal.UserRegisterResult;
import fun.trackmoney.auth.dto.internal.UserRegisterSuccess;
import fun.trackmoney.auth.service.AuthService;
import fun.trackmoney.user.dtos.UserRequestDTO;
import fun.trackmoney.user.dtos.UserResponseDTO;
import fun.trackmoney.utils.CustomFieldError;
import fun.trackmoney.utils.response.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController
@RequestMapping("auth")
public class AuthController {

  private final AuthService authService;

  public AuthController(AuthService authService) {
    this.authService = authService;
  }

  @PostMapping("/register")
  public ResponseEntity<ApiResponse<UserResponseDTO>> register(@RequestBody @Valid UserRequestDTO userDto) {
    UserRegisterResult user = authService.register(userDto);
    if(user instanceof UserRegisterSuccess registerSuccess) {
      UserResponseDTO responseDTO = (registerSuccess).user();
      return ResponseEntity.ok().body(
          ApiResponse.<UserResponseDTO>success()
              .message("User register with success")
              .data(responseDTO)
              .build()
      );
    }
    UserRegisterFailure failure = (UserRegisterFailure) user;
    return ResponseEntity.badRequest().body(
        ApiResponse.<UserResponseDTO>failure()
            .message("Unregistered user.")
            .data(null)
            .errors(List.of(new CustomFieldError("Email", failure.errorList().getMessage())))
            .build()
    );
  }

  @PostMapping("/login")
  public ResponseEntity<ApiResponse<LoginResponseDTO>> login(@RequestBody LoginRequestDTO loginDto) {
    LoginResult loginResult = authService.login(loginDto);
    if(loginResult instanceof LoginSuccess response) {
      return ResponseEntity.ok().body(
          ApiResponse.<LoginResponseDTO>success()
              .message("Login successful")
              .data(response.tokens())
              .build()
      );
    }

    LoginFailure loginFailure = (LoginFailure) loginResult;
    String message = "Login failure";
    if(loginFailure.error().equals(AuthError.INVALID_CREDENTIALS)){
      CustomFieldError error = new CustomFieldError("Credentials", loginFailure.error().getMessage());
      return ResponseEntity.badRequest().body(
          ApiResponse.<LoginResponseDTO>failure()
              .message(message)
              .errors(error)
              .build()
      );
    }

    if(loginFailure.error().equals(AuthError.EMAIL_NOT_VERIFIED)){
      CustomFieldError error = new CustomFieldError("User", loginFailure.error().getMessage());
      return ResponseEntity.badRequest().body(
          ApiResponse.<LoginResponseDTO>failure()
              .message(message)
              .errors(error)
              .build()
      );
    }

    CustomFieldError error = new CustomFieldError("User", loginFailure.error().getMessage());
    return ResponseEntity.badRequest().body(
        ApiResponse.<LoginResponseDTO>failure()
            .message(message)
            .errors(error)
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
