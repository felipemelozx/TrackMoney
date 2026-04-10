package fun.trackmoney.user.controller;

import fun.trackmoney.dto.user.UserResponseDTO;
import fun.trackmoney.entity.UserEntity;
import fun.trackmoney.user.service.UserService;
import fun.trackmoney.utils.AuthUtils;
import fun.trackmoney.utils.response.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
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

  @DeleteMapping
  public ResponseEntity<Void> deleteUser(@AuthenticationPrincipal UserEntity currentUser) {
    boolean isDeleted = userService.deleteUser(currentUser);
    if(!isDeleted){
      return ResponseEntity.badRequest().build();
    }
    return ResponseEntity.ok().build();
  }
}
