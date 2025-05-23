package fun.trackmoney.user.service;

import fun.trackmoney.account.dtos.AccountRequestDTO;
import fun.trackmoney.account.service.AccountService;
import fun.trackmoney.auth.dto.LoginRequestDTO;
import fun.trackmoney.user.dtos.UserRequestDTO;
import fun.trackmoney.user.dtos.UserResponseDTO;
import fun.trackmoney.user.entity.UserEntity;
import fun.trackmoney.user.exception.EmailAlreadyExistsException;
import fun.trackmoney.user.exception.UserNotFoundException;
import fun.trackmoney.user.exception.PasswordNotValid;
import fun.trackmoney.user.mapper.UserMapper;
import fun.trackmoney.user.repository.UserRepository;
import fun.trackmoney.utils.CustomFieldError;
import fun.trackmoney.utils.PasswordCheck;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
public class UserService {

  private final UserRepository userRepository;
  private final UserMapper userMapper;
  private final PasswordEncoder encoder;
  private final AccountService accountService;

  public UserService(UserRepository userRepository,
                     UserMapper userMapper,
                     PasswordEncoder encoder,
                     AccountService accountService) {
    this.userRepository = userRepository;
    this.userMapper = userMapper;
    this.encoder = encoder;
    this.accountService = accountService;
  }


  public UserResponseDTO register(UserRequestDTO userRequestDTO) {
    List<CustomFieldError> passwordIsValid = PasswordCheck.validatePassword(userRequestDTO.password());
    if(!passwordIsValid.isEmpty()) {
      throw new PasswordNotValid(passwordIsValid);
    }
    UserEntity user = userMapper.userRequestDTOToEntity(userRequestDTO);
    user.setPassword(encoder.encode(user.getPassword()));
    try {
      UserResponseDTO userResponseDTO = userMapper.userEntityToUserResponseDto(userRepository.save(user));
      accountService.createAccount(new AccountRequestDTO(
          userResponseDTO.userId(), "Default Account", BigDecimal.valueOf(0), true));
      return userResponseDTO;
    } catch (RuntimeException e) {
      throw new EmailAlreadyExistsException("Email already registered.");
    }
  }

  public UserEntity findUserByEmail(LoginRequestDTO loginDto) {
    return userRepository.findByEmail(loginDto.email())
        .orElseThrow(() ->
           new UserNotFoundException("User not found")
        );
  }

  public UserEntity findUserById(UUID userId) {
    return userRepository.findById(userId)
        .orElseThrow(() ->
           new UserNotFoundException("User not found")
        );
  }
}
