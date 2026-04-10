package fun.trackmoney.controller;

import fun.trackmoney.dto.metrics.response.BudgetPerformanceDTO;
import fun.trackmoney.dto.metrics.response.CategoryBreakdownDTO;
import fun.trackmoney.dto.metrics.response.DashboardOverviewDTO;
import fun.trackmoney.dto.metrics.response.MonthlySummaryDTO;
import fun.trackmoney.service.MetricsService;
import fun.trackmoney.entity.UserEntity;
import fun.trackmoney.utils.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

/**
 * REST controller for metrics and analytics endpoints.
 * Provides aggregated financial data for dashboards and reports.
 */
@RestController
@RequestMapping("metrics")
@Tag(name = "Metrics", description = "Financial analytics and dashboard endpoints")
public class MetricsController {

  private final MetricsService metricsService;

  public MetricsController(MetricsService metricsService) {
    this.metricsService = metricsService;
  }

  /**
   * Gets monthly income vs expense summary.
   * Can filter by year or by a custom date range.
   *
   * @param currentUser the authenticated user
   * @param year        the year to get data for (used if startDate/endDate not provided)
   * @param startDate   the start date (optional, overrides year if provided)
   * @param endDate     the end date (optional, overrides year if provided)
   * @return monthly summary with income, expense, and balance per month
   */
  @GetMapping("/monthly-summary")
  @Operation(summary = "Get monthly income vs expense summary")
  public ResponseEntity<ApiResponse<MonthlySummaryDTO>> getMonthlySummary(
      @AuthenticationPrincipal UserEntity currentUser,
      @RequestParam(required = false) @Min(2000) @Max(2100) Integer year,
      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
      @RequestParam(required = false) @Min(1) Integer categoryId
  ) {
    Integer accountId = currentUser.getAccount().getAccountId();
    MonthlySummaryDTO result;

    if (startDate != null && endDate != null) {
      result = metricsService.getMonthlySummary(accountId, startDate, endDate, categoryId);
    } else if (year != null) {
      result = metricsService.getMonthlySummary(accountId, year, categoryId);
    } else {
      // Default to current year if no parameters provided
      result = metricsService.getMonthlySummary(accountId, LocalDate.now().getYear(), categoryId);
    }

    ApiResponse<MonthlySummaryDTO> body = ApiResponse.<MonthlySummaryDTO>success()
        .message("Monthly summary retrieved successfully")
        .data(result)
        .build();

    return ResponseEntity.ok(body);
  }

  /**
   * Gets expense breakdown by category.
   * Can filter by year/month or by a custom date range.
   *
   * @param currentUser the authenticated user
   * @param year        the year (used if startDate/endDate not provided)
   * @param month       the month (1-12, used if startDate/endDate not provided)
   * @param startDate   the start date (optional, overrides year/month if provided)
   * @param endDate     the end date (optional, overrides year/month if provided)
   * @return category breakdown with amounts and percentages
   */
  @GetMapping("/by-category")
  @Operation(summary = "Get expense breakdown by category")
  public ResponseEntity<ApiResponse<CategoryBreakdownDTO>> getByCategory(
      @AuthenticationPrincipal UserEntity currentUser,
      @RequestParam(required = false) @Min(2000) @Max(2100) Integer year,
      @RequestParam(required = false) @Min(1) @Max(12) Integer month,
      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
      @RequestParam(required = false) @Min(1) Integer categoryId
  ) {
    Integer accountId = currentUser.getAccount().getAccountId();
    CategoryBreakdownDTO result;

    if (startDate != null && endDate != null) {
      result = metricsService.getByCategory(accountId, startDate, endDate, categoryId);
    } else if (year != null && month != null) {
      result = metricsService.getByCategory(accountId, year, month, categoryId);
    } else {
      // Default to current month if no parameters provided
      LocalDate now = LocalDate.now();
      result = metricsService.getByCategory(accountId, now.getYear(), now.getMonthValue(), categoryId);
    }

    ApiResponse<CategoryBreakdownDTO> body = ApiResponse.<CategoryBreakdownDTO>success()
        .message("Category breakdown retrieved successfully")
        .data(result)
        .build();

    return ResponseEntity.ok(body);
  }

  /**
   * Gets budget performance history.
   * Can filter by year or by a custom date range.
   *
   * @param currentUser the authenticated user
   * @param year        the year to get data for (used if startDate/endDate not provided)
   * @param startDate   the start date (optional, overrides year if provided)
   * @param endDate     the end date (optional, overrides year if provided)
   * @return budget performance grouped by month and category
   */
  @GetMapping("/budgets/performance")
  @Operation(summary = "Get budget performance history")
  public ResponseEntity<ApiResponse<BudgetPerformanceDTO>> getBudgetPerformance(
      @AuthenticationPrincipal UserEntity currentUser,
      @RequestParam(required = false) @Min(2000) @Max(2100) Integer year,
      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
      @RequestParam(required = false) @Min(1) Integer categoryId
  ) {
    Integer accountId = currentUser.getAccount().getAccountId();
    BudgetPerformanceDTO result;

    if (startDate != null && endDate != null) {
      result = metricsService.getBudgetPerformance(accountId, startDate, endDate, categoryId);
    } else if (year != null) {
      result = metricsService.getBudgetPerformance(accountId, year, categoryId);
    } else {
      // Default to current year if no parameters provided
      result = metricsService.getBudgetPerformance(accountId, LocalDate.now().getYear(), categoryId);
    }

    ApiResponse<BudgetPerformanceDTO> body = ApiResponse.<BudgetPerformanceDTO>success()
        .message("Budget performance retrieved successfully")
        .data(result)
        .build();

    return ResponseEntity.ok(body);
  }

  /**
   * Gets dashboard overview.
   * Can show current month overview or a custom date range overview.
   *
   * @param currentUser the authenticated user
   * @param startDate   the start date (optional, if provided shows overview for date range)
   * @param endDate     the end date (optional, if provided shows overview for date range)
   * @return dashboard overview with financials and budget status
   */
  @GetMapping("/overview")
  @Operation(summary = "Get dashboard overview")
  public ResponseEntity<ApiResponse<DashboardOverviewDTO>> getOverview(
      @AuthenticationPrincipal UserEntity currentUser,
      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
      @RequestParam(required = false) @Min(1) Integer categoryId
  ) {
    Integer accountId = currentUser.getAccount().getAccountId();
    DashboardOverviewDTO result;

    if (startDate != null && endDate != null) {
      result = metricsService.getOverview(accountId, startDate, endDate, categoryId);
    } else {
      // Default to current month if no parameters provided
      result = metricsService.getOverview(accountId, categoryId);
    }

    ApiResponse<DashboardOverviewDTO> body = ApiResponse.<DashboardOverviewDTO>success()
        .message("Dashboard overview retrieved successfully")
        .data(result)
        .build();

    return ResponseEntity.ok(body);
  }
}
