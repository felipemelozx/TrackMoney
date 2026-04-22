package fun.trackmoney.metrics;
import fun.trackmoney.controller.MetricsController;

import fun.trackmoney.dto.metrics.response.BudgetPerformanceDTO;
import fun.trackmoney.dto.metrics.response.CategoryBreakdownDTO;
import fun.trackmoney.dto.metrics.response.DashboardOverviewDTO;
import fun.trackmoney.dto.metrics.response.MonthlySummaryDTO;
import fun.trackmoney.service.MetricsService;
import fun.trackmoney.entity.UserEntity;
import fun.trackmoney.utils.response.ApiResponse;
import fun.trackmoney.testutils.UserEntityFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MetricsControllerTest {

  @Mock
  private MetricsService metricsService;

  @InjectMocks
  private MetricsController metricsController;

  private UserEntity mockUser;

  @BeforeEach
  void setUp() {
    mockUser = UserEntityFactory.defaultUser();
  }

  @Test
  void getMonthlySummary_shouldReturnOkAndMonthlySummaryData() {
    int year = 2024;
    Integer accountId = mockUser.getAccount().getAccountId();

    MonthlySummaryDTO.MonthSummary jan = new MonthlySummaryDTO.MonthSummary(
        2024, 1, new BigDecimal("5000"), new BigDecimal("3000"), new BigDecimal("2000")
    );

    MonthlySummaryDTO.MonthSummary feb = new MonthlySummaryDTO.MonthSummary(
        2024, 2, new BigDecimal("5500"), new BigDecimal("3500"), new BigDecimal("2000")
    );

    MonthlySummaryDTO expectedDTO = new MonthlySummaryDTO(List.of(jan, feb));

    when(metricsService.getMonthlySummary(accountId, year, null)).thenReturn(expectedDTO);

    ResponseEntity<ApiResponse<MonthlySummaryDTO>> response =
        metricsController.getMonthlySummary(mockUser, year, null, null, null);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getBody());
    assertTrue(response.getBody().isSuccess());
    assertEquals("Monthly summary retrieved successfully", response.getBody().getMessage());
    assertEquals(expectedDTO, response.getBody().getData());
    verify(metricsService, times(1)).getMonthlySummary(accountId, year, null);
  }

  @Test
  void getMonthlySummary_shouldReturnEmptyListWhenNoData() {
    int year = 2024;
    Integer accountId = mockUser.getAccount().getAccountId();

    MonthlySummaryDTO emptyDTO = new MonthlySummaryDTO(List.of());

    when(metricsService.getMonthlySummary(accountId, year, null)).thenReturn(emptyDTO);

    ResponseEntity<ApiResponse<MonthlySummaryDTO>> response =
        metricsController.getMonthlySummary(mockUser, year, null, null, null);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertTrue(response.getBody().isSuccess());
    assertTrue(response.getBody().getData().months().isEmpty());
  }

  @Test
  void getByCategory_shouldReturnOkAndCategoryBreakdownData() {
    int year = 2024;
    int month = 1;
    Integer accountId = mockUser.getAccount().getAccountId();

    CategoryBreakdownDTO.CategoryBreakdown food = new CategoryBreakdownDTO.CategoryBreakdown(
        "Food", "#FF5733", new BigDecimal("500"), new BigDecimal("62.5000")
    );

    CategoryBreakdownDTO.CategoryBreakdown transport = new CategoryBreakdownDTO.CategoryBreakdown(
        "Transport", "#33FF57", new BigDecimal("300"), new BigDecimal("37.5000")
    );

    CategoryBreakdownDTO expectedDTO = new CategoryBreakdownDTO(
        year, month, new BigDecimal("800"), List.of(food, transport)
    );

    when(metricsService.getByCategory(accountId, year, month, null)).thenReturn(expectedDTO);

    ResponseEntity<ApiResponse<CategoryBreakdownDTO>> response =
        metricsController.getByCategory(mockUser, year, month, null, null, null);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getBody());
    assertTrue(response.getBody().isSuccess());
    assertEquals("Category breakdown retrieved successfully", response.getBody().getMessage());
    assertEquals(expectedDTO, response.getBody().getData());
    verify(metricsService, times(1)).getByCategory(accountId, year, month, null);
  }

  @Test
  void getByCategory_shouldHandleEmptyBreakdown() {
    int year = 2024;
    int month = 1;
    Integer accountId = mockUser.getAccount().getAccountId();

    CategoryBreakdownDTO emptyDTO = new CategoryBreakdownDTO(
        year, month, BigDecimal.ZERO, List.of()
    );

    when(metricsService.getByCategory(accountId, year, month, null)).thenReturn(emptyDTO);

    ResponseEntity<ApiResponse<CategoryBreakdownDTO>> response =
        metricsController.getByCategory(mockUser, year, month, null, null, null);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertTrue(response.getBody().getData().categories().isEmpty());
  }

  @Test
  void getBudgetPerformance_shouldReturnOkAndBudgetPerformanceData() {
    int year = 2024;
    Integer accountId = mockUser.getAccount().getAccountId();

    BudgetPerformanceDTO.CategoryBudgetPerformance category = new BudgetPerformanceDTO.CategoryBudgetPerformance(
        "Food", "#FF5733", new BigDecimal("1000"), new BigDecimal("800"),
        new BigDecimal("200"), "OK"
    );

    BudgetPerformanceDTO.BudgetMonthPerformance month = new BudgetPerformanceDTO.BudgetMonthPerformance(
        1, List.of(category)
    );

    BudgetPerformanceDTO expectedDTO = new BudgetPerformanceDTO(year, List.of(month));

    when(metricsService.getBudgetPerformance(accountId, year, null)).thenReturn(expectedDTO);

    ResponseEntity<ApiResponse<BudgetPerformanceDTO>> response =
        metricsController.getBudgetPerformance(mockUser, year, null, null, null);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getBody());
    assertTrue(response.getBody().isSuccess());
    assertEquals("Budget performance retrieved successfully", response.getBody().getMessage());
    assertEquals(expectedDTO, response.getBody().getData());
    verify(metricsService, times(1)).getBudgetPerformance(accountId, year, null);
  }

  @Test
  void getBudgetPerformance_shouldHandleEmptyPerformance() {
    int year = 2024;
    Integer accountId = mockUser.getAccount().getAccountId();

    BudgetPerformanceDTO emptyDTO = new BudgetPerformanceDTO(year, List.of());

    when(metricsService.getBudgetPerformance(accountId, year, null)).thenReturn(emptyDTO);

    ResponseEntity<ApiResponse<BudgetPerformanceDTO>> response =
        metricsController.getBudgetPerformance(mockUser, year, null, null, null);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertTrue(response.getBody().getData().monthlyPerformance().isEmpty());
  }

  @Test
  void getOverview_shouldReturnOkAndDashboardOverviewData() {
    Integer accountId = mockUser.getAccount().getAccountId();
    LocalDate now = LocalDate.now();
    int currentMonth = now.getMonthValue();
    int currentYear = now.getYear();

    DashboardOverviewDTO expectedDTO = new DashboardOverviewDTO(
        new BigDecimal("5000"),
        new BigDecimal("3000"),
        new BigDecimal("2000"),
        1,
        3,
        "Food",
        new BigDecimal("500"),
        currentMonth,
        currentYear
    );

    when(metricsService.getOverview(accountId, null)).thenReturn(expectedDTO);

    ResponseEntity<ApiResponse<DashboardOverviewDTO>> response =
        metricsController.getOverview(mockUser, null, null, null);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getBody());
    assertTrue(response.getBody().isSuccess());
    assertEquals("Dashboard overview retrieved successfully", response.getBody().getMessage());
    assertEquals(expectedDTO, response.getBody().getData());
    verify(metricsService, times(1)).getOverview(accountId, null);
  }

  @Test
  void getOverview_shouldHandleNoData() {
    Integer accountId = mockUser.getAccount().getAccountId();
    LocalDate now = LocalDate.now();
    int currentMonth = now.getMonthValue();
    int currentYear = now.getYear();

    DashboardOverviewDTO emptyDTO = new DashboardOverviewDTO(
        BigDecimal.ZERO,
        BigDecimal.ZERO,
        BigDecimal.ZERO,
        0,
        0,
        "N/A",
        BigDecimal.ZERO,
        currentMonth,
        currentYear
    );

    when(metricsService.getOverview(accountId, null)).thenReturn(emptyDTO);

    ResponseEntity<ApiResponse<DashboardOverviewDTO>> response =
        metricsController.getOverview(mockUser, null, null, null);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(BigDecimal.ZERO, response.getBody().getData().totalIncome());
    assertEquals("N/A", response.getBody().getData().topExpenseCategory());
  }

  // ===== DATE RANGE AND DEFAULT PARAMS TESTS =====

  @Test
  void getMonthlySummary_withDateRange_shouldCallServiceWithDateRange() {
    Integer accountId = mockUser.getAccount().getAccountId();
    LocalDate startDate = LocalDate.of(2024, 1, 1);
    LocalDate endDate = LocalDate.of(2024, 3, 31);

    MonthlySummaryDTO.MonthSummary jan = new MonthlySummaryDTO.MonthSummary(
        2024, 1, new BigDecimal("5000"), new BigDecimal("3000"), new BigDecimal("2000")
    );

    MonthlySummaryDTO expectedDTO = new MonthlySummaryDTO(List.of(jan));

    when(metricsService.getMonthlySummary(accountId, startDate, endDate, null)).thenReturn(expectedDTO);

    ResponseEntity<ApiResponse<MonthlySummaryDTO>> response =
        metricsController.getMonthlySummary(mockUser, null, startDate, endDate, null);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertTrue(response.getBody().isSuccess());
    assertEquals(expectedDTO, response.getBody().getData());
    verify(metricsService, times(1)).getMonthlySummary(accountId, startDate, endDate, null);
    verify(metricsService, never()).getMonthlySummary(eq(accountId), anyInt(), isNull());
  }

  @Test
  void getMonthlySummary_withNoParams_shouldCallServiceWithCurrentYear() {
    Integer accountId = mockUser.getAccount().getAccountId();
    LocalDate now = LocalDate.now();
    int currentYear = now.getYear();

    MonthlySummaryDTO expectedDTO = new MonthlySummaryDTO(List.of());

    when(metricsService.getMonthlySummary(accountId, currentYear, null)).thenReturn(expectedDTO);

    ResponseEntity<ApiResponse<MonthlySummaryDTO>> response =
        metricsController.getMonthlySummary(mockUser, null, null, null, null);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    verify(metricsService, times(1)).getMonthlySummary(accountId, currentYear, null);
  }

  @Test
  void getByCategory_withDateRange_shouldCallServiceWithDateRange() {
    Integer accountId = mockUser.getAccount().getAccountId();
    LocalDate startDate = LocalDate.of(2024, 1, 1);
    LocalDate endDate = LocalDate.of(2024, 3, 31);

    CategoryBreakdownDTO expectedDTO = new CategoryBreakdownDTO(
        2024, 1, new BigDecimal("800"), List.of()
    );

    when(metricsService.getByCategory(accountId, startDate, endDate, null)).thenReturn(expectedDTO);

    ResponseEntity<ApiResponse<CategoryBreakdownDTO>> response =
        metricsController.getByCategory(mockUser, null, null, startDate, endDate, null);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertTrue(response.getBody().isSuccess());
    assertEquals(expectedDTO, response.getBody().getData());
    verify(metricsService, times(1)).getByCategory(accountId, startDate, endDate, null);
    verify(metricsService, never()).getByCategory(eq(accountId), anyInt(), anyInt(), isNull());
  }

  @Test
  void getByCategory_withNoParams_shouldCallServiceWithCurrentMonth() {
    Integer accountId = mockUser.getAccount().getAccountId();
    LocalDate now = LocalDate.now();
    int currentMonth = now.getMonthValue();
    int currentYear = now.getYear();

    CategoryBreakdownDTO expectedDTO = new CategoryBreakdownDTO(
        currentYear, currentMonth, BigDecimal.ZERO, List.of()
    );

    when(metricsService.getByCategory(accountId, currentYear, currentMonth, null)).thenReturn(expectedDTO);

    ResponseEntity<ApiResponse<CategoryBreakdownDTO>> response =
        metricsController.getByCategory(mockUser, null, null, null, null, null);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    verify(metricsService, times(1)).getByCategory(accountId, currentYear, currentMonth, null);
  }

  @Test
  void getBudgetPerformance_withDateRange_shouldCallServiceWithDateRange() {
    Integer accountId = mockUser.getAccount().getAccountId();
    LocalDate startDate = LocalDate.of(2024, 1, 1);
    LocalDate endDate = LocalDate.of(2024, 3, 31);

    BudgetPerformanceDTO expectedDTO = new BudgetPerformanceDTO(2024, List.of());

    when(metricsService.getBudgetPerformance(accountId, startDate, endDate, null)).thenReturn(expectedDTO);

    ResponseEntity<ApiResponse<BudgetPerformanceDTO>> response =
        metricsController.getBudgetPerformance(mockUser, null, startDate, endDate, null);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertTrue(response.getBody().isSuccess());
    assertEquals(expectedDTO, response.getBody().getData());
    verify(metricsService, times(1)).getBudgetPerformance(accountId, startDate, endDate, null);
    verify(metricsService, never()).getBudgetPerformance(eq(accountId), anyInt(), isNull());
  }

  @Test
  void getBudgetPerformance_withNoParams_shouldCallServiceWithCurrentYear() {
    Integer accountId = mockUser.getAccount().getAccountId();
    LocalDate now = LocalDate.now();
    int currentYear = now.getYear();

    BudgetPerformanceDTO expectedDTO = new BudgetPerformanceDTO(currentYear, List.of());

    when(metricsService.getBudgetPerformance(accountId, currentYear, null)).thenReturn(expectedDTO);

    ResponseEntity<ApiResponse<BudgetPerformanceDTO>> response =
        metricsController.getBudgetPerformance(mockUser, null, null, null, null);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    verify(metricsService, times(1)).getBudgetPerformance(accountId, currentYear, null);
  }

  @Test
  void getOverview_withDateRange_shouldCallServiceWithDateRange() {
    Integer accountId = mockUser.getAccount().getAccountId();
    LocalDate startDate = LocalDate.of(2024, 1, 1);
    LocalDate endDate = LocalDate.of(2024, 3, 31);

    DashboardOverviewDTO expectedDTO = new DashboardOverviewDTO(
        new BigDecimal("5000"),
        new BigDecimal("3000"),
        new BigDecimal("2000"),
        1,
        3,
        "Food",
        new BigDecimal("500"),
        1,
        2024
    );

    when(metricsService.getOverview(accountId, startDate, endDate, null)).thenReturn(expectedDTO);

    ResponseEntity<ApiResponse<DashboardOverviewDTO>> response =
        metricsController.getOverview(mockUser, startDate, endDate, null);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertTrue(response.getBody().isSuccess());
    assertEquals(expectedDTO, response.getBody().getData());
    verify(metricsService, times(1)).getOverview(accountId, startDate, endDate, null);
    verify(metricsService, never()).getOverview(eq(accountId), isNull());
  }

  @Test
  void getMonthlySummary_withYearOnly_shouldPreferYearOverDefault() {
    Integer accountId = mockUser.getAccount().getAccountId();
    int year = 2023;

    MonthlySummaryDTO expectedDTO = new MonthlySummaryDTO(List.of());

    when(metricsService.getMonthlySummary(accountId, year, null)).thenReturn(expectedDTO);

    ResponseEntity<ApiResponse<MonthlySummaryDTO>> response =
        metricsController.getMonthlySummary(mockUser, year, null, null, null);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    verify(metricsService, times(1)).getMonthlySummary(accountId, year, null);
  }

  @Test
  void getByCategory_withYearOnly_shouldUseCurrentMonth() {
    Integer accountId = mockUser.getAccount().getAccountId();
    LocalDate now = LocalDate.now();
    int year = now.getYear(); // Use current year instead of hardcoded 2023
    int currentMonth = now.getMonthValue();

    CategoryBreakdownDTO expectedDTO = new CategoryBreakdownDTO(
        year, currentMonth, BigDecimal.ZERO, List.of()
    );

    when(metricsService.getByCategory(accountId, year, currentMonth, null)).thenReturn(expectedDTO);

    ResponseEntity<ApiResponse<CategoryBreakdownDTO>> response =
        metricsController.getByCategory(mockUser, year, null, null, null, null);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    verify(metricsService, times(1)).getByCategory(accountId, year, currentMonth, null);
  }
}
