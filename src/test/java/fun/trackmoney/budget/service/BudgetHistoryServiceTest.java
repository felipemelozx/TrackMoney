package fun.trackmoney.budget.service;

import fun.trackmoney.budget.entity.BudgetHistoryEntity;
import fun.trackmoney.budget.entity.BudgetsEntity;
import fun.trackmoney.budget.enums.BudgetStatus;
import fun.trackmoney.budget.repository.BudgetHistoryRepository;
import fun.trackmoney.budget.repository.BudgetsRepository;
import fun.trackmoney.category.entity.CategoryEntity;
import fun.trackmoney.enums.TransactionType;
import fun.trackmoney.testutils.BudgetsEntityFactory;
import fun.trackmoney.testutils.CategoryEntityFactory;
import fun.trackmoney.testutils.TransactionEntityFactory;
import fun.trackmoney.testutils.UserEntityFactory;
import fun.trackmoney.transaction.entity.TransactionEntity;
import fun.trackmoney.transaction.repository.TransactionRepository;
import fun.trackmoney.user.entity.UserEntity;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BudgetHistoryServiceTest {

  @Mock
  private BudgetHistoryRepository budgetHistoryRepository;
  @Mock
  private BudgetsRepository budgetsRepository;
  @Mock
  private TransactionRepository transactionRepository;

  @InjectMocks
  private BudgetHistoryService budgetHistoryService;

  @Test
  void generateHistoryForMonth_shouldReturnZeroWhenHistoryExists() {
    UserEntity user = UserEntityFactory.defaultUser();
    int month = 1;
    int year = 2025;

    when(budgetHistoryRepository.existsHistoryForAccountAndMonth(
        user.getAccount().getAccountId(), (short) month, year))
        .thenReturn(true);

    int result = budgetHistoryService.generateHistoryForMonth(user, month, year);

    assertEquals(0, result);
    verify(budgetHistoryRepository, never()).save(any());
  }

  @Test
  void generateHistoryForMonth_shouldGenerateHistorySuccessfully() {
    UserEntity user = UserEntityFactory.defaultUser();
    CategoryEntity category = CategoryEntityFactory.defaultCategory();
    BudgetsEntity budget = new BudgetsEntity()
        .setBudgetId(1)
        .setAccount(user.getAccount())
        .setCategory(category)
        .setPercent((short) 20);

    int month = 1;
    int year = 2025;

    when(budgetHistoryRepository.existsHistoryForAccountAndMonth(
        user.getAccount().getAccountId(), (short) month, year))
        .thenReturn(false);

    when(budgetsRepository.findAllByAccountAccountId(user.getAccount().getAccountId()))
        .thenReturn(List.of(budget));

    LocalDateTime startDate = LocalDateTime.of(2025, 1, 1, 0, 0);
    LocalDateTime endDate = LocalDateTime.of(2025, 1, 31, 23, 59, 59);

    TransactionEntity income = new TransactionEntity()
        .setAmount(BigDecimal.valueOf(5000))
        .setTransactionType(TransactionType.INCOME);

    TransactionEntity expense = new TransactionEntity()
        .setAmount(BigDecimal.valueOf(800))
        .setTransactionType(TransactionType.EXPENSE)
        .setCategory(category);

    when(transactionRepository.findAllByAccountIdAndDateRange(
        user.getAccount().getAccountId(), startDate, endDate))
        .thenReturn(List.of(income, expense));

    when(transactionRepository.sumExpensesByCategoryAndDateRange(
        user.getAccount().getAccountId(), category.getCategoryId(), startDate, endDate))
        .thenReturn(BigDecimal.valueOf(800));

    when(budgetHistoryRepository.save(any(BudgetHistoryEntity.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));

    int result = budgetHistoryService.generateHistoryForMonth(user, month, year);

    assertEquals(1, result);
    verify(budgetHistoryRepository).save(any(BudgetHistoryEntity.class));
  }

  @Test
  void generateHistoryForMonth_shouldHandleEmptyBudgets() {
    UserEntity user = UserEntityFactory.defaultUser();
    int month = 1;
    int year = 2025;

    when(budgetHistoryRepository.existsHistoryForAccountAndMonth(
        user.getAccount().getAccountId(), (short) month, year))
        .thenReturn(false);

    when(budgetsRepository.findAllByAccountAccountId(user.getAccount().getAccountId()))
        .thenReturn(List.of());

    int result = budgetHistoryService.generateHistoryForMonth(user, month, year);

    assertEquals(0, result);
    verify(budgetHistoryRepository, never()).save(any());
  }

  @Test
  void getAllHistory_shouldReturnListOfHistory() {
    UserEntity user = UserEntityFactory.defaultUser();

    BudgetHistoryEntity history1 = createMockHistory(1, (short) 1, 2025);
    BudgetHistoryEntity history2 = createMockHistory(2, (short) 2, 2025);

    when(budgetHistoryRepository
        .findByAccountAccountIdOrderByReferenceYearDescReferenceMonthDesc(user.getAccount().getAccountId()))
        .thenReturn(List.of(history2, history1));

    List<BudgetHistoryEntity> result = budgetHistoryService.getAllHistory(user);

    assertEquals(2, result.size());
    assertEquals(2025, result.get(0).getReferenceYear());
    assertEquals((short) 2, result.get(0).getReferenceMonth());
  }

  @Test
  void getHistoryByDateRange_shouldReturnFilteredHistory() {
    UserEntity user = UserEntityFactory.defaultUser();

    BudgetHistoryEntity history1 = createMockHistory(1, (short) 1, 2025);
    BudgetHistoryEntity history2 = createMockHistory(2, (short) 2, 2025);
    BudgetHistoryEntity history3 = createMockHistory(3, (short) 3, 2025);

    when(budgetHistoryRepository.findByAccountAndDateRange(
        user.getAccount().getAccountId(), 2025, (short) 1, 2025, (short) 2))
        .thenReturn(List.of(history2, history1));

    List<BudgetHistoryEntity> result = budgetHistoryService.getHistoryByDateRange(
        user, (short) 1, 2025, (short) 2, 2025
    );

    assertEquals(2, result.size());
    assertTrue(result.stream().allMatch(h -> h.getReferenceMonth() <= 2));
  }

  @Test
  void recoverMissingHistory_shouldHandleMissingMonths() {
    when(budgetsRepository.findAll())
        .thenReturn(List.of());

    assertDoesNotThrow(() -> budgetHistoryService.recoverMissingHistory());
  }

  private BudgetHistoryEntity createMockHistory(Integer id, Short month, Integer year) {
    CategoryEntity category = CategoryEntityFactory.defaultCategory();
    UserEntity user = UserEntityFactory.defaultUser();
    BudgetsEntity budget = new BudgetsEntity()
        .setBudgetId(id)
        .setAccount(user.getAccount())
        .setCategory(category)
        .setPercent((short) 20);

    return new BudgetHistoryEntity()
        .setHistoryId(id)
        .setBudget(budget)
        .setAccount(user.getAccount())
        .setCategory(category)
        .setReferenceMonth(month)
        .setReferenceYear(year)
        .setPercent((short) 20)
        .setTargetAmount(BigDecimal.valueOf(1000))
        .setSpentAmount(BigDecimal.valueOf(800))
        .setRemainingAmount(BigDecimal.valueOf(200))
        .setTotalIncome(BigDecimal.valueOf(5000))
        .setPercentageUsed(BigDecimal.valueOf(80.00))
        .setStatus(BudgetStatus.WITHIN_LIMIT);
  }
}
