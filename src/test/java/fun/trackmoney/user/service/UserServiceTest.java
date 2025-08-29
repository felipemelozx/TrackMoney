package fun.trackmoney.user.service;

import fun.trackmoney.account.service.AccountService;
import fun.trackmoney.auth.dto.internal.AuthError;
import fun.trackmoney.auth.dto.internal.UserRegisterFailure;
import fun.trackmoney.auth.dto.internal.UserRegisterResult;
import fun.trackmoney.auth.dto.internal.UserRegisterSuccess;
import fun.trackmoney.user.dtos.UserRequestDTO;
import fun.trackmoney.user.dtos.UserResponseDTO;
import fun.trackmoney.user.entity.UserEntity;
import fun.trackmoney.user.mapper.UserMapper;
import fun.trackmoney.user.repository.UserRepository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

  @Mock
  private UserRepository userRepository;

  @Mock
  private AccountService accountService;

  @Mock
  private UserMapper userMapper;

  @Mock
  private PasswordEncoder passwordEncoder;

  @InjectMocks
  private UserService userService;

  @Test
  void register_ValidUser_ReturnsUserResponseDTO() {
    // Arrange
    UserRequestDTO requestDTO = new UserRequestDTO("name", "test@example.com", "StrongPassword123#");
    UUID uuid = UUID.randomUUID();
    UserEntity entityToSave = new UserEntity(null, "name", "test@example.com", "StrongPassword123#");
    UserEntity savedEntityReturn = new UserEntity(uuid, "name", "test@example.com", "encodedPass");

    UserResponseDTO mockDTO = new UserResponseDTO(savedEntityReturn.getUserId(), savedEntityReturn.getName(), savedEntityReturn.getEmail());
    UserRegisterSuccess expectedResponse = new UserRegisterSuccess(mockDTO);


    when(userMapper.userRequestDTOToEntity(requestDTO)).thenReturn(entityToSave);
    when(passwordEncoder.encode("StrongPassword123#")).thenReturn("encodedPass");
    when(userRepository.save(entityToSave)).thenReturn(savedEntityReturn);
    when(userMapper.userEntityToUserResponseDto(savedEntityReturn)).thenReturn(mockDTO);
    when(accountService.createAccount(any())).thenReturn(null);

    // Act
    UserRegisterResult actualResponse = userService.register(requestDTO);

    // Assert
    assertNotNull(actualResponse);
    assertInstanceOf(UserRegisterSuccess.class, expectedResponse);
    verify(userRepository, times(1)).save(entityToSave);
  }


  @Test
  void register_EmailAlreadyExists_ThrowsEmailAlreadyExistsException() {
    // Arrange
    UserRequestDTO requestDTO = new UserRequestDTO("name", "duplicate@example.com", "StrongPassword123#");
    UUID uuid = UUID.randomUUID();
    UserEntity existingEntity = new UserEntity(uuid, requestDTO.name(), requestDTO.email(), "encodedPass");
    UserRegisterFailure reponseExpected = new UserRegisterFailure(AuthError.EMAIL_ALREADY_EXISTS);
    when(userService.findUserByEmail(requestDTO.email())).thenReturn(Optional.of(existingEntity));

    // Act
    UserRegisterResult result = userService.register(requestDTO);

    // Assert
    assertInstanceOf(UserRegisterFailure.class, result);
    UserRegisterFailure resultCast = (UserRegisterFailure)result;
    assertEquals(reponseExpected.errorList().getMessage(), resultCast.errorList().getMessage());
    verify(userRepository, times(0)).save(any());
  }

/*x
  @Test
  void findUserByEmail_EmailNotFound_ThrowsEmailNotFoundException() {
    // Arrange
    LoginRequestDTO requestDTO = new LoginRequestDTO("duplicate@example.com", "StrongPassword123#");
    when(userRepository.findByEmail(requestDTO.email())).thenReturn(Optional.empty());
    // Act & Assert
    assertThrows(UserNotFoundException.class, () -> userService.findUserByEmail(requestDTO));
  }

  @Test
  void findUserByEmail_ValidUser_ReturnsUserResponseDT() {
    // Arrange
    LoginRequestDTO requestDTO = new LoginRequestDTO("duplicate@example.com", "StrongPassword123#");
    UUID uuid = UUID.randomUUID();
    UserEntity entityToSave = new UserEntity(uuid, "name", "duplicate@example.com", "StrongPassword123#");

    when(userRepository.findByEmail(requestDTO.email())).thenReturn(Optional.of(entityToSave));
    // Act
    UserEntity responseDTO = userService.findUserByEmail(requestDTO);
    //Assert
    assertEquals(entityToSave.getUserId(), responseDTO.getUserId());
    assertEquals(entityToSave.getEmail(), responseDTO.getEmail());
    assertEquals(entityToSave.getPassword(), responseDTO.getPassword());
    assertEquals(entityToSave.getName(), responseDTO.getName());
  }

  @Test
  void findUserById_ValidUser_ReturnsUserResponseDT() {
    // Arrange
    UUID uuid = UUID.randomUUID();
    UserEntity entityToSave = new UserEntity(uuid, "name", "duplicate@example.com", "StrongPassword123#");

    when(userRepository.findById(uuid)).thenReturn(Optional.of(entityToSave));
    // Act
    UserEntity responseDTO = userService.findUserById(uuid);
    //Assert
    assertEquals(entityToSave.getUserId(), responseDTO.getUserId());
    assertEquals(entityToSave.getEmail(), responseDTO.getEmail());
    assertEquals(entityToSave.getPassword(), responseDTO.getPassword());
    assertEquals(entityToSave.getName(), responseDTO.getName());
  }

  @Test
  void findUserById_EmailNotFound_ThrowsEmailNotFoundException() {
    // Arrange
    UUID uuid = UUID.randomUUID();
    when(userRepository.findById(uuid)).thenReturn(Optional.empty());
    // Act & Assert
    assertThrows(UserNotFoundException.class, () -> userService.findUserById(uuid));
  }*/
}
