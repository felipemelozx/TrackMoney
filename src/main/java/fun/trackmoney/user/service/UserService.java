package fun.trackmoney.user.service;

import fun.trackmoney.account.dtos.AccountRequestDTO;
import fun.trackmoney.account.service.AccountService;
import fun.trackmoney.auth.dto.internal.AuthError;
import fun.trackmoney.auth.dto.internal.register.UserRegisterFailure;
import fun.trackmoney.auth.dto.internal.register.UserRegisterResult;
import fun.trackmoney.auth.dto.internal.register.UserRegisterSuccess;
import fun.trackmoney.user.dtos.UserRequestDTO;
import fun.trackmoney.user.dtos.UserResponseDTO;
import fun.trackmoney.user.entity.UserEntity;
import fun.trackmoney.user.mapper.UserMapper;
import fun.trackmoney.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Optional;
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

  @Transactional
  public UserRegisterResult register(UserRequestDTO userRequestDTO) {
    Optional<UserEntity> userExist= findUserByEmail(userRequestDTO.email());
    if(userExist.isPresent()) {
      return new UserRegisterFailure(AuthError.EMAIL_ALREADY_EXISTS);
    }
    UserEntity user = userMapper.userRequestDTOToEntity(userRequestDTO);
    user.setPassword(encoder.encode(user.getPassword()));

    UserEntity userResponse = userRepository.save(user);

    UserResponseDTO userResponseDTO = userMapper.userEntityToUserResponseDto(userResponse);
    AccountRequestDTO account = new AccountRequestDTO(
        userResponseDTO.userId(), "Default Account", BigDecimal.valueOf(0), true);
    accountService.createAccount(account);
    return new UserRegisterSuccess(userResponseDTO);
  }

  public Boolean activateUser(String email) {
    UserEntity user = findUserByEmail(email).orElse(null);

    if(user == null) {
      return false;
    }

    user.activate();
    userRepository.save(user);
    return true;
  }

  public Optional<UserEntity> findUserByEmail(String email) {
    return userRepository.findByEmail(email);
  }

  public Optional<UserEntity> findUserById(UUID userId) {
    return userRepository.findById(userId);
  }
}
