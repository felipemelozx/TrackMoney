package fun.trackmoney.service;
import fun.trackmoney.service.MetricsService;


import fun.trackmoney.entity.BudgetHistoryEntity;
import fun.trackmoney.enums.BudgetStatus;
import fun.trackmoney.repository.BudgetHistoryRepository;
import fun.trackmoney.entity.CategoryEntity;
import fun.trackmoney.dto.metrics.response.BudgetPerformanceDTO;
import fun.trackmoney.dto.metrics.response.CategoryBreakdownDTO;
import fun.trackmoney.dto.metrics.response.DashboardOverviewDTO;
import fun.trackmoney.dto.metrics.response.MonthlySummaryDTO;
import fun.trackmoney.repository.projection.CategoryAggregateProjection;
import fun.trackmoney.repository.projection.MonthAggregateProjection;
import fun.trackmoney.testutils.AccountEntityFactory;
import fun.trackmoney.testutils.CategoryEntityFactory;
import fun.trackmoney.repository.TransactionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MetricsServiceTest {

  @Mock
  private TransactionRepository transactionRepository;

  @Mock
  private BudgetHistoryRepository budgetHistoryRepository;

  @Mock
  private BudgetHistoryService budgetHistoryService;

  @InjectMocks
  private MetricsService metricsService;

  @Test
  void getMonthlySummary_shouldReturnAggregatedData() {
    int accountId = 1;
    int year = 2024;

    MonthAggregateProjection jan = new MonthAggregateProjection() {
      @Override public int getMonth() { return 1; }
      @Override public Integer getYear() { return 2024; }
      @Override public BigDecimal getIncome() { return new BigDecimal("5000"); }
      @Override public BigDecimal getExpense() { return new BigDecimal("3000"); }
    };

    MonthAggregateProjection feb = new MonthAggregateProjection() {
      @Override public int getMonth() { return 2; }
      @Override public Integer getYear() { return 2024; }
      @Override public BigDecimal getIncome() { return new BigDecimal("5500"); }
      @Override public BigDecimal getExpense() { return new BigDecimal("3500"); }
    };

    when(transactionRepository.sumByMonthAndType(accountId, year, null))
        .thenReturn(List.of(jan, feb));

    MonthlySummaryDTO result = metricsService.getMonthlySummary(accountId, year, null);

    assertNotNull(result);
    assertEquals(2, result.months().size());

    MonthlySummaryDTO.MonthSummary janSummary = result.months().get(0);
    assertEquals(2024, janSummary.year());
    assertEquals(1, janSummary.month());
    assertEquals(new BigDecimal("5000"), janSummary.income());
    assertEquals(new BigDecimal("3000"), janSummary.expense());
    assertEquals(new BigDecimal("2000"), janSummary.balance());

    MonthlySummaryDTO.MonthSummary febSummary = result.months().get(1);
    assertEquals(2024, febSummary.year());
    assertEquals(2, febSummary.month());
    assertEquals(new BigDecimal("5500"), febSummary.income());
    assertEquals(new BigDecimal("3500"), febSummary.expense());
    assertEquals(new BigDecimal("2000"), febSummary.balance());
  }

  @Test
  void getMonthlySummary_shouldReturnEmptyListWhenNoData() {
    int accountId = 1;
    int year = 2024;

    when(transactionRepository.sumByMonthAndType(accountId, year, null))
        .thenReturn(List.of());

    MonthlySummaryDTO result = metricsService.getMonthlySummary(accountId, year, null);

    assertNotNull(result);
    assertTrue(result.months().isEmpty());
  }

  @Test
  void getByCategory_shouldReturnCategoryBreakdown() {
    int accountId = 1;
    int year = 2024;
    int month = 1;

    CategoryEntity category = CategoryEntityFactory.defaultCategory();

    CategoryAggregateProjection food = new CategoryAggregateProjection() {
      @Override public Integer getCategoryId() { return 1; }
      @Override public String getCategoryName() { return "Food"; }
      @Override public String getColor() { return "#FF5733"; }
      @Override public BigDecimal getAmount() { return new BigDecimal("500"); }
    };

    CategoryAggregateProjection transport = new CategoryAggregateProjection() {
      @Override public Integer getCategoryId() { return 2; }
      @Override public String getCategoryName() { return "Transport"; }
      @Override public String getColor() { return "#33FF57"; }
      @Override public BigDecimal getAmount() { return new BigDecimal("300"); }
    };

    when(transactionRepository.sumByCategoryForMonth(accountId, year, month, null))
        .thenReturn(List.of(food, transport));

    CategoryBreakdownDTO result = metricsService.getByCategory(accountId, year, month, null);

    assertNotNull(result);
    assertEquals(year, result.year());
    assertEquals(month, result.month());
    assertEquals(new BigDecimal("800"), result.totalExpense());
    assertEquals(2, result.categories().size());

    CategoryBreakdownDTO.CategoryBreakdown foodBreakdown = result.categories().get(0);
    assertEquals("Food", foodBreakdown.categoryName());
    assertEquals("#FF5733", foodBreakdown.color());
    assertEquals(new BigDecimal("500"), foodBreakdown.amount());
    assertEquals(new BigDecimal("62.5000"), foodBreakdown.percentage());

    CategoryBreakdownDTO.CategoryBreakdown transportBreakdown = result.categories().get(1);
    assertEquals("Transport", transportBreakdown.categoryName());
    assertEquals("#33FF57", transportBreakdown.color());
    assertEquals(new BigDecimal("300"), transportBreakdown.amount());
    assertEquals(new BigDecimal("37.5000"), transportBreakdown.percentage());
  }

  @Test
  void getByCategory_shouldHandleZeroTotal() {
    int accountId = 1;
    int year = 2024;
    int month = 1;

    when(transactionRepository.sumByCategoryForMonth(accountId, year, month, null))
        .thenReturn(List.of());

    CategoryBreakdownDTO result = metricsService.getByCategory(accountId, year, month, null);

    assertNotNull(result);
    assertEquals(BigDecimal.ZERO, result.totalExpense());
    assertTrue(result.categories().isEmpty());
  }

  @Test
  void getBudgetPerformance_shouldReturnPerformanceData() {
    int accountId = 1;
    int year = 2024;

    CategoryEntity food = CategoryEntityFactory.defaultCategory();

    BudgetHistoryEntity history1 = new BudgetHistoryEntity()
        .setHistoryId(1)
        .setAccount(AccountEntityFactory.defaultAccount())
        .setCategory(food)
        .setReferenceMonth((short) 1)
        .setReferenceYear(year)
        .setTargetAmount(new BigDecimal("1000"))
        .setSpentAmount(new BigDecimal("800"))
        .setRemainingAmount(new BigDecimal("200"))
        .setStatus(BudgetStatus.WITHIN_LIMIT);

    BudgetHistoryEntity history2 = new BudgetHistoryEntity()
        .setHistoryId(2)
        .setAccount(AccountEntityFactory.defaultAccount())
        .setCategory(food)
        .setReferenceMonth((short) 2)
        .setReferenceYear(year)
        .setTargetAmount(new BigDecimal("1000"))
        .setSpentAmount(new BigDecimal("1050"))
        .setRemainingAmount(new BigDecimal("-50"))
        .setStatus(BudgetStatus.EXCEEDED);

    when(budgetHistoryRepository.findByAccountAndYear(accountId, year, null))
        .thenReturn(List.of(history1, history2));

    BudgetPerformanceDTO result = metricsService.getBudgetPerformance(accountId, year, null);

    assertNotNull(result);
    assertEquals(year, result.year());
    assertEquals(2, result.monthlyPerformance().size());

    BudgetPerformanceDTO.BudgetMonthPerformance month1 = result.monthlyPerformance().get(0);
    assertEquals(1, month1.month());
    assertEquals(1, month1.categories().size());
    assertEquals("OK", month1.categories().get(0).status());

    BudgetPerformanceDTO.BudgetMonthPerformance month2 = result.monthlyPerformance().get(1);
    assertEquals(2, month2.month());
    assertEquals(1, month2.categories().size());
    assertEquals("EXCEEDED", month2.categories().get(0).status());
  }

  @Test
  void getBudgetPerformance_shouldReturnEmptyListWhenNoData() {
    int accountId = 1;
    int year = 2024;

    when(budgetHistoryRepository.findByAccountAndYear(accountId, year, null))
        .thenReturn(List.of());

    BudgetPerformanceDTO result = metricsService.getBudgetPerformance(accountId, year, null);

    assertNotNull(result);
    assertEquals(year, result.year());
    assertTrue(result.monthlyPerformance().isEmpty());
  }

  @Test
  void getOverview_shouldReturnCurrentMonthData() {
    int accountId = 1;
    LocalDate now = LocalDate.now();
    int currentMonth = now.getMonthValue();
    int currentYear = now.getYear();

    MonthAggregateProjection monthData = new MonthAggregateProjection() {
      @Override public int getMonth() { return currentMonth; }
      @Override public Integer getYear() { return currentYear; }
      @Override public BigDecimal getIncome() { return new BigDecimal("5000"); }
      @Override public BigDecimal getExpense() { return new BigDecimal("3000"); }
    };

    CategoryEntity food = CategoryEntityFactory.defaultCategory();
    food.setName("Food");
    food.setColor("#FF5733");

    CategoryAggregateProjection topCategory = new CategoryAggregateProjection() {
      @Override public Integer getCategoryId() { return 1; }
      @Override public String getCategoryName() { return "Food"; }
      @Override public String getColor() { return "#FF5733"; }
      @Override public BigDecimal getAmount() { return new BigDecimal("500"); }
    };

    BudgetHistoryEntity exceededBudget = new BudgetHistoryEntity()
        .setHistoryId(1)
        .setAccount(AccountEntityFactory.defaultAccount())
        .setCategory(food)
        .setReferenceMonth((short) currentMonth)
        .setReferenceYear(currentYear)
        .setStatus(BudgetStatus.EXCEEDED);

    BudgetHistoryEntity onTrackBudget = new BudgetHistoryEntity()
        .setHistoryId(2)
        .setAccount(AccountEntityFactory.defaultAccount())
        .setCategory(food)
        .setReferenceMonth((short) currentMonth)
        .setReferenceYear(currentYear)
        .setStatus(BudgetStatus.WITHIN_LIMIT);

    when(transactionRepository.sumByMonthAndType(accountId, currentYear, null))
        .thenReturn(List.of(monthData));

    when(budgetHistoryRepository.findByAccountAndYearAndMonth(accountId, currentYear, (short) currentMonth, null))
        .thenReturn(List.of(exceededBudget, onTrackBudget));

    when(transactionRepository.sumByCategoryForMonth(accountId, currentYear, currentMonth, null))
        .thenReturn(List.of(topCategory));

    DashboardOverviewDTO result = metricsService.getOverview(accountId, null);

    assertNotNull(result);
    assertEquals(new BigDecimal("5000"), result.totalIncome());
    assertEquals(new BigDecimal("3000"), result.totalExpense());
    assertEquals(new BigDecimal("2000"), result.totalBalance());
    assertEquals(1, result.exceededBudgetsCount());
    assertEquals(2, result.totalBudgetsCount());
    assertEquals("Food", result.topExpenseCategory());
    assertEquals(new BigDecimal("500"), result.topExpenseAmount());
    assertEquals(currentMonth, result.periodStartMonth());
    assertEquals(currentYear, result.periodStartYear());
  }

  @Test
  void getOverview_shouldHandleNoTransactionData() {
    int accountId = 1;
    LocalDate now = LocalDate.now();
    int currentMonth = now.getMonthValue();
    int currentYear = now.getYear();

    when(transactionRepository.sumByMonthAndType(accountId, currentYear, null))
        .thenReturn(List.of());

    when(budgetHistoryRepository.findByAccountAndYearAndMonth(accountId, currentYear, (short) currentMonth, null))
        .thenReturn(List.of());

    when(transactionRepository.sumByCategoryForMonth(accountId, currentYear, currentMonth, null))
        .thenReturn(List.of());

    DashboardOverviewDTO result = metricsService.getOverview(accountId, null);

    assertNotNull(result);
    assertEquals(BigDecimal.ZERO, result.totalIncome());
    assertEquals(BigDecimal.ZERO, result.totalExpense());
    assertEquals(BigDecimal.ZERO, result.totalBalance());
    assertEquals(0, result.exceededBudgetsCount());
    assertEquals(0, result.totalBudgetsCount());
    assertEquals("N/A", result.topExpenseCategory());
    assertEquals(BigDecimal.ZERO, result.topExpenseAmount());
  }

  @Test
  void determineStatus_shouldReturnExceededWhenSpentMoreThanTarget() {
    BudgetHistoryEntity history = new BudgetHistoryEntity()
        .setTargetAmount(new BigDecimal("100"))
        .setSpentAmount(new BigDecimal("110"));

    // Test through getBudgetPerformance which uses the private method
    CategoryEntity category = CategoryEntityFactory.defaultCategory();

    BudgetHistoryEntity exceededHistory = new BudgetHistoryEntity()
        .setHistoryId(1)
        .setAccount(AccountEntityFactory.defaultAccount())
        .setCategory(category)
        .setReferenceMonth((short) 1)
        .setReferenceYear(2024)
        .setTargetAmount(new BigDecimal("100"))
        .setSpentAmount(new BigDecimal("110"))
        .setRemainingAmount(new BigDecimal("-10"))
        .setStatus(BudgetStatus.EXCEEDED);

    when(budgetHistoryRepository.findByAccountAndYear(1, 2024, null))
        .thenReturn(List.of(exceededHistory));

    BudgetPerformanceDTO result = metricsService.getBudgetPerformance(1, 2024, null);

    assertEquals("EXCEEDED", result.monthlyPerformance().get(0).categories().get(0).status());
  }

  @Test
  void determineStatus_shouldReturnWarningWhenSpentOver90Percent() {
    CategoryEntity category = CategoryEntityFactory.defaultCategory();

    BudgetHistoryEntity warningHistory = new BudgetHistoryEntity()
        .setHistoryId(1)
        .setAccount(AccountEntityFactory.defaultAccount())
        .setCategory(category)
        .setReferenceMonth((short) 1)
        .setReferenceYear(2024)
        .setTargetAmount(new BigDecimal("100"))
        .setSpentAmount(new BigDecimal("95"))
        .setRemainingAmount(new BigDecimal("5"))
        .setStatus(BudgetStatus.WITHIN_LIMIT);

    when(budgetHistoryRepository.findByAccountAndYear(1, 2024, null))
        .thenReturn(List.of(warningHistory));

    BudgetPerformanceDTO result = metricsService.getBudgetPerformance(1, 2024, null);

    assertEquals("WARNING", result.monthlyPerformance().get(0).categories().get(0).status());
  }

  @Test
  void determineStatus_shouldReturnOkWhenSpentLessThan90Percent() {
    CategoryEntity category = CategoryEntityFactory.defaultCategory();

    BudgetHistoryEntity okHistory = new BudgetHistoryEntity()
        .setHistoryId(1)
        .setAccount(AccountEntityFactory.defaultAccount())
        .setCategory(category)
        .setReferenceMonth((short) 1)
        .setReferenceYear(2024)
        .setTargetAmount(new BigDecimal("100"))
        .setSpentAmount(new BigDecimal("50"))
        .setRemainingAmount(new BigDecimal("50"))
        .setStatus(BudgetStatus.WITHIN_LIMIT);

    when(budgetHistoryRepository.findByAccountAndYear(1, 2024, null))
        .thenReturn(List.of(okHistory));

    BudgetPerformanceDTO result = metricsService.getBudgetPerformance(1, 2024, null);

    assertEquals("OK", result.monthlyPerformance().get(0).categories().get(0).status());
  }

  // ===== DATE RANGE TESTS =====

  @Test
  void getMonthlySummary_withDateRange_shouldReturnAggregatedData() {
    int accountId = 1;
    LocalDate startDate = LocalDate.of(2024, 1, 1);
    LocalDate endDate = LocalDate.of(2024, 3, 31);

    MonthAggregateProjection jan = new MonthAggregateProjection() {
      @Override public int getMonth() { return 1; }
      @Override public Integer getYear() { return 2024; }
      @Override public BigDecimal getIncome() { return new BigDecimal("5000"); }
      @Override public BigDecimal getExpense() { return new BigDecimal("3000"); }
    };

    MonthAggregateProjection feb = new MonthAggregateProjection() {
      @Override public int getMonth() { return 2; }
      @Override public Integer getYear() { return 2024; }
      @Override public BigDecimal getIncome() { return new BigDecimal("5500"); }
      @Override public BigDecimal getExpense() { return new BigDecimal("3500"); }
    };

    MonthAggregateProjection mar = new MonthAggregateProjection() {
      @Override public int getMonth() { return 3; }
      @Override public Integer getYear() { return 2024; }
      @Override public BigDecimal getIncome() { return new BigDecimal("6000"); }
      @Override public BigDecimal getExpense() { return new BigDecimal("4000"); }
    };

    when(transactionRepository.sumByMonthAndTypeForDateRange(eq(accountId), any(), any(), eq(null)))
        .thenReturn(List.of(jan, feb, mar));

    MonthlySummaryDTO result = metricsService.getMonthlySummary(accountId, startDate, endDate, null);

    assertNotNull(result);
    assertEquals(3, result.months().size());
    assertEquals(2024, result.months().get(0).year());
    assertEquals(2024, result.months().get(1).year());
    assertEquals(2024, result.months().get(2).year());
    assertEquals(new BigDecimal("5000"), result.months().get(0).income());
    assertEquals(new BigDecimal("5500"), result.months().get(1).income());
    assertEquals(new BigDecimal("6000"), result.months().get(2).income());
  }

  @Test
  void getMonthlySummary_withDateRange_shouldReturnEmptyListWhenNoData() {
    int accountId = 1;
    LocalDate startDate = LocalDate.of(2024, 1, 1);
    LocalDate endDate = LocalDate.of(2024, 3, 31);

    when(transactionRepository.sumByMonthAndTypeForDateRange(eq(accountId), any(), any(), eq(null)))
        .thenReturn(List.of());

    MonthlySummaryDTO result = metricsService.getMonthlySummary(accountId, startDate, endDate, null);

    assertNotNull(result);
    assertTrue(result.months().isEmpty());
  }

  @Test
  void getByCategory_withDateRange_shouldReturnCategoryBreakdown() {
    int accountId = 1;
    LocalDate startDate = LocalDate.of(2024, 1, 1);
    LocalDate endDate = LocalDate.of(2024, 3, 31);

    CategoryAggregateProjection food = new CategoryAggregateProjection() {
      @Override public Integer getCategoryId() { return 1; }
      @Override public String getCategoryName() { return "Food"; }
      @Override public String getColor() { return "#FF5733"; }
      @Override public BigDecimal getAmount() { return new BigDecimal("1500"); }
    };

    CategoryAggregateProjection transport = new CategoryAggregateProjection() {
      @Override public Integer getCategoryId() { return 2; }
      @Override public String getCategoryName() { return "Transport"; }
      @Override public String getColor() { return "#33FF57"; }
      @Override public BigDecimal getAmount() { return new BigDecimal("900"); }
    };

    when(transactionRepository.sumByCategoryForDateRange(eq(accountId), any(), any(), eq(null)))
        .thenReturn(List.of(food, transport));

    CategoryBreakdownDTO result = metricsService.getByCategory(accountId, startDate, endDate, null);

    assertNotNull(result);
    assertEquals(2024, result.year());
    assertEquals(1, result.month()); // Start month
    assertEquals(new BigDecimal("2400"), result.totalExpense());
    assertEquals(2, result.categories().size());

    CategoryBreakdownDTO.CategoryBreakdown foodBreakdown = result.categories().get(0);
    assertEquals("Food", foodBreakdown.categoryName());
    assertEquals(new BigDecimal("1500"), foodBreakdown.amount());
    assertEquals(new BigDecimal("62.5000"), foodBreakdown.percentage());
  }

  @Test
  void getByCategory_withDateRange_shouldHandleZeroTotal() {
    int accountId = 1;
    LocalDate startDate = LocalDate.of(2024, 1, 1);
    LocalDate endDate = LocalDate.of(2024, 3, 31);

    when(transactionRepository.sumByCategoryForDateRange(eq(accountId), any(), any(), eq(null)))
        .thenReturn(List.of());

    CategoryBreakdownDTO result = metricsService.getByCategory(accountId, startDate, endDate, null);

    assertNotNull(result);
    assertEquals(BigDecimal.ZERO, result.totalExpense());
    assertTrue(result.categories().isEmpty());
  }

  @Test
  void getBudgetPerformance_withDateRange_shouldReturnPerformanceData() {
    int accountId = 1;
    LocalDate startDate = LocalDate.of(2024, 1, 1);
    LocalDate endDate = LocalDate.of(2024, 3, 31);

    CategoryEntity food = CategoryEntityFactory.defaultCategory();

    BudgetHistoryEntity historyJan = new BudgetHistoryEntity()
        .setHistoryId(1)
        .setAccount(AccountEntityFactory.defaultAccount())
        .setCategory(food)
        .setReferenceMonth((short) 1)
        .setReferenceYear(2024)
        .setTargetAmount(new BigDecimal("1000"))
        .setSpentAmount(new BigDecimal("800"))
        .setRemainingAmount(new BigDecimal("200"))
        .setStatus(BudgetStatus.WITHIN_LIMIT);

    BudgetHistoryEntity historyFeb = new BudgetHistoryEntity()
        .setHistoryId(2)
        .setAccount(AccountEntityFactory.defaultAccount())
        .setCategory(food)
        .setReferenceMonth((short) 2)
        .setReferenceYear(2024)
        .setTargetAmount(new BigDecimal("1000"))
        .setSpentAmount(new BigDecimal("1050"))
        .setRemainingAmount(new BigDecimal("-50"))
        .setStatus(BudgetStatus.EXCEEDED);

    when(budgetHistoryRepository.findByAccountAndYearMonthRange(
        eq(accountId), anyInt(), any(Short.class), anyInt(), any(Short.class), eq(null)
    )).thenReturn(List.of(historyJan, historyFeb));

    BudgetPerformanceDTO result = metricsService.getBudgetPerformance(accountId, startDate, endDate, null);

    assertNotNull(result);
    assertEquals(2024, result.year());
    assertEquals(2, result.monthlyPerformance().size());
    assertEquals(1, result.monthlyPerformance().get(0).month());
    assertEquals(2, result.monthlyPerformance().get(1).month());
  }

  @Test
  void getBudgetPerformance_withDateRange_shouldReturnEmptyListWhenNoData() {
    int accountId = 1;
    LocalDate startDate = LocalDate.of(2024, 1, 1);
    LocalDate endDate = LocalDate.of(2024, 3, 31);

    when(budgetHistoryRepository.findByAccountAndYearMonthRange(
        eq(accountId), anyInt(), any(Short.class), anyInt(), any(Short.class), eq(null)
    )).thenReturn(List.of());

    BudgetPerformanceDTO result = metricsService.getBudgetPerformance(accountId, startDate, endDate, null);

    assertNotNull(result);
    assertEquals(2024, result.year());
    assertTrue(result.monthlyPerformance().isEmpty());
  }

  @Test
  void getOverview_withDateRange_shouldReturnAggregatedData() {
    int accountId = 1;
    LocalDate startDate = LocalDate.of(2024, 1, 1);
    LocalDate endDate = LocalDate.of(2024, 3, 31);

    MonthAggregateProjection monthData = new MonthAggregateProjection() {
      @Override public int getMonth() { return 1; }
      @Override public Integer getYear() { return 2024; }
      @Override public BigDecimal getIncome() { return new BigDecimal("15000"); }
      @Override public BigDecimal getExpense() { return new BigDecimal("9000"); }
    };

    CategoryEntity food = CategoryEntityFactory.defaultCategory();
    food.setName("Food");
    food.setColor("#FF5733");

    CategoryAggregateProjection topCategory = new CategoryAggregateProjection() {
      @Override public Integer getCategoryId() { return 1; }
      @Override public String getCategoryName() { return "Food"; }
      @Override public String getColor() { return "#FF5733"; }
      @Override public BigDecimal getAmount() { return new BigDecimal("3000"); }
    };

    BudgetHistoryEntity exceededBudget = new BudgetHistoryEntity()
        .setHistoryId(1)
        .setAccount(AccountEntityFactory.defaultAccount())
        .setCategory(food)
        .setReferenceMonth((short) 1)
        .setReferenceYear(2024)
        .setStatus(BudgetStatus.EXCEEDED);

    BudgetHistoryEntity onTrackBudget = new BudgetHistoryEntity()
        .setHistoryId(2)
        .setAccount(AccountEntityFactory.defaultAccount())
        .setCategory(food)
        .setReferenceMonth((short) 2)
        .setReferenceYear(2024)
        .setStatus(BudgetStatus.WITHIN_LIMIT);

    when(transactionRepository.sumByMonthAndTypeForDateRange(eq(accountId), any(), any(), eq(null)))
        .thenReturn(List.of(monthData));

    when(budgetHistoryRepository.findByAccountAndYearMonthRange(
        eq(accountId), anyInt(), any(Short.class), anyInt(), any(Short.class), eq(null)
    )).thenReturn(List.of(exceededBudget, onTrackBudget));

    when(transactionRepository.sumByCategoryForDateRange(eq(accountId), any(), any(), eq(null)))
        .thenReturn(List.of(topCategory));

    DashboardOverviewDTO result = metricsService.getOverview(accountId, startDate, endDate, null);

    assertNotNull(result);
    assertEquals(new BigDecimal("15000"), result.totalIncome());
    assertEquals(new BigDecimal("9000"), result.totalExpense());
    assertEquals(new BigDecimal("6000"), result.totalBalance());
    assertEquals(1, result.exceededBudgetsCount()); // Only January is within range
    assertEquals(2, result.totalBudgetsCount()); // Both months within range
    assertEquals("Food", result.topExpenseCategory());
    assertEquals(new BigDecimal("3000"), result.topExpenseAmount());
    assertEquals(1, result.periodStartMonth());
    assertEquals(2024, result.periodStartYear());
  }

  @Test
  void getOverview_withDateRange_shouldHandleNoData() {
    int accountId = 1;
    LocalDate startDate = LocalDate.of(2024, 1, 1);
    LocalDate endDate = LocalDate.of(2024, 3, 31);

    when(transactionRepository.sumByMonthAndTypeForDateRange(eq(accountId), any(), any(), eq(null)))
        .thenReturn(List.of());

    when(budgetHistoryRepository.findByAccountAndYearMonthRange(
        eq(accountId), anyInt(), any(Short.class), anyInt(), any(Short.class), eq(null)
    )).thenReturn(List.of());

    when(transactionRepository.sumByCategoryForDateRange(eq(accountId), any(), any(), eq(null)))
        .thenReturn(List.of());

    DashboardOverviewDTO result = metricsService.getOverview(accountId, startDate, endDate, null);

    assertNotNull(result);
    assertEquals(BigDecimal.ZERO, result.totalIncome());
    assertEquals(BigDecimal.ZERO, result.totalExpense());
    assertEquals(BigDecimal.ZERO, result.totalBalance());
    assertEquals(0, result.exceededBudgetsCount());
    assertEquals(0, result.totalBudgetsCount());
    assertEquals("N/A", result.topExpenseCategory());
    assertEquals(BigDecimal.ZERO, result.topExpenseAmount());
  }

  @Test
  void getOverview_withDateRange_shouldFilterBudgetsOutsideRange() {
    int accountId = 1;
    LocalDate startDate = LocalDate.of(2024, 2, 1);
    LocalDate endDate = LocalDate.of(2024, 3, 31);

    CategoryEntity food = CategoryEntityFactory.defaultCategory();

    // January budget - should be filtered out
    BudgetHistoryEntity janBudget = new BudgetHistoryEntity()
        .setHistoryId(1)
        .setAccount(AccountEntityFactory.defaultAccount())
        .setCategory(food)
        .setReferenceMonth((short) 1)
        .setReferenceYear(2024)
        .setStatus(BudgetStatus.EXCEEDED);

    // February budget - should be included
    BudgetHistoryEntity febBudget = new BudgetHistoryEntity()
        .setHistoryId(2)
        .setAccount(AccountEntityFactory.defaultAccount())
        .setCategory(food)
        .setReferenceMonth((short) 2)
        .setReferenceYear(2024)
        .setStatus(BudgetStatus.EXCEEDED);

    when(transactionRepository.sumByMonthAndTypeForDateRange(eq(accountId), any(), any(), eq(null)))
        .thenReturn(List.of());

    when(budgetHistoryRepository.findByAccountAndYearMonthRange(
        eq(accountId), anyInt(), any(Short.class), anyInt(), any(Short.class), eq(null)
    )).thenReturn(List.of(janBudget, febBudget));

    when(transactionRepository.sumByCategoryForDateRange(eq(accountId), any(), any(), eq(null)))
        .thenReturn(List.of());

    DashboardOverviewDTO result = metricsService.getOverview(accountId, startDate, endDate, null);

    assertNotNull(result);
    // Only February budget should be counted (January is before start date)
    assertEquals(1, result.totalBudgetsCount());
    assertEquals(1, result.exceededBudgetsCount());
  }

  @Test
  void getBudgetPerformance_withDateRange_shouldCallProvisionalGeneration_whenCurrentMonthRange() {
    LocalDate now = LocalDate.now();
    LocalDate startDate = LocalDate.of(now.getYear(), now.getMonthValue(), 1);
    LocalDate endDate = now;

    when(budgetHistoryRepository.findByAccountAndYearMonthRange(
        eq(1), anyInt(), any(Short.class), anyInt(), any(Short.class), eq(null)
    )).thenReturn(List.of());

    metricsService.getBudgetPerformance(1, startDate, endDate, null);

    verify(budgetHistoryService).generateProvisionalHistoryForCurrentMonth(eq(1), eq(null));
  }

  @Test
  void getBudgetPerformance_withDateRange_shouldNotCallProvisionalGeneration_whenNotCurrentMonthRange() {
    LocalDate startDate = LocalDate.of(2024, 1, 1);
    LocalDate endDate = LocalDate.of(2024, 3, 31);

    when(budgetHistoryRepository.findByAccountAndYearMonthRange(
        eq(1), anyInt(), any(Short.class), anyInt(), any(Short.class), eq(null)
    )).thenReturn(List.of());

    metricsService.getBudgetPerformance(1, startDate, endDate, null);

    verify(budgetHistoryService, never()).generateProvisionalHistoryForCurrentMonth(anyInt(), any());
  }

  @Test
  void getBudgetPerformance_withDateRange_shouldCallProvisionalGeneration_withCategoryId() {
    LocalDate now = LocalDate.now();
    LocalDate startDate = LocalDate.of(now.getYear(), now.getMonthValue(), 1);
    LocalDate endDate = now;
    Integer categoryId = 5;

    when(budgetHistoryRepository.findByAccountAndYearMonthRange(
        eq(1), anyInt(), any(Short.class), anyInt(), any(Short.class), eq(categoryId)
    )).thenReturn(List.of());

    metricsService.getBudgetPerformance(1, startDate, endDate, categoryId);

    verify(budgetHistoryService).generateProvisionalHistoryForCurrentMonth(eq(1), eq(categoryId));
  }
}
