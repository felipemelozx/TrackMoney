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
  void shouldRegisterUserSuccessfullyWhenUserRequestIsValid() {
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

    UserRegisterResult actualResponse = userService.register(requestDTO);

    assertNotNull(actualResponse);
    assertInstanceOf(UserRegisterSuccess.class, expectedResponse);
    verify(userRepository, times(1)).save(entityToSave);
  }


  @Test
  void shouldReturnFailureWhenEmailAlreadyExists() {
    UserRequestDTO requestDTO = new UserRequestDTO("name", "duplicate@example.com", "StrongPassword123#");
    UUID uuid = UUID.randomUUID();
    UserEntity existingEntity = new UserEntity(uuid, requestDTO.name(), requestDTO.email(), "encodedPass");
    UserRegisterFailure reponseExpected = new UserRegisterFailure(AuthError.EMAIL_ALREADY_EXISTS);
    when(userService.findUserByEmail(requestDTO.email())).thenReturn(Optional.of(existingEntity));

    UserRegisterResult result = userService.register(requestDTO);

    assertInstanceOf(UserRegisterFailure.class, result);
    UserRegisterFailure resultCast = (UserRegisterFailure)result;
    assertEquals(reponseExpected.errorList().getMessage(), resultCast.errorList().getMessage());
    verify(userRepository, times(0)).save(any());
  }


  @Test
  void shouldReturnEmptyOptionalWhenEmailNotFound() {
    String mockEmail = "mockEmail@com.br";
    when(userRepository.findByEmail(mockEmail)).thenReturn(Optional.empty());

    Optional<UserEntity> response = userService.findUserByEmail(mockEmail);
    assertTrue(response.isEmpty());
  }

  @Test
  void shouldReturnUserWhenEmailExists() {
    String mockEmail = "mockEmail@example.com";
    UUID uuid = UUID.randomUUID();
    UserEntity user = new UserEntity(uuid, "name", mockEmail, "StrongPassword123#", true);

    when(userRepository.findByEmail(mockEmail)).thenReturn(Optional.of(user));

    Optional<UserEntity> response = userService.findUserByEmail(mockEmail);

    assertTrue(response.isPresent());
    UserEntity userResponse = response.get();
    assertEquals(user.getUserId(), userResponse.getUserId());
    assertEquals(user.getEmail(), userResponse.getEmail());
    assertEquals(user.getPassword(), userResponse.getPassword());
    assertEquals(user.getName(), userResponse.getName());
  }

  @Test
  void shouldReturnEmptyOptionalWhenUserIdNotFound() {
    UUID mockUserId = UUID.randomUUID();
    when(userRepository.findById(mockUserId)).thenReturn(Optional.empty());

    Optional<UserEntity> response = userService.findUserById(mockUserId);
    assertTrue(response.isEmpty());
  }

  @Test
  void shouldReturnUserWhenUserIdExists() {
    UUID mockUserId = UUID.randomUUID();
    UserEntity user = new UserEntity(mockUserId, "name", "mock@gmail.com", "StrongPassword123#", true);

    when(userRepository.findById(mockUserId)).thenReturn(Optional.of(user));

    Optional<UserEntity> response = userService.findUserById(mockUserId);

    assertTrue(response.isPresent());
    UserEntity userResponse = response.get();
    assertEquals(user.getUserId(), userResponse.getUserId());
    assertEquals(user.getEmail(), userResponse.getEmail());
    assertEquals(user.getPassword(), userResponse.getPassword());
    assertEquals(user.getName(), userResponse.getName());
  }

  @Test
  void shouldReturnTrueWhenUserIsAbleToActive() {
    String mockEmail = "mock@email.com";
    UserEntity user = new UserEntity(UUID.randomUUID(), "test", mockEmail, "mockPass", false);

    when(userService.findUserByEmail(mockEmail)).thenReturn(Optional.of(user));

    boolean response = userService.activateUser(mockEmail);

    assertTrue(response);
    assertTrue(user.isActive());
    verify(userRepository, times(1)).save(user);
  }

  @Test
  void shouldReturnFalseWhenUserIsNull() {
    String mockEmail = "mock@email.com";

    when(userService.findUserByEmail(mockEmail)).thenReturn(Optional.empty());

    boolean response = userService.activateUser(mockEmail);

    assertFalse(response);
    verify(userRepository, times(0)).save(any());
  }

}
