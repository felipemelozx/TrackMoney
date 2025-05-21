package fun.trackmoney.budget.service;

import fun.trackmoney.account.dtos.AccountResponseDTO;
import fun.trackmoney.account.entity.AccountEntity;
import fun.trackmoney.account.mapper.AccountMapper;
import fun.trackmoney.account.service.AccountService;
import fun.trackmoney.budget.dtos.BudgetCreateDTO;
import fun.trackmoney.budget.dtos.BudgetResponseDTO;
import fun.trackmoney.budget.entity.BudgetsEntity;
import fun.trackmoney.budget.exception.BudgetsNotFoundException;
import fun.trackmoney.budget.mapper.BudgetMapper;
import fun.trackmoney.budget.repository.BudgetsRepository;
import fun.trackmoney.category.entity.CategoryEntity;
import fun.trackmoney.category.service.CategoryService;
import fun.trackmoney.user.dtos.UserResponseDTO;
import fun.trackmoney.user.entity.UserEntity;
import fun.trackmoney.user.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class BudgetsServiceTest {

  @Mock
  private BudgetsRepository budgetsRepository;
  @Mock
  private BudgetMapper budgetMapper;
  @Mock
  private AccountService accountService;
  @Mock
  private AccountMapper accountMapper;
  @Mock
  private CategoryService categoryService;
  @Mock
  private UserService userService;

  @InjectMocks
  private BudgetsService budgetsService;

  private UUID userId;
  private BudgetCreateDTO createDTO;
  private BudgetsEntity budgetEntity;
  private UserEntity userEntity;
  private UserResponseDTO userResponseDTO;
  private AccountResponseDTO accountResponseDTO;
  private AccountEntity accountEntity;
  private CategoryEntity categoryEntity;
  private BudgetResponseDTO budgetResponseDTO;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);

    userId = UUID.randomUUID();
    createDTO = new BudgetCreateDTO(10, userId, 20, BigDecimal.valueOf(1000), 5);
    budgetEntity = new BudgetsEntity();
    userEntity = new UserEntity();
    userResponseDTO = new UserResponseDTO(userId, "testUser", "email@test");
    accountResponseDTO = new AccountResponseDTO(20, userResponseDTO, "true", BigDecimal.valueOf(1000), true);
    accountEntity = new AccountEntity();
    categoryEntity = new CategoryEntity();
    budgetResponseDTO = new BudgetResponseDTO(1, categoryEntity, accountResponseDTO, BigDecimal.valueOf(1000), 5);
  }

  @Test
  void create_shouldReturnBudgetResponseDTO() {
    budgetEntity.setBudgetId(99);
    budgetEntity.setCategory(categoryEntity);
    budgetEntity.setUserEntity(userEntity);
    budgetEntity.setTargetAmount(BigDecimal.valueOf(1234));
    budgetEntity.setResetDay(7);
    budgetEntity.setAccount(accountEntity);

    when(budgetMapper.createDtoTOEntity(createDTO)).thenReturn(budgetEntity);
    when(userService.findUserById(userId)).thenReturn(userEntity);
    when(accountService.findAccountById(20)).thenReturn(accountResponseDTO);
    when(accountMapper.accountResponseToEntity(accountResponseDTO)).thenReturn(accountEntity);
    when(categoryService.findById(10)).thenReturn(categoryEntity);
    when(budgetsRepository.save(budgetEntity)).thenReturn(budgetEntity);
    when(budgetMapper.entityToResponseDTO(budgetEntity)).thenReturn(budgetResponseDTO);

    BudgetResponseDTO result = budgetsService.create(createDTO);

    assertNotNull(result);
    assertEquals(99, budgetEntity.getBudgetId());
    assertEquals(categoryEntity, budgetEntity.getCategory());
    assertEquals(userEntity, budgetEntity.getUserEntity());
    assertEquals(BigDecimal.valueOf(1234), budgetEntity.getTargetAmount());
    assertEquals(7, budgetEntity.getResetDay());
    assertEquals(accountEntity, budgetEntity.getAccount());
    assertEquals(budgetResponseDTO, result);
    verify(budgetsRepository, times(1)).save(budgetEntity);
    verify(budgetMapper, times(1)).createDtoTOEntity(createDTO);
    verify(userService, times(1)).findUserById(userId);
    verify(accountService, times(1)).findAccountById(20);
    verify(accountMapper, times(1)).accountResponseToEntity(accountResponseDTO);
    verify(categoryService, times(1)).findById(10);
    verify(budgetMapper, times(1)).entityToResponseDTO(budgetEntity);
  }

  @Test
  void findAll_shouldReturnEmptyList_whenNoBudgetsExist() {
    when(budgetsRepository.findAll()).thenReturn(Collections.emptyList());
    when(budgetMapper.entityListToResponseList(Collections.emptyList())).thenReturn(Collections.emptyList());

    List<BudgetResponseDTO> result = budgetsService.findAll();

    assertNotNull(result);
    assertTrue(result.isEmpty());
    verify(budgetsRepository, times(1)).findAll();
    verify(budgetMapper, times(1)).entityListToResponseList(Collections.emptyList());
  }

  @Test
  void findAll_shouldReturnListOfBudgetResponseDTO() {
    BudgetsEntity entity = new BudgetsEntity();
    BudgetResponseDTO dto = new BudgetResponseDTO(1, new CategoryEntity(),
        new AccountResponseDTO(1, userResponseDTO, "test", BigDecimal.valueOf(100), true),
        BigDecimal.TEN, 10);
    List<BudgetsEntity> entities = List.of(entity);
    List<BudgetResponseDTO> dtos = List.of(dto);

    when(budgetsRepository.findAll()).thenReturn(entities);
    when(budgetMapper.entityListToResponseList(entities)).thenReturn(dtos);

    List<BudgetResponseDTO> result = budgetsService.findAll();

    assertNotNull(result);
    assertEquals(1, result.size());
    assertEquals(dtos, result);
    verify(budgetsRepository, times(1)).findAll();
    verify(budgetMapper, times(1)).entityListToResponseList(entities);
  }

  @Test
  void findById_shouldReturnBudgetResponseDTO_whenExists() {
    int budgetId = 1;
    when(budgetsRepository.findById(budgetId)).thenReturn(Optional.of(budgetEntity));
    when(budgetMapper.entityToResponseDTO(budgetEntity)).thenReturn(budgetResponseDTO);

    BudgetResponseDTO result = budgetsService.findById(budgetId);

    assertNotNull(result);
    assertEquals(budgetResponseDTO, result);
    verify(budgetsRepository, times(1)).findById(budgetId);
    verify(budgetMapper, times(1)).entityToResponseDTO(budgetEntity);
  }

  @Test
  void findById_shouldThrowException_whenNotExists() {
    int budgetId = 1;
    when(budgetsRepository.findById(budgetId)).thenReturn(Optional.empty());

    BudgetsNotFoundException exception = assertThrows(BudgetsNotFoundException.class, () -> budgetsService.findById(budgetId));
    assertEquals("Budget not found", exception.getMessage());
    verify(budgetsRepository, times(1)).findById(budgetId);
    verify(budgetMapper, never()).entityToResponseDTO(any());
  }

  @Test
  void update_shouldReturnUpdatedBudgetResponseDTO_whenBudgetExists() {
    int budgetIdToUpdate = 1;
    BudgetCreateDTO updateDTO = new BudgetCreateDTO(11, userId, 21, BigDecimal.valueOf(1500), 6);
    BudgetsEntity existingEntity = new BudgetsEntity();
    existingEntity.setBudgetId(budgetIdToUpdate);
    UserResponseDTO updatedUserResponseDTO = new UserResponseDTO(userId, "updatedUser", "updated@test");
    AccountResponseDTO updatedAccountDTO = new AccountResponseDTO(21, updatedUserResponseDTO, "false", BigDecimal.valueOf(1500), false);
    AccountEntity updatedAccountEntity = new AccountEntity();
    CategoryEntity updatedCategory = new CategoryEntity();
    BudgetResponseDTO updatedResponse = new BudgetResponseDTO(budgetIdToUpdate, updatedCategory,
        updatedAccountDTO, BigDecimal.valueOf(1500), 6);
    BudgetsEntity updatedEntity = new BudgetsEntity();
    updatedEntity.setBudgetId(budgetIdToUpdate);

    when(budgetsRepository.findById(budgetIdToUpdate)).thenReturn(Optional.of(existingEntity));
    when(budgetMapper.createDtoTOEntity(updateDTO)).thenReturn(updatedEntity);
    when(userService.findUserById(userId)).thenReturn(userEntity);
    when(accountService.findAccountById(21)).thenReturn(updatedAccountDTO);
    when(accountMapper.accountResponseToEntity(updatedAccountDTO)).thenReturn(updatedAccountEntity);
    when(categoryService.findById(11)).thenReturn(updatedCategory);
    when(budgetsRepository.save(updatedEntity)).thenReturn(updatedEntity);
    when(budgetMapper.entityToResponseDTO(updatedEntity)).thenReturn(updatedResponse);

    BudgetResponseDTO result = budgetsService.update(updateDTO, budgetIdToUpdate);

    assertNotNull(result);
    assertEquals(updatedResponse, result);
    assertEquals(budgetIdToUpdate, updatedEntity.getBudgetId());
    verify(budgetsRepository, times(1)).findById(budgetIdToUpdate);
    verify(budgetMapper, times(1)).createDtoTOEntity(updateDTO);
    verify(userService, times(1)).findUserById(userId);
    verify(accountService, times(1)).findAccountById(21);
    verify(accountMapper, times(1)).accountResponseToEntity(updatedAccountDTO);
    verify(categoryService, times(1)).findById(11);
    verify(budgetsRepository, times(1)).save(updatedEntity);
    verify(budgetMapper, times(1)).entityToResponseDTO(updatedEntity);
  }

  @Test
  void update_shouldThrowException_whenBudgetNotExists() {
    int budgetIdToUpdate = 1;
    BudgetCreateDTO updateDTO = new BudgetCreateDTO(11, userId, 21, BigDecimal.valueOf(1500), 6);

    when(budgetsRepository.findById(budgetIdToUpdate)).thenReturn(Optional.empty());

    BudgetsNotFoundException exception = assertThrows(BudgetsNotFoundException.class,
        () -> budgetsService.update(updateDTO, budgetIdToUpdate));
    assertEquals("Budget not found", exception.getMessage());
    verify(budgetsRepository, times(1)).findById(budgetIdToUpdate);
    verify(budgetMapper, never()).createDtoTOEntity(any());
    verify(userService, never()).findUserById(any());
    verify(accountService, never()).findAccountById(anyInt());
    verify(accountMapper, never()).accountResponseToEntity(any());
    verify(categoryService, never()).findById(anyInt());
    verify(budgetsRepository, never()).save(any());
    verify(budgetMapper, never()).entityToResponseDTO(any());
  }

  @Test
  void deleteById_shouldCallRepositoryDelete() {
    int budgetId = 5;
    budgetsService.deleteById(budgetId);
    verify(budgetsRepository, times(1)).deleteById(budgetId);
  }
}