package fun.trackmoney.auth.controller;

import fun.trackmoney.auth.dto.LoginRequestDTO;
import fun.trackmoney.auth.dto.LoginResponseDTO;
import fun.trackmoney.auth.dto.PasswordRequest;
import fun.trackmoney.auth.dto.PasswordResponse;
import fun.trackmoney.auth.dto.RefreshTokenResponse;
import fun.trackmoney.auth.dto.internal.AuthError;
import fun.trackmoney.auth.dto.internal.ForgotPasswordFailure;
import fun.trackmoney.auth.dto.internal.ForgotPasswordResult;
import fun.trackmoney.auth.dto.internal.ForgotPasswordSuccess;
import fun.trackmoney.auth.dto.internal.email.verification.VerificationEmailFailure;
import fun.trackmoney.auth.dto.internal.email.verification.VerificationEmailResult;
import fun.trackmoney.auth.dto.internal.email.verification.VerificationEmailSuccess;
import fun.trackmoney.auth.dto.internal.login.LoginFailure;
import fun.trackmoney.auth.dto.internal.login.LoginResult;
import fun.trackmoney.auth.dto.internal.login.LoginSuccess;
import fun.trackmoney.auth.dto.internal.register.UserRegisterFailure;
import fun.trackmoney.auth.dto.internal.register.UserRegisterResult;
import fun.trackmoney.auth.dto.internal.register.UserRegisterSuccess;
import fun.trackmoney.auth.service.AuthService;
import fun.trackmoney.user.dtos.UserRequestDTO;
import fun.trackmoney.user.dtos.UserResponseDTO;
import fun.trackmoney.user.entity.UserEntity;
import fun.trackmoney.utils.AuthUtils;
import fun.trackmoney.utils.CustomFieldError;
import fun.trackmoney.utils.response.ApiResponse;
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

import java.util.List;


@RestController
@RequestMapping("auth")
public class AuthController {

  private final AuthService authService;
  private final AuthUtils authUtils;

  public AuthController(AuthService authService, AuthUtils authUtils) {
    this.authService = authService;
    this.authUtils = authUtils;
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

    CustomFieldError error = new CustomFieldError("User", loginFailure.error().getMessage());
    return ResponseEntity.badRequest().body(
        ApiResponse.<LoginResponseDTO>failure()
            .message(message)
            .errors(error)
            .build()
    );
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
    VerificationEmailResult response = authService.resendVerificationEmail(user);

    if(response instanceof VerificationEmailSuccess) {
      return ResponseEntity.ok().body(
          ApiResponse.<Void>successWithNoContent()
              .message("Email resend to " + user.getEmail())
              .build()
      );
    }
    VerificationEmailFailure failure = (VerificationEmailFailure) response;
    if(failure.error().getMessage().equals(AuthError.USER_IS_VERIFIED.getMessage())) {
      return ResponseEntity.badRequest().body(
          ApiResponse.<Void>failure()
              .message("Email not send")
              .errors(new CustomFieldError("Email", failure.error().getMessage()))
              .build()
      );
    }

    return ResponseEntity.badRequest().body(
        ApiResponse.<Void>failure()
            .message("Error sending email")
            .errors(new CustomFieldError("Email", failure.error().getMessage()))
            .build()
    );
  }

  @PostMapping("/reset-password")
  public ResponseEntity<ApiResponse<PasswordResponse>> resetPassword(@RequestBody @Valid PasswordRequest request) {
    UserEntity user = authUtils.getCurrentUser();
    ForgotPasswordResult response = authService.resetPassword(user.getEmail(), request.newPassword());
    if(response instanceof ForgotPasswordSuccess) {
      return ResponseEntity.ok().body(
          ApiResponse.<PasswordResponse>success()
              .message("Password reset was successful")
              .data(new PasswordResponse(user.getEmail()))
              .build()
      );
    }
    ForgotPasswordFailure failure = (ForgotPasswordFailure) response;
    if(failure.error().getMessage().equals(AuthError.USER_NOT_REGISTER.getMessage())) {
      return ResponseEntity.badRequest().body(
          ApiResponse.<PasswordResponse>failure()
              .errors(new CustomFieldError("Email", "This email: " + user.getEmail() + " is not register"))
              .build());
    }

    return ResponseEntity.badRequest().body(
        ApiResponse.<PasswordResponse>failure()
            .errors(new CustomFieldError("Internal", "Error sending email. Please try again later."))
            .build());
  }

  @PostMapping("/forgot-password/{email}")
  public ResponseEntity<ApiResponse<Void>> forgotPassword(@PathVariable String email) {
    ForgotPasswordResult result = authService.forgotPassword(email);
    if(result instanceof ForgotPasswordSuccess){
      return ResponseEntity.ok().body(
          ApiResponse.<Void>successWithNoContent().build()
      );
    }
    ForgotPasswordFailure failure = (ForgotPasswordFailure) result;

    if(failure.error().getMessage().equals(AuthError.USER_NOT_REGISTER.getMessage())) {
      return ResponseEntity.badRequest().body(
          ApiResponse.<Void>failure()
              .errors(new CustomFieldError("Email", "This email: " + email + " is not register"))
              .build());
    }

    return ResponseEntity.internalServerError()
        .body(
            ApiResponse.<Void>failure()
                .errors(new CustomFieldError("Email", "Error sending email"))
                .build());
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
