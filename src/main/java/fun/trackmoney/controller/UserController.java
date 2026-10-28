package fun.trackmoney.controller;

import fun.trackmoney.dto.user.ChangePasswordRequestDTO;
import fun.trackmoney.dto.user.DeleteAccountRequestDTO;
import fun.trackmoney.dto.user.UserResponseDTO;
import fun.trackmoney.entity.UserEntity;
import fun.trackmoney.service.UserService;
import fun.trackmoney.utils.AuthUtils;
import fun.trackmoney.utils.CustomFieldError;
import fun.trackmoney.utils.response.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("user")
public class UserController {

  private final AuthUtils authUtils;
  private final UserService userService;
  public UserController(AuthUtils authUtils, UserService userService) {
    this.authUtils = authUtils;
    this.userService = userService;
  }

  @GetMapping
  public ResponseEntity<ApiResponse<UserResponseDTO>> getUserInfo() {
    UserEntity actualUser = authUtils.getCurrentUser();
    return ResponseEntity.ok().body(
        ApiResponse.<UserResponseDTO>success()
            .message("Success to get user info.")
            .data(
                new UserResponseDTO(
                  actualUser.getUserId(),
                  actualUser.getName(),
                  actualUser.getEmail()
                )
            )
            .build());
  }

  @PutMapping("/password")
  public ResponseEntity<ApiResponse<Void>> changePassword(@RequestBody @Valid ChangePasswordRequestDTO request) {
    UserEntity actualUser = authUtils.getCurrentUser();
    boolean isChanged = userService.changePassword(actualUser, request.currentPassword(), request.newPassword());
    if (!isChanged) {
      return ResponseEntity.badRequest().body(
          ApiResponse.<Void>failure()
              .message("Password change failed.")
              .errors(new CustomFieldError("Password", "Current password is incorrect."))
              .build());
    }
    return ResponseEntity.ok().body(
        ApiResponse.<Void>success()
            .message("Password changed successfully.")
            .build());
  }

  @DeleteMapping
  public ResponseEntity<ApiResponse<Void>> deleteUser(@RequestBody @Valid DeleteAccountRequestDTO request,
                                                      @AuthenticationPrincipal UserEntity currentUser) {
    boolean isDeleted = userService.deleteAccount(currentUser, request.password());
    if (!isDeleted) {
      return ResponseEntity.badRequest().body(
          ApiResponse.<Void>failure()
              .message("Account deletion failed.")
              .errors(new CustomFieldError("Password", "Incorrect password."))
              .build());
    }
    return ResponseEntity.noContent().build();
  }
}
