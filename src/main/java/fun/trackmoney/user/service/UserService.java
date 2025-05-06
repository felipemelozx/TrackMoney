package fun.trackmoney.user.service;

import fun.trackmoney.user.dtos.UserRequestDTO;
import fun.trackmoney.user.dtos.UserResponseDTO;
import fun.trackmoney.user.entity.UserEntity;
import fun.trackmoney.user.exception.EmailAlreadyExistsException;
import fun.trackmoney.user.exception.PasswordNotValid;
import fun.trackmoney.user.mapper.UserMapper;
import fun.trackmoney.user.repository.UserRepository;
import fun.trackmoney.utils.CustomFieldError;
import fun.trackmoney.utils.PasswordCheck;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UserService {

  private final UserRepository userRepository;
  private final UserMapper userMapper;
  private final PasswordEncoder encoder;

  public UserService(UserRepository userRepository, UserMapper userMapper, PasswordEncoder encoder) {
    this.userRepository = userRepository;
    this.userMapper = userMapper;
    this.encoder = encoder;
  }


  public UserResponseDTO register(UserRequestDTO userRequestDTO) {
    List<CustomFieldError> passwordIsValid = PasswordCheck.validatePasswordEmail(userRequestDTO.password());
    if(!passwordIsValid.isEmpty()) {
      throw new PasswordNotValid(passwordIsValid);
    }
    UserEntity user = userMapper.userRequestDTOToEntity(userRequestDTO);
    user.setPassword(encoder.encode(user.getPassword()));
    try {
      return userMapper.userEntityToUserResponseDto(userRepository.save(user));
    } catch (RuntimeException e) {
      throw new EmailAlreadyExistsException("Email already registered.");
    }
  }
}
