package fun.trackmoney.budget.service;

import fun.trackmoney.budget.dtos.BudgetHistoryResponseDTO;
import fun.trackmoney.budget.dtos.GenerationResultDTO;
import fun.trackmoney.budget.entity.BudgetHistoryEntity;
import fun.trackmoney.budget.entity.BudgetsEntity;
import fun.trackmoney.budget.enums.BudgetStatus;
import fun.trackmoney.budget.mapper.BudgetHistoryMapper;
import fun.trackmoney.budget.repository.BudgetHistoryRepository;
import fun.trackmoney.budget.repository.BudgetsRepository;
import fun.trackmoney.category.entity.CategoryEntity;
import fun.trackmoney.enums.TransactionType;
import fun.trackmoney.testutils.BudgetsEntityFactory;
import fun.trackmoney.testutils.CategoryEntityFactory;
import fun.trackmoney.testutils.TransactionEntityFactory;
import fun.trackmoney.testutils.UserEntityFactory;
import fun.trackmoney.transaction.dto.TransactionSimpleDTO;
import fun.trackmoney.transaction.entity.TransactionEntity;
import fun.trackmoney.transaction.mapper.TransactionSimpleMapper;
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
  @Mock
  private TransactionSimpleMapper transactionSimpleMapper;
  @Mock
  private BudgetHistoryMapper budgetHistoryMapper;

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

    GenerationResultDTO result = budgetHistoryService.generateHistoryForMonth(user, month, year);

    assertTrue(result.isAlreadyExists());
    assertEquals(0, result.generatedCount());
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

    when(transactionRepository.existsByAccountIdAndDateRange(
        user.getAccount().getAccountId(), startDate, endDate))
        .thenReturn(true);

    when(transactionRepository.sumExpensesByCategoryAndDateRange(
        user.getAccount().getAccountId(), category.getCategoryId(), startDate, endDate))
        .thenReturn(BigDecimal.valueOf(800));

    when(budgetHistoryRepository.save(any(BudgetHistoryEntity.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));

    GenerationResultDTO result = budgetHistoryService.generateHistoryForMonth(user, month, year);

    assertTrue(result.isSuccess());
    assertEquals(1, result.generatedCount());
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

    LocalDateTime startDate = LocalDateTime.of(2025, 1, 1, 0, 0);
    LocalDateTime endDate = LocalDateTime.of(2025, 1, 31, 23, 59, 59);

    when(transactionRepository.existsByAccountIdAndDateRange(
        user.getAccount().getAccountId(), startDate, endDate))
        .thenReturn(true);

    GenerationResultDTO result = budgetHistoryService.generateHistoryForMonth(user, month, year);

    assertEquals(0, result.generatedCount());
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

    List<BudgetHistoryEntity> result = budgetHistoryService.getAllHistory(user, null);

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
        user, (short) 1, 2025, (short) 2, 2025, null
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

  @Test
  void enrichWithTransactions_shouldLimitTo5Transactions() {
    CategoryEntity category = CategoryEntityFactory.defaultCategory();
    UserEntity user = UserEntityFactory.defaultUser();
    BudgetHistoryEntity history = createMockHistory(1, (short) 1, 2025);

    // Create 10 transactions
    List<TransactionEntity> transactions = List.of(
        new TransactionEntity(1, user.getAccount(), category, TransactionType.EXPENSE,
            BigDecimal.valueOf(100), "Transaction 1", LocalDateTime.of(2025, 1, 15, 10, 0)),
        new TransactionEntity(2, user.getAccount(), category, TransactionType.EXPENSE,
            BigDecimal.valueOf(100), "Transaction 2", LocalDateTime.of(2025, 1, 14, 10, 0)),
        new TransactionEntity(3, user.getAccount(), category, TransactionType.EXPENSE,
            BigDecimal.valueOf(100), "Transaction 3", LocalDateTime.of(2025, 1, 13, 10, 0)),
        new TransactionEntity(4, user.getAccount(), category, TransactionType.EXPENSE,
            BigDecimal.valueOf(100), "Transaction 4", LocalDateTime.of(2025, 1, 12, 10, 0)),
        new TransactionEntity(5, user.getAccount(), category, TransactionType.EXPENSE,
            BigDecimal.valueOf(100), "Transaction 5", LocalDateTime.of(2025, 1, 11, 10, 0)),
        new TransactionEntity(6, user.getAccount(), category, TransactionType.EXPENSE,
            BigDecimal.valueOf(100), "Transaction 6", LocalDateTime.of(2025, 1, 10, 10, 0)),
        new TransactionEntity(7, user.getAccount(), category, TransactionType.EXPENSE,
            BigDecimal.valueOf(100), "Transaction 7", LocalDateTime.of(2025, 1, 9, 10, 0)),
        new TransactionEntity(8, user.getAccount(), category, TransactionType.EXPENSE,
            BigDecimal.valueOf(100), "Transaction 8", LocalDateTime.of(2025, 1, 8, 10, 0)),
        new TransactionEntity(9, user.getAccount(), category, TransactionType.EXPENSE,
            BigDecimal.valueOf(100), "Transaction 9", LocalDateTime.of(2025, 1, 7, 10, 0)),
        new TransactionEntity(10, user.getAccount(), category, TransactionType.EXPENSE,
            BigDecimal.valueOf(100), "Transaction 10", LocalDateTime.of(2025, 1, 6, 10, 0))
    );

    when(transactionRepository.findExpensesByCategoryAndDateRange(
        anyInt(), anyInt(), any(), any()))
        .thenReturn(transactions);

    TransactionSimpleDTO dto1 = new TransactionSimpleDTO(1, "Transaction 1",
        BigDecimal.valueOf(100), LocalDateTime.of(2025, 1, 15, 10, 0));
    TransactionSimpleDTO dto2 = new TransactionSimpleDTO(2, "Transaction 2",
        BigDecimal.valueOf(100), LocalDateTime.of(2025, 1, 14, 10, 0));
    TransactionSimpleDTO dto3 = new TransactionSimpleDTO(3, "Transaction 3",
        BigDecimal.valueOf(100), LocalDateTime.of(2025, 1, 13, 10, 0));
    TransactionSimpleDTO dto4 = new TransactionSimpleDTO(4, "Transaction 4",
        BigDecimal.valueOf(100), LocalDateTime.of(2025, 1, 12, 10, 0));
    TransactionSimpleDTO dto5 = new TransactionSimpleDTO(5, "Transaction 5",
        BigDecimal.valueOf(100), LocalDateTime.of(2025, 1, 11, 10, 0));

    when(transactionSimpleMapper.entityListToSimpleDTOList(any()))
        .thenReturn(List.of(dto1, dto2, dto3, dto4, dto5));

    BudgetHistoryResponseDTO mockResponse = new BudgetHistoryResponseDTO(
        1, 1, category, (short) 1, 2025, (short) 20,
        BigDecimal.valueOf(1000), BigDecimal.valueOf(800), BigDecimal.valueOf(200),
        BigDecimal.valueOf(5000), List.of(dto1, dto2, dto3, dto4, dto5),
        BudgetStatus.WITHIN_LIMIT, null
    );

    when(budgetHistoryMapper.entityToResponseDTO(any())).thenReturn(mockResponse);

    List<BudgetHistoryResponseDTO> result = budgetHistoryService.enrichWithTransactions(
        List.of(history)
    );

    assertEquals(1, result.size());
    assertEquals(5, result.get(0).transactions().size());
    assertEquals("Transaction 1", result.get(0).transactions().get(0).transactionName());

    // Verify that mapper was called with only 5 transactions (limited list)
    verify(transactionSimpleMapper).entityListToSimpleDTOList(argThat(list ->
        list != null && list.size() == 5));
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
        .setStatus(BudgetStatus.WITHIN_LIMIT);
  }
}
