package fun.trackmoney.user.controller;

import fun.trackmoney.user.dtos.UserResponseDTO;
import fun.trackmoney.user.entity.UserEntity;
import fun.trackmoney.utils.AuthUtils;
import fun.trackmoney.utils.response.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("user")
public class UserController {

  private final AuthUtils authUtils;

  public UserController(AuthUtils authUtils) {
    this.authUtils = authUtils;
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
}
