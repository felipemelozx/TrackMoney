package fun.trackmoney.budget.service;

import fun.trackmoney.account.dtos.AccountResponseDTO;
import fun.trackmoney.account.entity.AccountEntity;
import fun.trackmoney.account.mapper.AccountMapper;
import fun.trackmoney.budget.dtos.BudgetCreateDTO;
import fun.trackmoney.budget.dtos.BudgetResponseDTO;
import fun.trackmoney.budget.dtos.internal.BudgetFailure;
import fun.trackmoney.budget.dtos.internal.BudgetResult;
import fun.trackmoney.budget.dtos.internal.BudgetSuccess;
import fun.trackmoney.budget.entity.BudgetsEntity;
import fun.trackmoney.budget.enums.BudgetError;
import fun.trackmoney.budget.exception.BudgetsNotFoundException;
import fun.trackmoney.budget.mapper.BudgetMapper;
import fun.trackmoney.budget.repository.BudgetCheckProjection;
import fun.trackmoney.budget.repository.BudgetsRepository;
import fun.trackmoney.category.entity.CategoryEntity;
import fun.trackmoney.category.service.CategoryService;
import fun.trackmoney.testutils.AccountResponseDTOFactory;
import fun.trackmoney.testutils.BudgetCreateDTOFactory;
import fun.trackmoney.testutils.BudgetResponseDTOFactory;
import fun.trackmoney.testutils.BudgetsEntityFactory;
import fun.trackmoney.testutils.CategoryEntityFactory;
import fun.trackmoney.testutils.TransactionEntityFactory;
import fun.trackmoney.testutils.TransactionResponseDTOFactory;
import fun.trackmoney.testutils.UserEntityFactory;
import fun.trackmoney.transaction.dto.TransactionResponseDTO;
import fun.trackmoney.transaction.entity.TransactionEntity;
import fun.trackmoney.transaction.service.TransactionService;
import fun.trackmoney.user.dtos.UserResponseDTO;
import fun.trackmoney.user.entity.UserEntity;
import fun.trackmoney.user.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BudgetsServiceTest {

  @Mock
  private BudgetsRepository budgetsRepository;
  @Mock
  private BudgetMapper budgetMapper;
  @Mock
  private TransactionService transactionService;
  @Mock
  private AccountMapper accountMapper;
  @Mock
  private CategoryService categoryService;

  @InjectMocks
  private BudgetsService budgetsService;

  @Test
  void create_shouldReturnBudgetResponseDTO() {
    BudgetCreateDTO createDTO = BudgetCreateDTOFactory.defaultDTO();
    UserEntity userEntity = UserEntityFactory.defaultUser();
    CategoryEntity category = CategoryEntityFactory.defaultCategory();
    BudgetResponseDTO budgetResponseDTO = BudgetResponseDTOFactory.defaultResponse();

    var mockResponseCheck = new BudgetCheckProjection() {
      @Override
      public Boolean getCategoryExists() {
        return false;
      }

      @Override
      public Integer getTotalPercent() {
        return 0;
      }
    };

    when(categoryService.findById(createDTO.categoryId())).thenReturn(category);
    when(budgetsRepository.checkBudget(userEntity.getAccount(), category)).thenReturn(mockResponseCheck);

    BudgetsEntity savedEntity = new BudgetsEntity()
        .setBudgetId(1)
        .setAccount(userEntity.getAccount())
        .setCategory(category)
        .setPercent(createDTO.percent());

    when(budgetsRepository.save(any(BudgetsEntity.class))).thenReturn(savedEntity);
    when(budgetMapper.entityToResponseDTO(savedEntity)).thenReturn(budgetResponseDTO);

    BudgetResult result = budgetsService.create(createDTO, userEntity);

    assertNotNull(result);
    assertInstanceOf(BudgetSuccess.class, result);

    BudgetSuccess budgetSuccess = (BudgetSuccess) result;
    assertEquals(1, budgetSuccess.response().budgetId());
  }


  @Test
  void create_shouldFailWhenCategoryNotFound() {
    BudgetCreateDTO dto = BudgetCreateDTOFactory.defaultDTO();
    UserEntity user = UserEntityFactory.defaultUser();

    when(categoryService.findById(dto.categoryId())).thenReturn(null);

    BudgetResult result = budgetsService.create(dto, user);

    assertInstanceOf(BudgetFailure.class, result);
    BudgetFailure failure = (BudgetFailure) result;

    assertEquals(BudgetError.CATEGORY_NOT_FOUND, failure.reason());
  }


  @Test
  void create_shouldFailWhenBudgetAlreadyExists() {

    BudgetCreateDTO dto = BudgetCreateDTOFactory.defaultDTO();
    UserEntity user = UserEntityFactory.defaultUser();
    CategoryEntity category = CategoryEntityFactory.defaultCategory();

    when(categoryService.findById(dto.categoryId())).thenReturn(category);

    var mockCheck = new BudgetCheckProjection() {
      @Override public Boolean getCategoryExists() { return true; }
      @Override public Integer getTotalPercent() { return 50; }
    };

    when(budgetsRepository.checkBudget(user.getAccount(), category)).thenReturn(mockCheck);

    BudgetResult result = budgetsService.create(dto, user);

    assertInstanceOf(BudgetFailure.class, result);
    BudgetFailure failure = (BudgetFailure) result;

    assertEquals(BudgetError.EXIST_BUDGET, failure.reason());
  }

  @Test
  void create_shouldFailWhenPercentExceedsLimit() {

    BudgetCreateDTO dto = new BudgetCreateDTO(1, (short)60);
    UserEntity user = UserEntityFactory.defaultUser();
    CategoryEntity category = CategoryEntityFactory.defaultCategory();

    when(categoryService.findById(dto.categoryId())).thenReturn(category);

    var mockCheck = new BudgetCheckProjection() {
      @Override public Boolean getCategoryExists() { return false; }
      @Override public Integer getTotalPercent() { return 50; }
    };

    when(budgetsRepository.checkBudget(user.getAccount(), category)).thenReturn(mockCheck);

    BudgetResult result = budgetsService.create(dto, user);

    assertInstanceOf(BudgetFailure.class, result);
    BudgetFailure failure = (BudgetFailure) result;

    assertEquals(BudgetError.PERCENT_LIMIT_EXCEEDED, failure.reason());
  }

  @Test
  void findAllBudgets_shouldReturnBudgetList() {
    UserEntity user = UserEntityFactory.defaultUser();
    Integer accountId = user.getAccount().getAccountId();

    CategoryEntity category = CategoryEntityFactory.defaultCategory();

    BudgetsEntity budget = new BudgetsEntity()
        .setBudgetId(10)
        .setPercent((short)20)
        .setCategory(category)
        .setAccount(user.getAccount());

    when(budgetsRepository.findAllByAccountAccountId(accountId))
        .thenReturn(List.of(budget));

    TransactionResponseDTO t1 = TransactionResponseDTOFactory.defaultTransactionResponse();
    TransactionEntity t2 = TransactionEntityFactory.defaultExpenseNow();

    when(transactionService.getCurrentMonthTransactions())
        .thenReturn(List.of(t2));
    Map<CategoryEntity, List<TransactionResponseDTO>> maps = new HashMap<>();
    maps.put(category, List.of(t1));
    when(transactionService.getLast5TransactionsPerCategory(accountId))
        .thenReturn(maps);

    when(transactionService.getIncome(user.getUserId()))
        .thenReturn(BigDecimal.valueOf(1000));

    when(accountMapper.accountEntityToAccountResponse(user.getAccount()))
        .thenReturn(AccountResponseDTOFactory.defaultAccountResponse());

    List<BudgetResponseDTO> response = budgetsService.findAllBudgets(user);

    assertEquals(1, response.size());
    BudgetResponseDTO dto = response.get(0);

    assertEquals(10, dto.budgetId());
    assertEquals(20, dto.percent());
    assertEquals(BigDecimal.valueOf(200), dto.targetAmount());
    assertEquals(BigDecimal.valueOf(0), dto.currentAmount());
  }

  @Test
  void findAllBudgets_shouldReturnEmptyListWhenNoBudgets() {
    UserEntity user = UserEntityFactory.defaultUser();
    Integer accountId = user.getAccount().getAccountId();

    when(budgetsRepository.findAllByAccountAccountId(accountId))
        .thenReturn(List.of());
    when(transactionService.getLast5TransactionsPerCategory(accountId)).thenReturn(Map.of());
    when(transactionService.getIncome(user.getUserId())).thenReturn(BigDecimal.valueOf(100));

    List<BudgetResponseDTO> result = budgetsService.findAllBudgets(user);

    assertNotNull(result);
    assertTrue(result.isEmpty());
  }

  @Test
  void findById_shouldReturnDTO() {
    UserEntity user = UserEntityFactory.defaultUser();
    BudgetsEntity budget = BudgetsEntityFactory.defaultBudget();
    BudgetResponseDTO responseDTO = BudgetResponseDTOFactory.defaultResponse();

    when(budgetsRepository.findByBudgetIdAndAccount(budget.getBudgetId(), user.getAccount()))
        .thenReturn(Optional.of(budget));

    when(budgetMapper.entityToResponseDTO(budget)).thenReturn(responseDTO);

    BudgetResponseDTO result = budgetsService.findById(budget.getBudgetId(), user);

    assertNotNull(result);
    assertEquals(responseDTO, result);
  }

  @Test
  void findById_shouldReturnNullWhenNotFound() {
    UserEntity user = UserEntityFactory.defaultUser();

    when(budgetsRepository.findByBudgetIdAndAccount(1, user.getAccount()))
        .thenReturn(Optional.empty());

    BudgetResponseDTO result = budgetsService.findById(1, user);

    assertNull(result);
  }


  @Test
  void update_shouldUpdateSuccessfully() {
    BudgetCreateDTO dto = BudgetCreateDTOFactory.defaultDTO();
    UserEntity user = UserEntityFactory.defaultUser();
    CategoryEntity category = CategoryEntityFactory.defaultCategory();

    BudgetsEntity existing = BudgetsEntityFactory.defaultBudget();
    BudgetsEntity saved = BudgetsEntityFactory.defaultBudget();

    BudgetResponseDTO response = BudgetResponseDTOFactory.defaultResponse();

    when(categoryService.findById(dto.categoryId())).thenReturn(category);
    when(budgetsRepository.findByBudgetIdAndAccount(existing.getBudgetId(), user.getAccount()))
        .thenReturn(Optional.of(existing));

    when(budgetsRepository.getTotalPercentExcludingId(user.getAccount(), existing.getBudgetId()))
        .thenReturn(20);

    when(budgetsRepository.save(existing)).thenReturn(saved);
    when(budgetMapper.entityToResponseDTO(saved)).thenReturn(response);

    BudgetResult result = budgetsService.update(dto, existing.getBudgetId(), user);

    assertInstanceOf(BudgetSuccess.class, result);
    assertEquals(response, ((BudgetSuccess) result).response());
  }

  @Test
  void update_shouldFailWhenCategoryNotFound() {
    BudgetCreateDTO dto = BudgetCreateDTOFactory.defaultDTO();
    UserEntity user = UserEntityFactory.defaultUser();

    when(categoryService.findById(dto.categoryId())).thenReturn(null);

    BudgetResult result = budgetsService.update(dto, 1, user);

    assertInstanceOf(BudgetFailure.class, result);
    assertEquals(BudgetError.CATEGORY_NOT_FOUND, ((BudgetFailure) result).reason());
  }

  @Test
  void update_shouldFailWhenBudgetNotFound() {
    BudgetCreateDTO dto = BudgetCreateDTOFactory.defaultDTO();
    UserEntity user = UserEntityFactory.defaultUser();
    CategoryEntity category = CategoryEntityFactory.defaultCategory();

    when(categoryService.findById(dto.categoryId())).thenReturn(category);
    when(budgetsRepository.findByBudgetIdAndAccount(1, user.getAccount()))
        .thenReturn(Optional.empty());

    BudgetResult result = budgetsService.update(dto, 1, user);

    assertInstanceOf(BudgetFailure.class, result);
    assertEquals(BudgetError.BUDGET_NOT_FOUND, ((BudgetFailure) result).reason());
  }

  @Test
  void update_shouldFailWhenPercentExceedsLimit() {
    BudgetCreateDTO dto = new BudgetCreateDTO(1, (short)60);
    UserEntity user = UserEntityFactory.defaultUser();
    CategoryEntity category = CategoryEntityFactory.defaultCategory();
    BudgetsEntity existing = BudgetsEntityFactory.defaultBudget();

    when(categoryService.findById(dto.categoryId())).thenReturn(category);
    when(budgetsRepository.findByBudgetIdAndAccount(existing.getBudgetId(), user.getAccount()))
        .thenReturn(Optional.of(existing));

    when(budgetsRepository.getTotalPercentExcludingId(user.getAccount(), existing.getBudgetId()))
        .thenReturn(50); // 50 + 60 > 100

    BudgetResult result = budgetsService.update(dto, existing.getBudgetId(), user);

    assertInstanceOf(BudgetFailure.class, result);
    assertEquals(BudgetError.PERCENT_LIMIT_EXCEEDED, ((BudgetFailure) result).reason());
  }

  @Test
  void deleteById_shouldCallRepository() {
    UserEntity user = UserEntityFactory.defaultUser();

    budgetsService.deleteById(10, user);

    verify(budgetsRepository).deleteByBudgetIdAndAccount(10, user.getAccount());
  }

  @Test
  void getCurrentAmountWested_shouldSumCorrectly() {
    CategoryEntity category = CategoryEntityFactory.defaultCategory();

    TransactionEntity t1 = new TransactionEntity().setAmount(BigDecimal.valueOf(30)).setCategory(category);
    TransactionEntity t2 = new TransactionEntity().setAmount(BigDecimal.valueOf(20)).setCategory(category);

    int result = budgetsService.getCurrentAmountWested(category, List.of(t1, t2));

    assertEquals(50, result);
  }
  @Test
  void getCurrentAmountWested_shouldReturnZeroWhenNoMatches() {
    CategoryEntity category = CategoryEntityFactory.defaultCategory();

    TransactionEntity t1 = new TransactionEntity().setAmount(BigDecimal.valueOf(30)).setCategory(new CategoryEntity());
    TransactionEntity t2 = new TransactionEntity().setAmount(BigDecimal.valueOf(20)).setCategory(new CategoryEntity());

    int result = budgetsService.getCurrentAmountWested(category, List.of(t1, t2));

    assertEquals(0, result);
  }

}