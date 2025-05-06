package fun.trackmoney.auth.service;

import fun.trackmoney.user.dtos.UserRequestDTO;
import fun.trackmoney.user.dtos.UserResponseDTO;
import fun.trackmoney.user.service.UserService;
import org.springframework.stereotype.Service;

/**
 * Provides authentication-related services, including user registration.
 */
@Service
public class AuthService {

  private final UserService userService;

  /**
   * Constructs an AuthService with the specified UserService.
   *
   * @param userService the service used to handle user-related operations
   */
  public AuthService(UserService userService) {
    this.userService = userService;
  }

  /**
   * Registers a new user based on the provided user data.
   *
   * @param userDto the data transfer object containing user registration details
   * @return a data transfer object containing the registered user's details
   */
  public UserResponseDTO register(UserRequestDTO userDto) {
    return userService.register(userDto);
  }
}