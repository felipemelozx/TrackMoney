package fun.trackmoney.controller.docs;

import fun.trackmoney.dto.metrics.response.BudgetPerformanceDTO;
import fun.trackmoney.dto.metrics.response.CategoryBreakdownDTO;
import fun.trackmoney.dto.metrics.response.DashboardOverviewDTO;
import fun.trackmoney.dto.metrics.response.MonthlySummaryDTO;
import fun.trackmoney.entity.UserEntity;
import fun.trackmoney.utils.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;

@Tag(name = "Metrics", description = "Financial analytics and dashboard endpoints")
public interface MetricsControllerDocs {

  @Operation(summary = "Get monthly income vs expense summary",
      description = "Retrieves monthly income, expense, and balance summary by year or custom date range")
  @ApiResponses(value = {
      @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200",
          description = "Monthly summary retrieved successfully")
  })
  ResponseEntity<ApiResponse<MonthlySummaryDTO>> getMonthlySummary(
      @Parameter(hidden = true) UserEntity currentUser,
      @Parameter(description = "Year (2000-2100)", example = "2024") Integer year,
      @Parameter(description = "Start date (YYYY-MM-DD)") LocalDate startDate,
      @Parameter(description = "End date (YYYY-MM-DD)") LocalDate endDate,
      @Parameter(description = "Optional category ID filter") Integer categoryId
  );

  @Operation(summary = "Get expense breakdown by category",
      description = "Retrieves expense breakdown with amounts and percentages for each category")
  @ApiResponses(value = {
      @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200",
          description = "Category breakdown retrieved successfully")
  })
  ResponseEntity<ApiResponse<CategoryBreakdownDTO>> getByCategory(
      @Parameter(hidden = true) UserEntity currentUser,
      @Parameter(description = "Year (2000-2100)", example = "2024") Integer year,
      @Parameter(description = "Month (1-12)", example = "5") Integer month,
      @Parameter(description = "Start date (YYYY-MM-DD)") LocalDate startDate,
      @Parameter(description = "End date (YYYY-MM-DD)") LocalDate endDate,
      @Parameter(description = "Optional category ID filter") Integer categoryId
  );

  @Operation(summary = "Get budget performance history",
      description = "Retrieves budget performance metrics grouped by month and category")
  @ApiResponses(value = {
      @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200",
          description = "Budget performance retrieved successfully")
  })
  ResponseEntity<ApiResponse<BudgetPerformanceDTO>> getBudgetPerformance(
      @Parameter(hidden = true) UserEntity currentUser,
      @Parameter(description = "Year (2000-2100)", example = "2024") Integer year,
      @Parameter(description = "Start date (YYYY-MM-DD)") LocalDate startDate,
      @Parameter(description = "End date (YYYY-MM-DD)") LocalDate endDate,
      @Parameter(description = "Optional category ID filter") Integer categoryId
  );

  @Operation(summary = "Get dashboard overview",
      description = "Retrieves high-level dashboard overview with financials and budget status")
  @ApiResponses(value = {
      @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200",
          description = "Dashboard overview retrieved successfully")
  })
  ResponseEntity<ApiResponse<DashboardOverviewDTO>> getOverview(
      @Parameter(hidden = true) UserEntity currentUser,
      @Parameter(description = "Start date (YYYY-MM-DD)") LocalDate startDate,
      @Parameter(description = "End date (YYYY-MM-DD)") LocalDate endDate,
      @Parameter(description = "Optional category ID filter") Integer categoryId
  );
}
