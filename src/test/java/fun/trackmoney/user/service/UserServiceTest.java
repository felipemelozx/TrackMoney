package fun.trackmoney.user.service;

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

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

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

  @BeforeEach
  void setup() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  void register_ValidUser_ReturnsUserResponseDTO() {
    // Arrange
    UserRequestDTO requestDTO = new UserRequestDTO("name", "test@example.com", "StrongPassword123#");
    UUID uuid = UUID.randomUUID();
    UserEntity entityToSave = new UserEntity(uuid, "name", "test@example.com", "StrongPassword123#");
    UserEntity savedEntity = new UserEntity(uuid, "name", "test@example.com", "encodedPass");
    UserResponseDTO expectedResponse = new UserResponseDTO(uuid, "name", "test@example.com");

    when(userMapper.userRequestDTOToEntity(requestDTO)).thenReturn(entityToSave);
    when(passwordEncoder.encode("StrongPassword123#")).thenReturn("encodedPass");
    when(userRepository.save(entityToSave)).thenReturn(savedEntity);
    when(userMapper.userEntityToUserResponseDto(savedEntity)).thenReturn(expectedResponse);
    when(accountService.createAccount(any())).thenReturn(null);

    // Act
    UserResponseDTO actualResponse = userService.register(requestDTO);

    // Assert
    assertNotNull(actualResponse);
    assertEquals(expectedResponse, actualResponse);
    verify(userRepository, times(1)).save(entityToSave);
  }


  @Test
  void register_InvalidPassword_ThrowsPasswordNotValid() {
    // Arrange
    UserRequestDTO requestDTO = new UserRequestDTO("name", "test@example.com", "123");

    // Força erro de validação — senhas fracas retornam lista de erros
    when(userMapper.userRequestDTOToEntity(any())).thenReturn(null);
    when(userRepository.save(any())).thenReturn(null);

    // Act & Assert
    PasswordNotValid exception = assertThrows(PasswordNotValid.class, () -> userService.register(requestDTO));
    assertFalse(exception.getErrors().isEmpty());
    verify(userRepository, never()).save(any());
  }


  @Test
  void register_EmailAlreadyExists_ThrowsEmailAlreadyExistsException() {
    // Arrange
    UserRequestDTO requestDTO = new UserRequestDTO("name", "duplicate@example.com", "StrongPassword123#");
    UUID uuid = UUID.randomUUID();
    UserEntity entityToSave = new UserEntity(uuid, "name", "duplicate@example.com", "StrongPassword123#");

    when(userMapper.userRequestDTOToEntity(requestDTO)).thenReturn(entityToSave);
    when(passwordEncoder.encode("StrongPassword123#")).thenReturn("encodedPass");
    when(userRepository.save(entityToSave)).thenThrow(new RuntimeException("Unique constraint"));

    // Act & Assert
    assertThrows(EmailAlreadyExistsException.class, () -> userService.register(requestDTO));
    verify(userRepository).save(entityToSave);
  }

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
  }
}
