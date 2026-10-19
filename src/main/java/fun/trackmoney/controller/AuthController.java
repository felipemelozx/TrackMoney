package fun.trackmoney.controller;

import fun.trackmoney.dto.auth.LoginRequestDTO;
import fun.trackmoney.dto.auth.LoginResponseDTO;
import fun.trackmoney.dto.auth.PasswordRequest;
import fun.trackmoney.dto.auth.PasswordResponse;
import fun.trackmoney.dto.auth.RefreshTokenResponse;
import fun.trackmoney.common.result.ErrorCode;
import fun.trackmoney.common.result.HttpErrorMapper;
import fun.trackmoney.service.AuthService;
import fun.trackmoney.dto.user.UserRequestDTO;
import fun.trackmoney.dto.user.UserResponseDTO;
import fun.trackmoney.entity.UserEntity;
import fun.trackmoney.utils.AuthUtils;
import fun.trackmoney.utils.CustomFieldError;
import fun.trackmoney.utils.response.ApiResponse;
import fun.trackmoney.utils.response.ErrorResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;


@RestController
@RequestMapping("auth")
public class AuthController {

  private static final String FIELD_EMAIL = "Email";

  private final AuthService authService;
  private final AuthUtils authUtils;

  public AuthController(AuthService authService, AuthUtils authUtils) {
    this.authService = authService;
    this.authUtils = authUtils;
  }

  @PostMapping("/register")
  public ResponseEntity<ApiResponse<UserResponseDTO>> register(@RequestBody @Valid UserRequestDTO userDto) {
    return authService.register(userDto).fold(
        responseDTO -> ResponseEntity.ok(ApiResponse.<UserResponseDTO>success()
            .message("User register with success").data(responseDTO).build()),
        failure -> failure.code() == ErrorCode.AUTH_ERROR_SENDING_EMAIL
            ? HttpErrorMapper.toResponse(failure)
            : ResponseEntity.badRequest().body(ApiResponse.error(new ErrorResponse(
                failure.code().name(), "Unregistered user.", Map.of(FIELD_EMAIL, failure.message())))));
  }

  @PostMapping("/login")
  public ResponseEntity<ApiResponse<LoginResponseDTO>> login(@RequestBody LoginRequestDTO loginDto) {
    return authService.login(loginDto).fold(
        tokens -> ResponseEntity.ok(ApiResponse.<LoginResponseDTO>success()
            .message("Login successful").data(tokens).build()),
        loginError -> ResponseEntity.badRequest().body(ApiResponse.error(new ErrorResponse(
            loginError.code().name(), "Login failed", Map.of(
                loginError.code() == ErrorCode.AUTH_INVALID_CREDENTIALS ? "Credentials" : "User",
                loginError.message())))));
  }

  @PostMapping("/verify-email/{code}")
  public ResponseEntity<ApiResponse<Void>> verifyEmail(@PathVariable
                                                       @Min(value = 1000,
                                                           message = "he code must be exactly 4 digits long.")
                                                       @Max(value = 9999,
                                                           message = "he code must be exactly 4 digits long.")
                                                       Integer code) {
    UserEntity user = (UserEntity) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    boolean isActive = authService.activateUser(code, user.getEmail());

    if(isActive) {
      return ResponseEntity.ok().body(
          ApiResponse.<Void>success()
              .message("User verification with success")
              .build()
      );
    }
    return ResponseEntity.badRequest().body(
        ApiResponse.<Void>failure()
            .message("User not verification")
            .errors(new CustomFieldError("Code", "Code is invalid or expired"))
            .build()
    );
  }

  @PostMapping("/resend-verification-email")
  public ResponseEntity<ApiResponse<Void>> resendVerificationEmail(){
    UserEntity user = authUtils.getCurrentUser();
    return authService.resendVerificationEmail(user).fold(
        ignored -> ResponseEntity.ok(ApiResponse.<Void>success()
            .message("Email resend to " + user.getEmail()).build()),
        failure -> ResponseEntity.badRequest().body(ApiResponse.error(new ErrorResponse(
            failure.code().name(), failure.code() == ErrorCode.AUTH_USER_ALREADY_VERIFIED
                ? "Email not send" : "Error sending email",
            Map.of(FIELD_EMAIL, failure.message())))));
  }

  @PostMapping("/reset-password")
  public ResponseEntity<ApiResponse<PasswordResponse>> resetPassword(@RequestBody @Valid PasswordRequest request) {
    UserEntity user = authUtils.getCurrentUser();
    return authService.resetPassword(user.getEmail(), request.newPassword()).fold(
        ignored -> ResponseEntity.ok(ApiResponse.<PasswordResponse>success()
            .message("Password reset was successful")
            .data(new PasswordResponse(user.getEmail())).build()),
        failure -> {
          if (failure.code() == ErrorCode.USER_NOT_REGISTERED) {
            return ResponseEntity.badRequest().body(ApiResponse.error(new ErrorResponse(
                failure.code().name(), "Password reset failed",
                Map.of(FIELD_EMAIL, "This email: " + user.getEmail() + " is not register"))));
          }
          return ResponseEntity.badRequest().body(ApiResponse.error(new ErrorResponse(
              failure.code().name(), "Password reset failed",
              Map.of("Internal", "Error sending email. Please try again later."))));
        });
  }

  @PostMapping("/forgot-password/{email}")
  public ResponseEntity<ApiResponse<Void>> forgotPassword(@PathVariable String email) {
    return authService.forgotPassword(email).fold(
        ignored -> ResponseEntity.ok(ApiResponse.<Void>successWithNoContent().build()),
        failure -> {
          if (failure.code() == ErrorCode.USER_NOT_REGISTERED) {
            return ResponseEntity.badRequest().body(ApiResponse.error(new ErrorResponse(
                failure.code().name(), "Password recovery failed",
                Map.of(FIELD_EMAIL, "This email: " + email + " is not register"))));
          }
          return ResponseEntity.internalServerError().body(ApiResponse.error(new ErrorResponse(
              failure.code().name(), "Password recovery failed",
              Map.of(FIELD_EMAIL, "Error sending email"))));
        });
  }

  @GetMapping("/refresh")
  public ResponseEntity<ApiResponse<RefreshTokenResponse>> refreshAccessToken() {
    UserEntity actualUser = authUtils.getCurrentUser();

    String accessToken = authService.refreshAccessToken(actualUser.getEmail());
    if(accessToken == null){
      return ResponseEntity.badRequest().body(
          ApiResponse.<RefreshTokenResponse>failure()
              .message("User not found")
              .errors(new CustomFieldError("User", "User not found"))
              .build()
      );
    }

    return ResponseEntity.ok().body(
        ApiResponse.<RefreshTokenResponse>success()
            .message("Access token refreshed successfully.")
            .data(new RefreshTokenResponse(accessToken))
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
