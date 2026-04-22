package fun.trackmoney.service;

import fun.trackmoney.dto.budget.BudgetHistoryResponseDTO;
import fun.trackmoney.dto.budget.GenerationResultDTO;
import fun.trackmoney.entity.BudgetHistoryEntity;
import fun.trackmoney.entity.BudgetsEntity;
import fun.trackmoney.enums.BudgetStatus;
import fun.trackmoney.mapper.BudgetHistoryMapper;
import fun.trackmoney.repository.BudgetHistoryRepository;
import fun.trackmoney.repository.BudgetsRepository;
import fun.trackmoney.entity.CategoryEntity;
import fun.trackmoney.enums.TransactionType;
import fun.trackmoney.entity.TransactionEntity;
import fun.trackmoney.mapper.TransactionSimpleMapper;
import fun.trackmoney.repository.TransactionRepository;
import fun.trackmoney.entity.UserEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;
import java.util.Objects;

@Service
public class BudgetHistoryService {

  private static final Logger LOG = LoggerFactory.getLogger(BudgetHistoryService.class);
  private static final BigDecimal HUNDRED = BigDecimal.valueOf(100);

  private final BudgetHistoryRepository budgetHistoryRepository;
  private final BudgetsRepository budgetsRepository;
  private final TransactionRepository transactionRepository;
  private final TransactionSimpleMapper transactionSimpleMapper;
  private final BudgetHistoryMapper budgetHistoryMapper;
  private final RecurringService recurringService;

  public BudgetHistoryService(
      BudgetHistoryRepository budgetHistoryRepository,
      BudgetsRepository budgetsRepository,
      TransactionRepository transactionRepository,
      TransactionSimpleMapper transactionSimpleMapper,
      BudgetHistoryMapper budgetHistoryMapper,
      RecurringService recurringService) {
    this.budgetHistoryRepository = budgetHistoryRepository;
    this.budgetsRepository = budgetsRepository;
    this.transactionRepository = transactionRepository;
    this.transactionSimpleMapper = transactionSimpleMapper;
    this.budgetHistoryMapper = budgetHistoryMapper;
    this.recurringService = recurringService;
  }

  /**
   * Scheduled method - runs at midnight on the 1st of each month
   * Generates budget history for the PREVIOUS month
   * Cron: 0 0 0 1 * * (midnight, 1st day of month, every month)
   */
  @Scheduled(cron = "0 0 0 1 * *")
  @Transactional
  public void generateMonthlyHistory() {
    LOG.info("Starting monthly budget history generation");
    YearMonth lastMonth = YearMonth.now().minusMonths(1);
    short month = (short) lastMonth.getMonthValue();
    int year = lastMonth.getYear();

    List<Integer> accountIds = budgetsRepository.findAll()
        .stream()
        .map(b -> b.getAccount().getAccountId())
        .distinct()
        .toList();

    int totalGenerated = 0;
    for (Integer accountId : accountIds) {
      if (!budgetHistoryRepository.existsHistoryForAccountAndMonth(accountId, month, year)) {
        int count = generateHistoryForAccount(accountId, month, year);
        totalGenerated += count;
      }
    }

    LOG.info("Completed monthly budget history generation: {} entries created for {}/{}",
        totalGenerated, month, year);
  }

  /**
   * Recovery method - runs on application startup
   * Checks for missing months and generates history if needed
   */
  @Transactional
  public void recoverMissingHistory() {
    LOG.info("Starting budget history recovery");
    LocalDate now = LocalDate.now();
    LocalDate lastMonth = now.minusMonths(1);

    List<Integer> accountIds = budgetsRepository.findAll()
        .stream()
        .map(b -> b.getAccount().getAccountId())
        .distinct()
        .toList();

    int totalRecovered = 0;
    for (Integer accountId : accountIds) {
      LocalDate checkDate = getOldestBudgetDate(accountId);
      if (checkDate == null) {
        continue;
      }

      while (checkDate.isBefore(lastMonth) || checkDate.equals(lastMonth)) {
        short month = (short) checkDate.getMonthValue();
        int year = checkDate.getYear();

        if (!budgetHistoryRepository.existsHistoryForAccountAndMonth(accountId, month, year)) {
          int count = generateHistoryForAccount(accountId, month, year);
          totalRecovered += count;
        }

        checkDate = checkDate.plusMonths(1);
      }
    }

    LOG.info("Completed budget history recovery: {} entries recovered", totalRecovered);
  }

  /**
   * Manual trigger - generates history for a specific month/year
   * Only generates if:
   * 1. History doesn't already exist for the month/year
   * 2. The month/year is NOT the current month
   * 3. There are transactions in the specified month/year
   */
  @Transactional
  public GenerationResultDTO generateHistoryForMonth(UserEntity currentUser, int month, int year) {
    Integer accountId = currentUser.getAccount().getAccountId();

    // Check if history already exists
    if (budgetHistoryRepository.hasAllBudgetHistoriesForMonth(accountId, (short) month, year)) {
      LOG.info("History already exists for this account");
      return GenerationResultDTO.alreadyExists();
    }

    // Check if it's the current month
    YearMonth now = YearMonth.now();
    YearMonth targetMonth = YearMonth.of(year, month);
    if (targetMonth.equals(now)) {
      LOG.info("Cannot generate history for current month");
      return GenerationResultDTO.currentMonthNotAllowed();
    }

    // Check if there are transactions in the specified month
    LocalDate monthStart = LocalDate.of(year, month, 1);
    LocalDate monthEnd = targetMonth.atEndOfMonth();
    LocalDateTime startDate = monthStart.atStartOfDay();
    LocalDateTime endDate = monthEnd.atTime(23, 59, 59);

    boolean hasTransactions = transactionRepository.existsByAccountIdAndDateRange(
        accountId, startDate, endDate
    );

    if (!hasTransactions) {
      LOG.info("No transactions found for this account in specified period");
      return GenerationResultDTO.noTransactions();
    }

    LOG.info("Generating history for account");
    int count = generateHistoryForAccount(accountId, (short) month, year);
    return GenerationResultDTO.success(count);
  }

  /**
   * Get all history for current user
   * @param currentUser the authenticated user
   * @param categoryId optional category filter
   */
  public List<BudgetHistoryEntity> getAllHistory(UserEntity currentUser, Integer categoryId) {
    Integer accountId = currentUser.getAccount().getAccountId();
    if (categoryId == null) {
      return budgetHistoryRepository.findByAccountAccountIdOrderByReferenceYearDescReferenceMonthDesc(accountId);
    }
    return budgetHistoryRepository.findByAccountAndOptionalCategoryId(accountId, categoryId);
  }

  /**
   * Get history by date range
   * @param currentUser the authenticated user
   * @param startMonth start month (inclusive)
   * @param startYear start year (inclusive)
   * @param endMonth end month (inclusive)
   * @param endYear end year (inclusive)
   * @param categoryId optional category filter
   */
  public List<BudgetHistoryEntity> getHistoryByDateRange(
      UserEntity currentUser,
      Short startMonth,
      Integer startYear,
      Short endMonth,
      Integer endYear,
      Integer categoryId) {
    Integer accountId = currentUser.getAccount().getAccountId();
    if (categoryId == null) {
      return budgetHistoryRepository.findByAccountAndDateRange(
          accountId, startYear, startMonth, endYear, endMonth
      );
    }
    return budgetHistoryRepository.findByAccountAndDateRangeAndOptionalCategoryId(
        accountId, startYear, startMonth, endYear, endMonth, categoryId
    );
  }

  /**
   * Generates budget history for one account for a specific month
   */
  private int generateHistoryForAccount(Integer accountId, short month, int year) {
    List<BudgetsEntity> budgets = budgetsRepository.findAllByAccountAccountId(accountId);

    if (budgets.isEmpty()) {
      return 0;
    }

    LocalDate monthStart = LocalDate.of(year, month, 1);
    LocalDate monthEnd = YearMonth.of(year, month).atEndOfMonth();
    LocalDateTime startDate = monthStart.atStartOfDay();
    LocalDateTime endDate = monthEnd.atTime(23, 59, 59);

    BigDecimal totalIncome = getTotalIncomeForMonth(accountId, startDate, endDate);

    int count = 0;
    for (BudgetsEntity budget : budgets) {
      // Check if history already exists for this budget/month/year
      if (budgetHistoryRepository.findByBudgetAndReferenceMonthAndReferenceYear(
          budget, month, year).isPresent()) {
        continue;
      }

      BudgetHistoryEntity history = calculateBudgetHistory(
          budget, month, year, startDate, endDate, totalIncome
      );

      if (history != null) {
        budgetHistoryRepository.save(history);
        count++;
      }
    }

    return count;
  }

  /**
   * Calculates all fields for a single budget history entry
   */
  private BudgetHistoryEntity calculateBudgetHistory(
      BudgetsEntity budget,
      short month,
      int year,
      LocalDateTime startDate,
      LocalDateTime endDate,
      BigDecimal totalIncome) {

    CategoryEntity category = budget.getCategory();
    Integer accountId = budget.getAccount().getAccountId();
    short percent = budget.getPercent();

    BigDecimal targetAmount = calculateTargetAmount(totalIncome, percent);
    BigDecimal spentAmount = getSpentAmount(accountId, category, startDate, endDate);
    BigDecimal remainingAmount = calculateRemainingAmount(targetAmount, spentAmount);
    BudgetStatus status = determineBudgetStatus(spentAmount, targetAmount);

    return buildBudgetHistoryEntity(budget, category, month, year, percent,
        targetAmount, spentAmount, remainingAmount, totalIncome, status);
  }

  private BigDecimal calculateTargetAmount(BigDecimal totalIncome, short percent) {
    return totalIncome
        .multiply(BigDecimal.valueOf(percent))
        .divide(HUNDRED, 2, RoundingMode.HALF_UP);
  }

  private BigDecimal getSpentAmount(Integer accountId, CategoryEntity category,
                                     LocalDateTime startDate, LocalDateTime endDate) {
    BigDecimal spentAmount = transactionRepository.sumExpensesByCategoryAndDateRange(
        accountId,
        category.getCategoryId(),
        startDate,
        endDate
    );
    return spentAmount != null ? spentAmount : BigDecimal.ZERO;
  }

  private BigDecimal calculateRemainingAmount(BigDecimal targetAmount, BigDecimal spentAmount) {
    return targetAmount.subtract(spentAmount);
  }

  private BudgetStatus determineBudgetStatus(BigDecimal spentAmount, BigDecimal targetAmount) {
    return spentAmount.compareTo(targetAmount) > 0
        ? BudgetStatus.EXCEEDED
        : BudgetStatus.WITHIN_LIMIT;
  }

  private BudgetHistoryEntity buildBudgetHistoryEntity(
      BudgetsEntity budget,
      CategoryEntity category,
      short month,
      int year,
      short percent,
      BigDecimal targetAmount,
      BigDecimal spentAmount,
      BigDecimal remainingAmount,
      BigDecimal totalIncome,
      BudgetStatus status) {
    return new BudgetHistoryEntity()
        .setBudget(budget)
        .setAccount(budget.getAccount())
        .setCategory(category)
        .setReferenceMonth(month)
        .setReferenceYear(year)
        .setPercent(percent)
        .setTargetAmount(targetAmount)
        .setSpentAmount(spentAmount)
        .setRemainingAmount(remainingAmount)
        .setTotalIncome(totalIncome)
        .setStatus(status);
  }

  /**
   * Gets total income for an account within a date range
   * First tries to get from recurring transactions, falls back to individual transactions
   */
  private BigDecimal getTotalIncomeForMonth(
      Integer accountId,
      LocalDateTime startDate,
      LocalDateTime endDate) {

    // Try to get income from recurring transactions first
    BigDecimal recurringIncome = recurringService.getIncomeFromRecurring(accountId);

    if (recurringIncome.compareTo(BigDecimal.ZERO) > 0) {
      return recurringIncome;
    }

    // Fall back to individual transactions if no recurring income found
    List<TransactionEntity> transactions = transactionRepository
        .findAllByAccountIdAndDateRange(accountId, startDate, endDate);

    return transactions.stream()
        .filter(t -> TransactionType.INCOME.equals(t.getTransactionType()))
        .map(TransactionEntity::getAmount)
        .filter(Objects::nonNull)
        .reduce(BigDecimal.ZERO, BigDecimal::add);
  }

  /**
   * Get the oldest budget date for an account
   */
  private LocalDate getOldestBudgetDate(Integer accountId) {
    List<BudgetsEntity> budgets = budgetsRepository.findAllByAccountAccountId(accountId);
    if (budgets.isEmpty()) {
      return null;
    }

    // For now, just return current year - 1 as a safe starting point
    // In a real implementation, you might want to track when budgets were created
    return LocalDate.now().minusMonths(1);
  }

  /**
   * Get expense transactions for a specific category and date range
   * These transactions contribute to the spentAmount in budget history
   */
  private List<TransactionEntity> getExpenseTransactionsForBudget(
      Integer accountId,
      Integer categoryId,
      LocalDateTime startDate,
      LocalDateTime endDate) {
    return transactionRepository.findExpensesByCategoryAndDateRange(
        accountId, categoryId, startDate, endDate
    );
  }

  /**
   * Enrich budget history entities with their associated transactions
   */
  public List<BudgetHistoryResponseDTO> enrichWithTransactions(
      List<BudgetHistoryEntity> historyEntities) {

    return historyEntities.stream()
        .map(entity -> {
          LocalDateTime startDate = LocalDateTime.of(
              entity.getReferenceYear(), entity.getReferenceMonth(), 1, 0, 0);
          LocalDateTime endDate = YearMonth.of(
              entity.getReferenceYear(), entity.getReferenceMonth())
              .atEndOfMonth().atTime(23, 59, 59);

          List<TransactionEntity> transactions = getExpenseTransactionsForBudget(
              entity.getAccount().getAccountId(),
              entity.getCategory().getCategoryId(),
              startDate, endDate
          ).stream()
              .limit(5)
              .toList();

          BudgetHistoryResponseDTO dto = budgetHistoryMapper.entityToResponseDTO(entity);

          return new BudgetHistoryResponseDTO(
              dto.historyId(),
              dto.budgetId(),
              dto.category(),
              dto.referenceMonth(),
              dto.referenceYear(),
              dto.percent(),
              dto.targetAmount(),
              dto.spentAmount(),
              dto.remainingAmount(),
              dto.totalIncome(),
              transactionSimpleMapper.entityListToSimpleDTOList(transactions),
              dto.status(),
              dto.createdAt()
          );
        })
        .toList();
  }

  /**
   * Delete a budget history entry by ID
   * Verifies that the history entry belongs to the current user's account
   */
  @Transactional
  public void deleteHistoryById(UserEntity currentUser, Integer historyId) {
    Integer accountId = currentUser.getAccount().getAccountId();

    BudgetHistoryEntity history = budgetHistoryRepository.findById(historyId)
        .orElseThrow(() -> new IllegalArgumentException(
            "Budget history not found with id: " + historyId));

    if (!history.getAccount().getAccountId().equals(accountId)) {
      throw new IllegalArgumentException(
          "Budget history does not belong to current user's account");
    }

    budgetHistoryRepository.delete(history);
    LOG.info("Deleted budget history entry");
  }
}
