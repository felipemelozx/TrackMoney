package fun.trackmoney.budget.service;

import fun.trackmoney.budget.entity.BudgetHistoryEntity;
import fun.trackmoney.budget.entity.BudgetsEntity;
import fun.trackmoney.budget.enums.BudgetStatus;
import fun.trackmoney.budget.repository.BudgetHistoryRepository;
import fun.trackmoney.budget.repository.BudgetsRepository;
import fun.trackmoney.category.entity.CategoryEntity;
import fun.trackmoney.enums.TransactionType;
import fun.trackmoney.transaction.entity.TransactionEntity;
import fun.trackmoney.transaction.repository.TransactionRepository;
import fun.trackmoney.user.entity.UserEntity;
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

  public BudgetHistoryService(
      BudgetHistoryRepository budgetHistoryRepository,
      BudgetsRepository budgetsRepository,
      TransactionRepository transactionRepository) {
    this.budgetHistoryRepository = budgetHistoryRepository;
    this.budgetsRepository = budgetsRepository;
    this.transactionRepository = transactionRepository;
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
   */
  @Transactional
  public int generateHistoryForMonth(UserEntity currentUser, int month, int year) {
    Integer accountId = currentUser.getAccount().getAccountId();

    if (budgetHistoryRepository.existsHistoryForAccountAndMonth(accountId, (short) month, year)) {
      LOG.info("History already exists for account {} and month {}/{}", accountId, month, year);
      return 0;
    }

    LOG.info("Generating history for account {} and month {}/{}", accountId, month, year);
    return generateHistoryForAccount(accountId, (short) month, year);
  }

  /**
   * Get all history for current user
   */
  public List<BudgetHistoryEntity> getAllHistory(UserEntity currentUser) {
    Integer accountId = currentUser.getAccount().getAccountId();
    return budgetHistoryRepository.findByAccountAccountIdOrderByReferenceYearDescReferenceMonthDesc(accountId);
  }

  /**
   * Get history by date range
   */
  public List<BudgetHistoryEntity> getHistoryByDateRange(
      UserEntity currentUser,
      Short startMonth,
      Integer startYear,
      Short endMonth,
      Integer endYear) {
    Integer accountId = currentUser.getAccount().getAccountId();
    return budgetHistoryRepository.findByAccountAndDateRange(
        accountId, startYear, startMonth, endYear, endMonth
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
    BigDecimal percentageUsed = calculatePercentageUsed(spentAmount, targetAmount);
    BudgetStatus status = determineBudgetStatus(spentAmount, targetAmount);

    return buildBudgetHistoryEntity(budget, category, month, year, percent,
        targetAmount, spentAmount, remainingAmount, totalIncome, percentageUsed, status);
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

  private BigDecimal calculatePercentageUsed(BigDecimal spentAmount, BigDecimal targetAmount) {
    if (targetAmount.compareTo(BigDecimal.ZERO) > 0) {
      return spentAmount
          .multiply(HUNDRED)
          .divide(targetAmount, 2, RoundingMode.HALF_UP);
    }
    return BigDecimal.ZERO;
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
      BigDecimal percentageUsed,
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
        .setPercentageUsed(percentageUsed)
        .setStatus(status);
  }

  /**
   * Gets total income for an account within a date range
   */
  private BigDecimal getTotalIncomeForMonth(
      Integer accountId,
      LocalDateTime startDate,
      LocalDateTime endDate) {

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
}
