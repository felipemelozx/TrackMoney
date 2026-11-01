package fun.trackmoney.controller.docs;

import fun.trackmoney.dto.budget.BudgetCreateDTO;
import fun.trackmoney.dto.budget.BudgetResponseDTO;
import fun.trackmoney.entity.UserEntity;
import fun.trackmoney.utils.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;

import java.util.List;

@Tag(name = "Budgets", description = "Endpoints for managing spending budgets and category limits")
public interface BudgetsControllerDocs {

  @Operation(summary = "Create budget", description = "Creates a new budget for a specific category")
  @ApiResponses(value = {
      @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201",
          description = "Budget created successfully"),
      @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400",
          description = "Category not found or percent limit exceeded")
  })
  ResponseEntity<ApiResponse<BudgetResponseDTO>> create(
      BudgetCreateDTO dto,
      @Parameter(hidden = true) UserEntity currentUser
  );

  @Operation(summary = "List all budgets", description = "Retrieves all budgets associated with the current user")
  @ApiResponses(value = {
      @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200",
          description = "Budgets retrieved successfully")
  })
  ResponseEntity<ApiResponse<List<BudgetResponseDTO>>> findAllByAccountId(
      @Parameter(hidden = true) UserEntity currentUser
  );

  @Operation(summary = "Get budget by ID", description = "Retrieves a specific budget by its ID")
  @ApiResponses(value = {
      @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200",
          description = "Budget found successfully"),
      @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404",
          description = "Budget not found")
  })
  ResponseEntity<ApiResponse<BudgetResponseDTO>> findById(
      @Parameter(description = "Budget ID", example = "1") Integer id,
      @Parameter(hidden = true) UserEntity currentUser
  );

  @Operation(summary = "Delete budget by ID", description = "Deletes an existing budget by its ID")
  @ApiResponses(value = {
      @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200",
          description = "Budget deleted successfully")
  })
  ResponseEntity<ApiResponse<Void>> deleteById(
      @Parameter(description = "Budget ID", example = "1") Integer id,
      @Parameter(hidden = true) UserEntity currentUser
  );

  @Operation(summary = "Update budget", description = "Updates an existing budget by its ID")
  @ApiResponses(value = {
      @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200",
          description = "Budget updated successfully"),
      @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400",
          description = "Percent limit exceeded or invalid data"),
      @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404",
          description = "Budget or Category not found")
  })
  ResponseEntity<ApiResponse<BudgetResponseDTO>> updateById(
      @Parameter(description = "Budget ID", example = "1") Integer id,
      BudgetCreateDTO dto,
      @Parameter(hidden = true) UserEntity currentUser
  );
}
