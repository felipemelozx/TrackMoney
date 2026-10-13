package fun.trackmoney.service;

import fun.trackmoney.entity.AccountEntity;
import fun.trackmoney.common.result.ErrorCode;
import fun.trackmoney.common.result.Result;
import fun.trackmoney.dto.user.UserRequestDTO;
import fun.trackmoney.dto.user.UserResponseDTO;
import fun.trackmoney.entity.UserEntity;
import fun.trackmoney.mapper.UserMapper;
import fun.trackmoney.repository.UserRepository;
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

  public UserService(UserRepository userRepository,
                     UserMapper userMapper,
                     PasswordEncoder encoder) {
    this.userRepository = userRepository;
    this.userMapper = userMapper;
    this.encoder = encoder;
  }

  @Transactional
  public Result<UserResponseDTO> register(UserRequestDTO userRequestDTO) {
    Optional<UserEntity> userExist= findUserByEmail(userRequestDTO.email());
    if(userExist.isPresent()) {
      return Result.err(ErrorCode.USER_EMAIL_ALREADY_EXISTS);
    }

    UserEntity user = userMapper.userRequestDTOToEntity(userRequestDTO);
    user.setPassword(encoder.encode(user.getPassword()));

    AccountEntity account = new AccountEntity()
        .setName("Default Account")
        .setBalance(BigDecimal.ZERO)
        .setUser(user);

    user.setAccount(account);
    UserEntity savedUser = userRepository.save(user);

    UserResponseDTO userResponseDTO = userMapper.userEntityToUserResponseDto(savedUser);
    return Result.ok(userResponseDTO);
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

  public void update(UserEntity user) {
    userRepository.save(user);
  }

  public boolean deleteUser(UserEntity currentUser) {
    Optional<UserEntity> userExist = userRepository.findByEmail(currentUser.getEmail());

    if(userExist.isEmpty()) {
      return false;
    }
    userExist.get().deletedUser();

    userRepository.save(userExist.get());
    return true;
  }
}
