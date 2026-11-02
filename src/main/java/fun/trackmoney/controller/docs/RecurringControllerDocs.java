package fun.trackmoney.controller.docs;

import fun.trackmoney.dto.recurring.CreateRecurringRequest;
import fun.trackmoney.dto.recurring.RecurringResponse;
import fun.trackmoney.dto.transaction.BillResponseDTO;
import fun.trackmoney.entity.UserEntity;
import fun.trackmoney.utils.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;

import java.util.List;

@Tag(name = "Recurring", description = "Endpoints for managing recurring transactions and recurring bills")
public interface RecurringControllerDocs {

  @Operation(summary = "Create recurring transaction",
      description = "Registers a new recurring financial transaction")
  @ApiResponses(value = {
      @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200",
          description = "Recurring transaction created successfully"),
      @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400",
          description = "Category not found or invalid input data")
  })
  ResponseEntity<ApiResponse<RecurringResponse>> createRecurring(
      CreateRecurringRequest request,
      @Parameter(hidden = true) UserEntity currentUser
  );

  @Operation(summary = "List all recurring transactions",
      description = "Retrieves all recurring transactions for the current user")
  @ApiResponses(value = {
      @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200",
          description = "Recurring transactions retrieved successfully")
  })
  ResponseEntity<ApiResponse<List<RecurringResponse>>> findAll(
      @Parameter(hidden = true) UserEntity currentUser
  );

  @Operation(summary = "Update recurring transaction", description = "Updates an existing recurring transaction")
  @ApiResponses(value = {
      @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200",
          description = "Recurring transaction updated successfully"),
      @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400",
          description = "Category not found or invalid payload")
  })
  ResponseEntity<ApiResponse<RecurringResponse>> update(
      @Parameter(description = "Recurring transaction ID", example = "1") Long id,
      CreateRecurringRequest request,
      @Parameter(hidden = true) UserEntity currentUser
  );

  @Operation(summary = "Delete recurring transaction", description = "Deletes a recurring transaction by its ID")
  @ApiResponses(value = {
      @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200",
          description = "Recurring transaction deleted successfully")
  })
  ResponseEntity<ApiResponse<Void>> delete(
      @Parameter(description = "Recurring transaction ID", example = "1") Long id,
      @Parameter(hidden = true) UserEntity currentUser
  );

  @Operation(summary = "Get recurring bills summary",
      description = "Retrieves recurring bills calculation and status summary")
  @ApiResponses(value = {
      @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200",
          description = "Bills summary retrieved successfully")
  })
  ResponseEntity<ApiResponse<BillResponseDTO>> getBills(
      @Parameter(hidden = true) UserEntity currentUser
  );
}
