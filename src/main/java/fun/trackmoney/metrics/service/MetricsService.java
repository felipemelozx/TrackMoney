package fun.trackmoney.metrics.service;

import fun.trackmoney.entity.BudgetHistoryEntity;
import fun.trackmoney.entity.BudgetsEntity;
import fun.trackmoney.repository.BudgetHistoryRepository;
import fun.trackmoney.repository.BudgetsRepository;
import fun.trackmoney.budget.enums.BudgetStatus;
import fun.trackmoney.entity.CategoryEntity;
import fun.trackmoney.enums.TransactionType;
import fun.trackmoney.metrics.dto.response.BudgetPerformanceDTO;
import fun.trackmoney.metrics.dto.response.CategoryBreakdownDTO;
import fun.trackmoney.metrics.dto.response.DashboardOverviewDTO;
import fun.trackmoney.metrics.dto.response.MonthlySummaryDTO;
import fun.trackmoney.metrics.projection.CategoryAggregateProjection;
import fun.trackmoney.metrics.projection.MonthAggregateProjection;
import fun.trackmoney.repository.TransactionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Service for metrics and analytics operations.
 * Provides aggregated financial data for dashboards and reports.
 */
@Service
public class MetricsService {

  private final TransactionRepository transactionRepository;
  private final BudgetHistoryRepository budgetHistoryRepository;
  private final BudgetsRepository budgetsRepository;

  public MetricsService(TransactionRepository transactionRepository,
                        BudgetHistoryRepository budgetHistoryRepository,
                        BudgetsRepository budgetsRepository) {
    this.transactionRepository = transactionRepository;
    this.budgetHistoryRepository = budgetHistoryRepository;
    this.budgetsRepository = budgetsRepository;
  }

  /**
   * Gets monthly income vs expense summary for a given year.
   *
   * @param accountId the user's account ID
   * @param year      the year to get data for
   * @param categoryId the category ID to filter by (null for all categories)
   * @return monthly summary with income, expense, and balance per month
   */
  public MonthlySummaryDTO getMonthlySummary(Integer accountId, int year, Integer categoryId) {
    List<MonthAggregateProjection> aggregates = transactionRepository
        .sumByMonthAndType(accountId, year, categoryId);

    List<MonthlySummaryDTO.MonthSummary> months = aggregates.stream()
        .map(agg -> new MonthlySummaryDTO.MonthSummary(
            agg.getYear(),
            agg.getMonth(),
            agg.getIncome(),
            agg.getExpense(),
            agg.getIncome().subtract(agg.getExpense())
        ))
        .toList();

    return new MonthlySummaryDTO(months);
  }

  /**
   * Gets monthly income vs expense summary for a given date range.
   *
   * @param accountId the user's account ID
   * @param startDate the start date (inclusive)
   * @param endDate   the end date (inclusive)
   * @param categoryId the category ID to filter by (null for all categories)
   * @return monthly summary with income, expense, and balance per month
   */
  public MonthlySummaryDTO getMonthlySummary(
      Integer accountId,
      LocalDate startDate,
      LocalDate endDate,
      Integer categoryId
  ) {
    LocalDateTime startDateTime = startDate.atStartOfDay();
    LocalDateTime endDateTime = endDate.atTime(23, 59, 59);

    List<MonthAggregateProjection> aggregates = transactionRepository
        .sumByMonthAndTypeForDateRange(accountId, startDateTime, endDateTime, categoryId);

    List<MonthlySummaryDTO.MonthSummary> months = aggregates.stream()
        .map(agg -> new MonthlySummaryDTO.MonthSummary(
            agg.getYear(),
            agg.getMonth(),
            agg.getIncome(),
            agg.getExpense(),
            agg.getIncome().subtract(agg.getExpense())
        ))
        .toList();

    return new MonthlySummaryDTO(months);
  }

  /**
   * Gets expense breakdown by category for a specific month.
   *
   * @param accountId the user's account ID
   * @param year      the year
   * @param month     the month (1-12)
   * @param categoryId the category ID to filter by (null for all categories)
   * @return category breakdown with amounts and percentages
   */
  public CategoryBreakdownDTO getByCategory(Integer accountId, int year, int month, Integer categoryId) {
    List<CategoryAggregateProjection> aggregates = transactionRepository
        .sumByCategoryForMonth(accountId, year, month, categoryId);

    BigDecimal total = aggregates.stream()
        .map(CategoryAggregateProjection::getAmount)
        .reduce(BigDecimal.ZERO, BigDecimal::add);

    List<CategoryBreakdownDTO.CategoryBreakdown> categories = aggregates.stream()
        .map(agg -> new CategoryBreakdownDTO.CategoryBreakdown(
            agg.getCategoryName(),
            agg.getColor(),
            agg.getAmount(),
            calculatePercentage(agg.getAmount(), total)
        ))
        .toList();

    return new CategoryBreakdownDTO(year, month, total, categories);
  }

  /**
   * Gets expense breakdown by category for a given date range.
   *
   * @param accountId the user's account ID
   * @param startDate the start date (inclusive)
   * @param endDate   the end date (inclusive)
   * @param categoryId the category ID to filter by (null for all categories)
   * @return category breakdown with amounts and percentages
   */
  public CategoryBreakdownDTO getByCategory(
      Integer accountId,
      LocalDate startDate,
      LocalDate endDate,
      Integer categoryId
  ) {
    LocalDateTime startDateTime = startDate.atStartOfDay();
    LocalDateTime endDateTime = endDate.atTime(23, 59, 59);

    List<CategoryAggregateProjection> aggregates = transactionRepository
        .sumByCategoryForDateRange(accountId, startDateTime, endDateTime, categoryId);

    BigDecimal total = aggregates.stream()
        .map(CategoryAggregateProjection::getAmount)
        .reduce(BigDecimal.ZERO, BigDecimal::add);

    List<CategoryBreakdownDTO.CategoryBreakdown> categories = aggregates.stream()
        .map(agg -> new CategoryBreakdownDTO.CategoryBreakdown(
            agg.getCategoryName(),
            agg.getColor(),
            agg.getAmount(),
            calculatePercentage(agg.getAmount(), total)
        ))
        .toList();

    return new CategoryBreakdownDTO(startDate.getYear(), startDate.getMonthValue(), total, categories);
  }

  /**
   * Gets budget performance history for a given year.
   *
   * @param accountId the user's account ID
   * @param year      the year to get data for
   * @param categoryId the category ID to filter by (null for all categories)
   * @return budget performance grouped by month and category
   */
  public BudgetPerformanceDTO getBudgetPerformance(Integer accountId, int year, Integer categoryId) {
    List<BudgetHistoryEntity> histories = budgetHistoryRepository
        .findByAccountAndYear(accountId, year, categoryId);

    // Group by month
    Map<Integer, List<BudgetHistoryEntity>> byMonth = histories.stream()
        .collect(Collectors.groupingBy(h -> h.getReferenceMonth().intValue()));

    List<BudgetPerformanceDTO.BudgetMonthPerformance> monthlyPerformance = byMonth.entrySet().stream()
        .map(entry -> buildMonthPerformance(entry.getKey(), entry.getValue()))
        .sorted(Comparator.comparing(BudgetPerformanceDTO.BudgetMonthPerformance::month))
        .toList();

    return new BudgetPerformanceDTO(year, monthlyPerformance);
  }

  /**
   * Gets budget performance history for a given date range.
   *
   * @param accountId the user's account ID
   * @param startDate the start date (inclusive)
   * @param endDate   the end date (inclusive)
   * @param categoryId the category ID to filter by (null for all categories)
   * @return budget performance grouped by month and category
   */
  public BudgetPerformanceDTO getBudgetPerformance(
      Integer accountId,
      LocalDate startDate,
      LocalDate endDate,
      Integer categoryId
  ) {
    // Generate provisional history if the date range corresponds to the current month
    if (isCurrentMonthRange(startDate, endDate)) {
      generateProvisionalHistoryForCurrentMonth(accountId, startDate, endDate, categoryId);
    }

    List<BudgetHistoryEntity> histories = budgetHistoryRepository.findByAccountAndYearMonthRange(
        accountId,
        startDate.getYear(),
        (short) startDate.getMonthValue(),
        endDate.getYear(),
        (short) endDate.getMonthValue(),
        categoryId
    );

    // Group by month and year
    Map<String, List<BudgetHistoryEntity>> byMonthAndYear = histories.stream()
        .collect(Collectors.groupingBy(h -> h.getReferenceYear() + "-" + h.getReferenceMonth()));

    List<BudgetPerformanceDTO.BudgetMonthPerformance> monthlyPerformance = byMonthAndYear.entrySet().stream()
        .map(entry -> {
          String[] parts = entry.getKey().split("-");
          return buildMonthPerformance(
              Integer.parseInt(parts[1]),
              entry.getValue()
          );
        })
        .sorted(Comparator.comparing(BudgetPerformanceDTO.BudgetMonthPerformance::month))
        .toList();

    return new BudgetPerformanceDTO(startDate.getYear(), monthlyPerformance);
  }

  /**
   * Gets dashboard overview with current month metrics.
   *
   * @param accountId the user's account ID
   * @param categoryId the category ID to filter by (null for all categories)
   * @return dashboard overview with financials and budget status
   */
  public DashboardOverviewDTO getOverview(Integer accountId, Integer categoryId) {
    LocalDate now = LocalDate.now();
    int currentMonth = now.getMonthValue();
    int currentYear = now.getYear();

    MonthAggregateProjection currentMonthData = getCurrentMonthFinancials(
        accountId, currentYear, currentMonth, categoryId);
    long exceededCount = getExceededBudgetsCount(
        accountId, currentYear, currentMonth, categoryId);
    int totalBudgetsCount = getTotalBudgetsCount(
        accountId, currentYear, currentMonth, categoryId);
    CategoryAggregateProjection topCategory = getTopExpenseCategory(
        accountId, currentYear, currentMonth, categoryId);

    return new DashboardOverviewDTO(
        currentMonthData.getIncome(),
        currentMonthData.getExpense(),
        currentMonthData.getIncome().subtract(currentMonthData.getExpense()),
        (int) exceededCount,
        totalBudgetsCount,
        topCategory != null ? topCategory.getCategoryName() : "N/A",
        topCategory != null ? topCategory.getAmount() : BigDecimal.ZERO,
        currentMonth,
        currentYear
    );
  }

  /**
   * Gets dashboard overview for a specific date range.
   *
   * @param accountId the user's account ID
   * @param startDate the start date (inclusive)
   * @param endDate   the end date (inclusive)
   * @param categoryId the category ID to filter by (null for all categories)
   * @return dashboard overview with aggregated financials and budget status for the period
   */
  public DashboardOverviewDTO getOverview(
      Integer accountId,
      LocalDate startDate,
      LocalDate endDate,
      Integer categoryId
  ) {
    LocalDateTime startDateTime = startDate.atStartOfDay();
    LocalDateTime endDateTime = endDate.atTime(23, 59, 59);

    // Get aggregated financial data for the period
    FinancialAggregate financials = getFinancialsForDateRange(
        accountId, startDateTime, endDateTime, categoryId);

    // Get budget status for the period
    long exceededCount = getExceededBudgetsCountForDateRange(
        accountId, startDateTime, endDateTime, categoryId);
    int totalBudgetsCount = getTotalBudgetsCountForDateRange(
        accountId, startDateTime, endDateTime, categoryId);

    // Get top expense category for the period
    CategoryAggregateProjection topCategory = getTopExpenseCategoryForDateRange(
        accountId, startDateTime, endDateTime, categoryId);

    return new DashboardOverviewDTO(
        financials.totalIncome(),
        financials.totalExpense(),
        financials.totalBalance(),
        (int) exceededCount,
        totalBudgetsCount,
        topCategory != null ? topCategory.getCategoryName() : "N/A",
        topCategory != null ? topCategory.getAmount() : BigDecimal.ZERO,
        startDate.getMonthValue(),
        startDate.getYear()
    );
  }

  /**
   * Gets financial data for the current month.
   *
   * @param accountId the account ID
   * @param year      the year
   * @param month     the month
   * @param categoryId the category ID to filter by (null for all categories)
   * @return month aggregate projection
   */
  private MonthAggregateProjection getCurrentMonthFinancials(
      Integer accountId,
      int year,
      int month,
      Integer categoryId
  ) {
    List<MonthAggregateProjection> monthlyData = transactionRepository
        .sumByMonthAndType(accountId, year, categoryId);

    return monthlyData.stream()
        .filter(m -> m.getMonth() == month)
        .findFirst()
        .orElse(new MonthAggregateProjection() {
          @Override
          public int getMonth() {
            return month;
          }

          @Override
          public Integer getYear() {
            return year;
          }

          @Override
          public BigDecimal getIncome() {
            return BigDecimal.ZERO;
          }

          @Override
          public BigDecimal getExpense() {
            return BigDecimal.ZERO;
          }
        });
  }

  /**
   * Gets count of exceeded budgets for a specific month.
   *
   * @param accountId the account ID
   * @param year      the year
   * @param month     the month
   * @param categoryId the category ID to filter by (null for all categories)
   * @return count of exceeded budgets
   */
  private long getExceededBudgetsCount(Integer accountId, int year, int month, Integer categoryId) {
    List<BudgetHistoryEntity> budgetHistories = budgetHistoryRepository
        .findByAccountAndYearAndMonth(accountId, year, (short) month, categoryId);

    return budgetHistories.stream()
        .filter(b -> b.getStatus() == BudgetStatus.EXCEEDED)
        .count();
  }

  /**
   * Gets total count of budgets for a specific month.
   *
   * @param accountId the account ID
   * @param year      the year
   * @param month     the month
   * @param categoryId the category ID to filter by (null for all categories)
   * @return total count of budgets
   */
  private int getTotalBudgetsCount(Integer accountId, int year, int month, Integer categoryId) {
    List<BudgetHistoryEntity> budgetHistories = budgetHistoryRepository
        .findByAccountAndYearAndMonth(accountId, year, (short) month, categoryId);
    return budgetHistories.size();
  }

  /**
   * Gets the top expense category for a specific month.
   *
   * @param accountId the account ID
   * @param year      the year
   * @param month     the month
   * @param categoryId the category ID to filter by (null for all categories)
   * @return top category projection or null if no expenses
   */
  private CategoryAggregateProjection getTopExpenseCategory(
      Integer accountId,
      int year,
      int month,
      Integer categoryId
  ) {
    List<CategoryAggregateProjection> categories = transactionRepository
        .sumByCategoryForMonth(accountId, year, month, categoryId);
    return categories.stream()
        .findFirst()
        .orElse(null);
  }

  /**
   * Calculates the percentage of an amount relative to a total.
   *
   * @param amount the amount
   * @param total  the total
   * @return percentage value (0-100)
   */
  private BigDecimal calculatePercentage(BigDecimal amount, BigDecimal total) {
    if (total.compareTo(BigDecimal.ZERO) == 0) {
      return BigDecimal.ZERO;
    }
    return amount.divide(total, 4, RoundingMode.HALF_UP)
        .multiply(new BigDecimal("100"));
  }

  /**
   * Builds budget month performance from budget history entities.
   *
   * @param month     the month number
   * @param histories list of budget history entities
   * @return budget month performance
   */
  private BudgetPerformanceDTO.BudgetMonthPerformance buildMonthPerformance(
      int month,
      List<BudgetHistoryEntity> histories
  ) {
    List<BudgetPerformanceDTO.CategoryBudgetPerformance> categories = histories.stream()
        .map(this::toCategoryPerformance)
        .toList();

    return new BudgetPerformanceDTO.BudgetMonthPerformance(month, categories);
  }

  /**
   * Converts a budget history entity to category budget performance.
   *
   * @param history the budget history entity
   * @return category budget performance
   */
  private BudgetPerformanceDTO.CategoryBudgetPerformance toCategoryPerformance(
      BudgetHistoryEntity history
  ) {
    String status;
    if (history.getStatus() == BudgetStatus.PROVISIONAL) {
      status = "PROVISIONAL";
    } else {
      status = determineStatus(history);
    }

    return new BudgetPerformanceDTO.CategoryBudgetPerformance(
        history.getCategory().getName(),
        history.getCategory().getColor(),
        history.getTargetAmount(),
        history.getSpentAmount(),
        history.getRemainingAmount(),
        status
    );
  }

  /**
   * Determines the budget status based on spent vs target amount.
   *
   * @param history the budget history entity
   * @return status string: "EXCEEDED", "WARNING", or "OK"
   */
  private String determineStatus(BudgetHistoryEntity history) {
    BigDecimal spent = history.getSpentAmount();
    BigDecimal target = history.getTargetAmount();

    if (spent.compareTo(target) > 0) {
      return "EXCEEDED";
    } else if (spent.compareTo(target.multiply(new BigDecimal("0.9"))) > 0) {
      return "WARNING";
    } else {
      return "OK";
    }
  }

  /**
   * Gets financial data aggregated for a date range.
   *
   * @param accountId     the account ID
   * @param startDateTime the start date and time
   * @param endDateTime   the end date and time
   * @param categoryId the category ID to filter by (null for all categories)
   * @return financial aggregate with total income, expense, and balance
   */
  private FinancialAggregate getFinancialsForDateRange(
      Integer accountId,
      LocalDateTime startDateTime,
      LocalDateTime endDateTime,
      Integer categoryId
  ) {
    List<MonthAggregateProjection> monthlyData = transactionRepository
        .sumByMonthAndTypeForDateRange(accountId, startDateTime, endDateTime, categoryId);

    BigDecimal totalIncome = monthlyData.stream()
        .map(MonthAggregateProjection::getIncome)
        .reduce(BigDecimal.ZERO, BigDecimal::add);

    BigDecimal totalExpense = monthlyData.stream()
        .map(MonthAggregateProjection::getExpense)
        .reduce(BigDecimal.ZERO, BigDecimal::add);

    return new FinancialAggregate(totalIncome, totalExpense, totalIncome.subtract(totalExpense));
  }

  /**
   * Gets count of exceeded budgets for a date range.
   *
   * @param accountId     the account ID
   * @param startDateTime the start date and time
   * @param endDateTime   the end date and time
   * @param categoryId the category ID to filter by (null for all categories)
   * @return count of exceeded budgets
   */
  private long getExceededBudgetsCountForDateRange(
      Integer accountId,
      LocalDateTime startDateTime,
      LocalDateTime endDateTime,
      Integer categoryId
  ) {
    List<BudgetHistoryEntity> budgetHistories = budgetHistoryRepository
        .findByAccountAndYearMonthRange(
            accountId,
            startDateTime.getYear(),
            (short) startDateTime.getMonthValue(),
            endDateTime.getYear(),
            (short) endDateTime.getMonthValue(),
            categoryId
        );

    // Filter by actual transaction date range and count exceeded
    return budgetHistories.stream()
        .filter(b -> {
          LocalDate budgetDate = LocalDate.of(b.getReferenceYear(), b.getReferenceMonth(), 1);
          LocalDate start = startDateTime.toLocalDate();
          LocalDate end = endDateTime.toLocalDate();
          return !budgetDate.isBefore(start) && !budgetDate.isAfter(end);
        })
        .filter(b -> b.getStatus() == BudgetStatus.EXCEEDED)
        .count();
  }

  /**
   * Gets total count of budgets for a date range.
   *
   * @param accountId     the account ID
   * @param startDateTime the start date and time
   * @param endDateTime   the end date and time
   * @param categoryId the category ID to filter by (null for all categories)
   * @return total count of budgets
   */
  private int getTotalBudgetsCountForDateRange(
      Integer accountId,
      LocalDateTime startDateTime,
      LocalDateTime endDateTime,
      Integer categoryId
  ) {
    List<BudgetHistoryEntity> budgetHistories = budgetHistoryRepository
        .findByAccountAndYearMonthRange(
            accountId,
            startDateTime.getYear(),
            (short) startDateTime.getMonthValue(),
            endDateTime.getYear(),
            (short) endDateTime.getMonthValue(),
            categoryId
        );

    // Filter by actual transaction date range
    return (int) budgetHistories.stream()
        .filter(b -> {
          LocalDate budgetDate = LocalDate.of(b.getReferenceYear(), b.getReferenceMonth(), 1);
          LocalDate start = startDateTime.toLocalDate();
          LocalDate end = endDateTime.toLocalDate();
          return !budgetDate.isBefore(start) && !budgetDate.isAfter(end);
        })
        .count();
  }

  /**
   * Gets the top expense category for a date range.
   *
   * @param accountId     the account ID
   * @param startDateTime the start date and time
   * @param endDateTime   the end date and time
   * @param categoryId the category ID to filter by (null for all categories)
   * @return top category projection or null if no expenses
   */
  private CategoryAggregateProjection getTopExpenseCategoryForDateRange(
      Integer accountId,
      LocalDateTime startDateTime,
      LocalDateTime endDateTime,
      Integer categoryId
  ) {
    List<CategoryAggregateProjection> categories = transactionRepository
        .sumByCategoryForDateRange(accountId, startDateTime, endDateTime, categoryId);
    return categories.stream()
        .findFirst()
        .orElse(null);
  }

  /**
   * Checks if the given date range corresponds to the current month.
   *
   * @param startDate the start date
   * @param endDate   the end date
   * @return true if both dates are within the current month
   */
  private boolean isCurrentMonthRange(LocalDate startDate, LocalDate endDate) {
    YearMonth currentMonth = YearMonth.now();
    YearMonth startMonth = YearMonth.from(startDate);
    YearMonth endMonth = YearMonth.from(endDate);

    return startMonth.equals(currentMonth) && endMonth.equals(currentMonth);
  }

  /**
   * Generates provisional budget history for the current month.
   * This method creates or overwrites budget history entries for the current month
   * with PROVISIONAL status, based on transactions from the start of the month until now.
   *
   * @param accountId  the user's account ID
   * @param startDate  the start date of the period
   * @param endDate    the end date of the period
   * @param categoryId the category ID to filter by (null for all categories)
   */
  @Transactional
  private void generateProvisionalHistoryForCurrentMonth(
      Integer accountId,
      LocalDate startDate,
      LocalDate endDate,
      Integer categoryId
  ) {
    LocalDate now = LocalDate.now();
    short currentMonth = (short) now.getMonthValue();
    int currentYear = now.getYear();

    List<BudgetsEntity> budgets = getBudgetsForAccount(accountId, categoryId);
    if (budgets.isEmpty()) {
      return;
    }

    LocalDateTime monthStart = LocalDate.of(currentYear, currentMonth, 1).atStartOfDay();
    LocalDateTime monthEnd = now.atTime(23, 59, 59);
    BigDecimal totalIncome = getTotalIncomeForMonth(accountId, monthStart, monthEnd);

    for (BudgetsEntity budget : budgets) {
      createProvisionalHistoryForBudget(budget, currentMonth, currentYear, totalIncome, monthStart, monthEnd);
    }
  }

  /**
   * Gets budgets for an account, optionally filtered by category.
   *
   * @param accountId  the account ID
   * @param categoryId the category ID filter (null for all)
   * @return list of budgets
   */
  private List<BudgetsEntity> getBudgetsForAccount(Integer accountId, Integer categoryId) {
    List<BudgetsEntity> budgets = budgetsRepository.findAllByAccountAccountId(accountId);

    if (categoryId != null) {
      budgets = budgets.stream()
          .filter(b -> b.getCategory().getCategoryId().equals(categoryId))
          .toList();
    }

    return budgets;
  }

  /**
   * Creates or overwrites a provisional budget history entry for a specific budget.
   *
   * @param budget       the budget entity
   * @param currentMonth the current month
   * @param currentYear  the current year
   * @param totalIncome  the total income for the month
   * @param monthStart   the start of the month
   * @param monthEnd     the end of the period (today)
   */
  private void createProvisionalHistoryForBudget(
      BudgetsEntity budget,
      short currentMonth,
      int currentYear,
      BigDecimal totalIncome,
      LocalDateTime monthStart,
      LocalDateTime monthEnd
  ) {
    // Delete existing history if present
    budgetHistoryRepository
        .findByBudgetAndReferenceMonthAndReferenceYear(budget, currentMonth, currentYear)
        .ifPresent(history -> budgetHistoryRepository.delete(history));

    // Calculate values
    CategoryEntity category = budget.getCategory();
    short percent = budget.getPercent();
    BigDecimal targetAmount = calculateTargetAmount(totalIncome, percent);
    BigDecimal spentAmount = getSpentAmount(budget.getAccount().getAccountId(), category, monthStart, monthEnd);
    BigDecimal remainingAmount = targetAmount.subtract(spentAmount);

    // Create and save new provisional history
    BudgetHistoryEntity history = new BudgetHistoryEntity()
        .setBudget(budget)
        .setAccount(budget.getAccount())
        .setCategory(category)
        .setReferenceMonth(currentMonth)
        .setReferenceYear(currentYear)
        .setPercent(percent)
        .setTargetAmount(targetAmount)
        .setSpentAmount(spentAmount)
        .setRemainingAmount(remainingAmount)
        .setTotalIncome(totalIncome)
        .setStatus(BudgetStatus.PROVISIONAL);

    budgetHistoryRepository.save(history);
  }

  /**
   * Calculates the target amount based on total income and budget percentage.
   *
   * @param totalIncome the total income
   * @param percent     the budget percentage
   * @return target amount
   */
  private BigDecimal calculateTargetAmount(BigDecimal totalIncome, short percent) {
    return totalIncome
        .multiply(BigDecimal.valueOf(percent))
        .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
  }

  /**
   * Gets the total spent amount for a category within a date range.
   *
   * @param accountId the account ID
   * @param category  the category
   * @param startDate the start date
   * @param endDate   the end date
   * @return spent amount
   */
  private BigDecimal getSpentAmount(
      Integer accountId,
      CategoryEntity category,
      LocalDateTime startDate,
      LocalDateTime endDate) {
    BigDecimal spentAmount = transactionRepository.sumExpensesByCategoryAndDateRange(
        accountId,
        category.getCategoryId(),
        startDate,
        endDate
    );
    return spentAmount != null ? spentAmount : BigDecimal.ZERO;
  }

  /**
   * Gets total income for an account within a date range.
   *
   * @param accountId the account ID
   * @param startDate the start date
   * @param endDate   the end date
   * @return total income
   */
  private BigDecimal getTotalIncomeForMonth(
      Integer accountId,
      LocalDateTime startDate,
      LocalDateTime endDate) {

    List<fun.trackmoney.entity.TransactionEntity> transactions =
        transactionRepository.findAllByAccountIdAndDateRange(accountId, startDate, endDate);

    return transactions.stream()
        .filter(t -> TransactionType.INCOME.equals(t.getTransactionType()))
        .map(fun.trackmoney.entity.TransactionEntity::getAmount)
        .filter(amount -> amount != null)
        .reduce(BigDecimal.ZERO, BigDecimal::add);
  }

  /**
   * Record to hold aggregated financial data.
   */
  private record FinancialAggregate(
      BigDecimal totalIncome,
      BigDecimal totalExpense,
      BigDecimal totalBalance
  ) {
  }
}
