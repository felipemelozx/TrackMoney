package fun.trackmoney.controller.docs;

import fun.trackmoney.dto.user.ChangePasswordRequestDTO;
import fun.trackmoney.dto.user.DeleteAccountRequestDTO;
import fun.trackmoney.dto.user.UserResponseDTO;
import fun.trackmoney.entity.UserEntity;
import fun.trackmoney.utils.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;

@Tag(name = "User", description = "Endpoints for managing authenticated user profile, password, and account deletion")
public interface UserControllerDocs {

  @Operation(summary = "Get user info", description = "Retrieves profile information for the authenticated user")
  @ApiResponses(value = {
      @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200",
          description = "User profile retrieved successfully"),
      @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401",
          description = "Unauthorized - authentication required")
  })
  ResponseEntity<ApiResponse<UserResponseDTO>> getUserInfo();

  @Operation(summary = "Change password", description = "Changes password for the authenticated user")
  @ApiResponses(value = {
      @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200",
          description = "Password changed successfully"),
      @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400",
          description = "Current password is incorrect or new password does not meet criteria")
  })
  ResponseEntity<ApiResponse<Void>> changePassword(ChangePasswordRequestDTO request);

  @Operation(summary = "Delete account",
      description = "Permanently deletes the user account after password verification")
  @ApiResponses(value = {
      @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "204",
          description = "Account deleted successfully"),
      @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400",
          description = "Password confirmation failed")
  })
  ResponseEntity<ApiResponse<Void>> deleteUser(
      DeleteAccountRequestDTO request,
      @Parameter(hidden = true) UserEntity currentUser
  );
}
