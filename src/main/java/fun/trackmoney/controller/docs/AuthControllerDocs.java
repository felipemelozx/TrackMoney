package fun.trackmoney.controller.docs;

import fun.trackmoney.dto.auth.LoginRequestDTO;
import fun.trackmoney.dto.auth.LoginResponseDTO;
import fun.trackmoney.dto.auth.PasswordRequest;
import fun.trackmoney.dto.auth.PasswordResponse;
import fun.trackmoney.dto.auth.RefreshTokenResponse;
import fun.trackmoney.dto.user.UserRequestDTO;
import fun.trackmoney.dto.user.UserResponseDTO;
import fun.trackmoney.utils.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;

@Tag(name = "Authentication",
    description = "Endpoints for user registration, authentication, and password management")
public interface AuthControllerDocs {

  @Operation(summary = "Register user", description = "Registers a new user account in the system", security = {})
  @ApiResponses(value = {
      @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200",
          description = "User registered successfully"),
      @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400",
          description = "Email already in use or validation error")
  })
  ResponseEntity<ApiResponse<UserResponseDTO>> register(UserRequestDTO userDto);

  @Operation(summary = "Login", description = "Authenticates user credentials and returns JWT tokens", security = {})
  @ApiResponses(value = {
      @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200",
          description = "Login successful"),
      @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400",
          description = "Invalid credentials or unverified user")
  })
  ResponseEntity<ApiResponse<LoginResponseDTO>> login(LoginRequestDTO loginDto);

  @Operation(summary = "Verify email", description = "Activates a user account with a 4-digit verification code",
      security = @SecurityRequirement(name = "bearerAuth"))
  @ApiResponses(value = {
      @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200",
          description = "Email verified successfully"),
      @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400",
          description = "Code is invalid or expired")
  })
  ResponseEntity<ApiResponse<Void>> verifyEmail(
      @Parameter(description = "4-digit verification code", example = "1234") Integer code
  );

  @Operation(summary = "Resend verification email", description = "Resends the account verification email",
      security = @SecurityRequirement(name = "bearerAuth"))
  @ApiResponses(value = {
      @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200",
          description = "Verification email sent successfully"),
      @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400",
          description = "User already verified or error sending email")
  })
  ResponseEntity<ApiResponse<Void>> resendVerificationEmail();

  @Operation(summary = "Reset password", description = "Resets user password using reset token authority",
      security = @SecurityRequirement(name = "bearerAuth"))
  @ApiResponses(value = {
      @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200",
          description = "Password reset successfully"),
      @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400",
          description = "User not registered or invalid payload")
  })
  ResponseEntity<ApiResponse<PasswordResponse>> resetPassword(PasswordRequest request);

  @Operation(summary = "Forgot password", description = "Initiates password recovery by sending reset code to email",
      security = {})
  @ApiResponses(value = {
      @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200",
          description = "Password reset request processed"),
      @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400",
          description = "Email not found in system"),
      @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500",
          description = "Error sending recovery email")
  })
  ResponseEntity<ApiResponse<Void>> forgotPassword(
      @Parameter(description = "User email address", example = "user@example.com") String email
  );

  @Operation(summary = "Refresh access token", description = "Generates a new access token using a refresh token",
      security = @SecurityRequirement(name = "bearerAuth"))
  @ApiResponses(value = {
      @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200",
          description = "Access token refreshed successfully"),
      @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400",
          description = "User not found or invalid token")
  })
  ResponseEntity<ApiResponse<RefreshTokenResponse>> refreshAccessToken();

  @Operation(summary = "Verify token validity", description = "Validates the current session token",
      security = @SecurityRequirement(name = "bearerAuth"))
  @ApiResponses(value = {
      @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200",
          description = "Token is valid")
  })
  ResponseEntity<ApiResponse<Boolean>> verify();
}
