package fun.trackmoney.controller.docs;

import fun.trackmoney.dto.budget.BudgetHistoryGenerateDTO;
import fun.trackmoney.dto.budget.BudgetHistoryGenerationResponse;
import fun.trackmoney.dto.budget.BudgetHistoryResponseDTO;
import fun.trackmoney.entity.UserEntity;
import fun.trackmoney.utils.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;

import java.util.List;

@Tag(name = "Budget History", description = "Endpoints for generating and viewing monthly budget performance history")
public interface BudgetHistoryControllerDocs {

  @Operation(summary = "Generate budget history",
      description = "Generates monthly budget snapshot history for a completed month")
  @ApiResponses(value = {
      @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201",
          description = "Budget history generated successfully"),
      @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200",
          description = "History already exists or no entries generated"),
      @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400",
          description = "Current month not allowed or no transactions found")
  })
  ResponseEntity<ApiResponse<BudgetHistoryGenerationResponse>> generateHistory(
      BudgetHistoryGenerateDTO dto,
      @Parameter(hidden = true) UserEntity currentUser
  );

  @Operation(summary = "Get budget history",
      description = "Retrieves budget history filtered by date range or all history")
  @ApiResponses(value = {
      @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200",
          description = "Budget history retrieved successfully")
  })
  ResponseEntity<ApiResponse<List<BudgetHistoryResponseDTO>>> getHistory(
      @Parameter(hidden = true) UserEntity currentUser,
      @Parameter(description = "Start month (1-12)", example = "1") Short startMonth,
      @Parameter(description = "Start year", example = "2024") Integer startYear,
      @Parameter(description = "End month (1-12)", example = "12") Short endMonth,
      @Parameter(description = "End year", example = "2024") Integer endYear,
      @Parameter(description = "Optional category ID filter") Integer categoryId
  );

  @Operation(summary = "Get budget history by month",
      description = "Retrieves budget history for a specific month and year")
  @ApiResponses(value = {
      @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200",
          description = "Budget history retrieved successfully")
  })
  ResponseEntity<ApiResponse<List<BudgetHistoryResponseDTO>>> getHistoryByMonth(
      @Parameter(description = "Month (1-12)", example = "5") Short month,
      @Parameter(description = "Year", example = "2024") Integer year,
      @Parameter(description = "Optional category ID filter") Integer categoryId,
      @Parameter(hidden = true) UserEntity currentUser
  );

  @Operation(summary = "Delete budget history entry",
      description = "Deletes a specific budget history snapshot by ID")
  @ApiResponses(value = {
      @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "204",
          description = "Budget history deleted successfully")
  })
  ResponseEntity<ApiResponse<Void>> deleteHistory(
      @Parameter(description = "Budget history entry ID", example = "1") Integer historyId,
      @Parameter(hidden = true) UserEntity currentUser
  );
}
