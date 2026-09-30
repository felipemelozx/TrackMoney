package fun.trackmoney.service;
import fun.trackmoney.service.UserService;


import fun.trackmoney.dto.auth.internal.AuthError;
import fun.trackmoney.dto.auth.internal.register.UserRegisterFailure;
import fun.trackmoney.dto.auth.internal.register.UserRegisterResult;
import fun.trackmoney.dto.auth.internal.register.UserRegisterSuccess;
import fun.trackmoney.dto.user.UserRequestDTO;
import fun.trackmoney.dto.user.UserResponseDTO;
import fun.trackmoney.entity.AccountEntity;
import fun.trackmoney.entity.BudgetHistoryEntity;
import fun.trackmoney.entity.BudgetsEntity;
import fun.trackmoney.entity.PotsEntity;
import fun.trackmoney.entity.RecurringEntity;
import fun.trackmoney.entity.TransactionEntity;
import fun.trackmoney.entity.UserEntity;
import fun.trackmoney.enums.ColorPick;
import fun.trackmoney.enums.Frequency;
import fun.trackmoney.enums.TransactionType;
import fun.trackmoney.mapper.UserMapper;
import fun.trackmoney.repository.AccountRepository;
import fun.trackmoney.repository.BudgetHistoryRepository;
import fun.trackmoney.repository.BudgetsRepository;
import fun.trackmoney.repository.PotsRepository;
import fun.trackmoney.repository.RecurringRepository;
import fun.trackmoney.repository.TransactionRepository;
import fun.trackmoney.repository.UserRepository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import fun.trackmoney.testutils.AccountEntityFactory;
import fun.trackmoney.testutils.BudgetHistoryEntityFactory;
import fun.trackmoney.testutils.BudgetsEntityFactory;
import fun.trackmoney.testutils.CategoryEntityFactory;
import fun.trackmoney.testutils.PotsEntityFactory;
import fun.trackmoney.testutils.RecurringEntityFactory;
import fun.trackmoney.testutils.TransactionEntityFactory;
import fun.trackmoney.testutils.UserEntityFactory;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

  @Mock
  private UserRepository userRepository;

  @Mock
  private AccountRepository accountRepository;

  @Mock
  private UserMapper userMapper;

  @Mock
  private PasswordEncoder passwordEncoder;

  @Mock
  private TransactionRepository transactionRepository;

  @Mock
  private PotsRepository potsRepository;

  @Mock
  private BudgetsRepository budgetsRepository;

  @Mock
  private BudgetHistoryRepository budgetHistoryRepository;

  @Mock
  private RecurringRepository recurringRepository;

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

  @Test
  void deleteAccount_shouldDeleteUserDataAndReturnTrue_whenPasswordIsValid() {
    AccountEntity account = AccountEntityFactory.defaultAccount();
    UserEntity user = UserEntityFactory.customUser(
        UUID.randomUUID(), "John Doe", "johndoe@example.com", "password123", true, account);

    when(userRepository.findById(user.getUserId())).thenReturn(Optional.of(user));
    when(passwordEncoder.matches("password123", user.getPassword())).thenReturn(true);

    boolean result = userService.deleteAccount(user, "password123");

    assertTrue(result);
    verify(accountRepository).deleteLegacyTransfersByAccountId(account.getAccountId());
    verify(accountRepository).deleteLegacyReportsByAccountId(account.getAccountId());
    verify(budgetHistoryRepository).deleteAllByAccountAccountId(account.getAccountId());
    verify(budgetsRepository).deleteAllByAccountAccountId(account.getAccountId());
    verify(potsRepository).deleteAllByAccount(account);
    verify(recurringRepository).deleteAllByAccountId(account.getAccountId());
    verify(transactionRepository).deleteAllByAccount(account);
    verify(userRepository).clearAccountReference(user.getUserId());
    verify(accountRepository).deleteByAccountIdDirect(account.getAccountId());
    verify(userRepository).deleteByUserIdDirect(user.getUserId());
  }

  @Test
  void deleteAccount_shouldDeleteAllLinkedData_whenUserHasTransactionsPotsBudgetsRecurringAndBudgetHistory() {
    AccountEntity account = AccountEntityFactory.defaultAccount();
    UserEntity user = UserEntityFactory.customUser(
        UUID.randomUUID(), "John Doe", "johndoe@example.com", "password123", true, account);

    // Dados variados ligados à conta do usuário
    TransactionEntity transaction = TransactionEntityFactory.customTransaction(
        1, account, CategoryEntityFactory.defaultCategory(), TransactionType.EXPENSE,
        BigDecimal.valueOf(100), "Mercado", LocalDateTime.now());
    PotsEntity pot = PotsEntityFactory.customPot(
        1L, "Férias", BigDecimal.valueOf(5000), BigDecimal.valueOf(500), account, ColorPick.DARK_BLUE);
    BudgetsEntity budget = BudgetsEntityFactory.customBudget(
        1, CategoryEntityFactory.defaultCategory(), account, (short) 50);
    RecurringEntity recurring = RecurringEntityFactory.customEntity(
        1L, Frequency.MONTHLY, LocalDateTime.now().plusDays(1), null, account,
        CategoryEntityFactory.defaultCategory(), TransactionType.INCOME,
        BigDecimal.valueOf(150), "Desc", "Netflix");
    BudgetHistoryEntity budgetHistory = BudgetHistoryEntityFactory.defaultBudgetHistory();

    when(userRepository.findById(user.getUserId())).thenReturn(Optional.of(user));
    when(passwordEncoder.matches("password123", user.getPassword())).thenReturn(true);

    boolean result = userService.deleteAccount(user, "password123");

    assertTrue(result);
    verify(accountRepository).deleteLegacyTransfersByAccountId(account.getAccountId());
    verify(accountRepository).deleteLegacyReportsByAccountId(account.getAccountId());
    verify(transactionRepository).deleteAllByAccount(account);
    verify(potsRepository).deleteAllByAccount(account);
    verify(budgetsRepository).deleteAllByAccountAccountId(account.getAccountId());
    verify(budgetHistoryRepository).deleteAllByAccountAccountId(account.getAccountId());
    verify(recurringRepository).deleteAllByAccountId(account.getAccountId());
    verify(userRepository).clearAccountReference(user.getUserId());
    verify(accountRepository).deleteByAccountIdDirect(account.getAccountId());
    verify(userRepository).deleteByUserIdDirect(user.getUserId());
    verifyNoMoreInteractions(userRepository, accountRepository, transactionRepository,
        potsRepository, budgetsRepository, budgetHistoryRepository, recurringRepository);
  }

  @Test
  void deleteAccount_shouldReturnFalse_whenPasswordIsInvalid() {
    AccountEntity account = AccountEntityFactory.defaultAccount();
    UserEntity user = UserEntityFactory.customUser(
        UUID.randomUUID(), "John Doe", "johndoe@example.com", "password123", true, account);

    when(userRepository.findById(user.getUserId())).thenReturn(Optional.of(user));
    when(passwordEncoder.matches("wrong-password", user.getPassword())).thenReturn(false);

    boolean result = userService.deleteAccount(user, "wrong-password");

    assertFalse(result);
    verify(accountRepository, never()).deleteLegacyTransfersByAccountId(anyInt());
    verify(accountRepository, never()).deleteLegacyReportsByAccountId(anyInt());
    verify(budgetHistoryRepository, never()).deleteAllByAccountAccountId(anyInt());
    verify(budgetsRepository, never()).deleteAllByAccountAccountId(anyInt());
    verify(potsRepository, never()).deleteAllByAccount(any());
    verify(recurringRepository, never()).deleteAllByAccountId(anyInt());
    verify(transactionRepository, never()).deleteAllByAccount(any());
    verify(userRepository, never()).clearAccountReference(any());
    verify(accountRepository, never()).deleteByAccountIdDirect(any());
    verify(userRepository, never()).deleteByUserIdDirect(any());
  }

  @Test
  void deleteAccount_shouldReturnFalse_whenUserDoesNotExist() {
    UUID userId = UUID.randomUUID();
    UserEntity user = new UserEntity(userId, "John Doe", "johndoe@example.com", "password123", true);

    when(userRepository.findById(userId)).thenReturn(Optional.empty());

    boolean result = userService.deleteAccount(user, "password123");

    assertFalse(result);
    verify(userRepository, never()).clearAccountReference(any());
    verify(accountRepository, never()).deleteByAccountIdDirect(any());
    verify(userRepository, never()).deleteByUserIdDirect(any());
  }

  @Test
  void changePassword_shouldUpdatePassword_whenCurrentPasswordIsCorrect() {
    UserEntity user = UserEntityFactory.defaultUser();
    String newPassword = "NewStrongPassword123#";

    when(userRepository.findById(user.getUserId())).thenReturn(Optional.of(user));
    when(passwordEncoder.matches("password123", user.getPassword())).thenReturn(true);
    when(passwordEncoder.encode(newPassword)).thenReturn("encodedNewPassword");

    boolean result = userService.changePassword(user, "password123", newPassword);

    assertTrue(result);
    assertEquals("encodedNewPassword", user.getPassword());
    verify(passwordEncoder).encode(newPassword);
    verify(userRepository).save(user);
  }

  @Test
  void changePassword_shouldReturnFalse_whenCurrentPasswordIsIncorrect() {
    UserEntity user = UserEntityFactory.defaultUser();

    when(userRepository.findById(user.getUserId())).thenReturn(Optional.of(user));
    when(passwordEncoder.matches("wrong-password", user.getPassword())).thenReturn(false);

    boolean result = userService.changePassword(user, "wrong-password", "NewStrongPassword123#");

    assertFalse(result);
    assertEquals("password123", user.getPassword());
    verify(passwordEncoder, never()).encode(anyString());
    verify(userRepository, never()).save(any());
  }

  @Test
  void changePassword_shouldReturnFalse_whenUserDoesNotExist() {
    UserEntity user = UserEntityFactory.defaultUser();

    when(userRepository.findById(user.getUserId())).thenReturn(Optional.empty());

    boolean result = userService.changePassword(user, "password123", "NewStrongPassword123#");

    assertFalse(result);
    verify(passwordEncoder, never()).encode(anyString());
    verify(userRepository, never()).save(any());
  }

}
