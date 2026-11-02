package fun.trackmoney.controller.docs;

import fun.trackmoney.dto.transaction.CreateTransactionDTO;
import fun.trackmoney.dto.transaction.TransactionResponseDTO;
import fun.trackmoney.dto.transaction.TransactionUpdateDTO;
import fun.trackmoney.entity.UserEntity;
import fun.trackmoney.utils.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Tag(name = "Transactions", description = "Endpoints for managing financial transactions, incomes, and expenses")
public interface TransactionControllerDocs {

  @Operation(summary = "Create transaction", description = "Registers a new income or expense transaction")
  @ApiResponses(value = {
      @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201",
          description = "Transaction created successfully"),
      @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400",
          description = "Account or Category not found, or invalid input")
  })
  ResponseEntity<ApiResponse<TransactionResponseDTO>> createTransaction(
      CreateTransactionDTO dto,
      @Parameter(hidden = true) UserEntity currentUser
  );

  @Operation(summary = "List all transactions",
      description = "Retrieves all transactions recorded for the authenticated user")
  @ApiResponses(value = {
      @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200",
          description = "Transactions retrieved successfully")
  })
  ResponseEntity<ApiResponse<List<TransactionResponseDTO>>> findAllTransaction(
      @Parameter(hidden = true) UserEntity currentUser
  );

  @Operation(summary = "Get paginated transactions",
      description = "Retrieves a paginated and filterable list of transactions")
  @ApiResponses(value = {
      @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200",
          description = "Paginated transactions retrieved successfully")
  })
  ResponseEntity<ApiResponse<Page<TransactionResponseDTO>>> getPaginatedTransactions(
      Pageable pageable,
      @Parameter(description = "Filter by transaction name (case-insensitive substring)") String transactionName,
      @Parameter(description = "Filter by category ID") Integer categoryId,
      @Parameter(description = "Filter by start date (YYYY-MM-DD)") LocalDate startDate,
      @Parameter(description = "Filter by end date (YYYY-MM-DD)") LocalDate endDate,
      @Parameter(hidden = true) UserEntity actualUser
  );

  @Operation(summary = "Get transaction by ID", description = "Retrieves a transaction by its ID")
  @ApiResponses(value = {
      @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200",
          description = "Transaction retrieved successfully"),
      @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404",
          description = "Transaction not found")
  })
  ResponseEntity<ApiResponse<TransactionResponseDTO>> findTransactionById(
      @Parameter(description = "Transaction ID", example = "1") Integer id,
      @Parameter(hidden = true) UserEntity currentUser
  );

  @Operation(summary = "Get total income", description = "Calculates the total income for the authenticated user")
  @ApiResponses(value = {
      @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200",
          description = "Income calculated successfully")
  })
  ResponseEntity<ApiResponse<BigDecimal>> getIncome(
      @Parameter(hidden = true) UserEntity actualUser
  );

  @Operation(summary = "Get total expense", description = "Calculates the total expense for the authenticated user")
  @ApiResponses(value = {
      @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200",
          description = "Expense calculated successfully")
  })
  ResponseEntity<ApiResponse<BigDecimal>> getExpense(
      @Parameter(hidden = true) UserEntity actualUser
  );

  @Operation(summary = "Update transaction", description = "Updates an existing transaction by its ID")
  @ApiResponses(value = {
      @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200",
          description = "Transaction updated successfully"),
      @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404",
          description = "Transaction not found")
  })
  ResponseEntity<ApiResponse<TransactionResponseDTO>> updateTransaction(
      @Parameter(description = "Transaction ID", example = "1") Integer id,
      TransactionUpdateDTO dto,
      @Parameter(hidden = true) UserEntity currentUser
  );

  @Operation(summary = "Delete transaction", description = "Deletes a transaction by its ID")
  @ApiResponses(value = {
      @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200",
          description = "Transaction deleted successfully"),
      @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404",
          description = "Transaction not found")
  })
  ResponseEntity<ApiResponse<Void>> deleteTransaction(
      @Parameter(description = "Transaction ID", example = "1") Integer id,
      @Parameter(hidden = true) UserEntity currentUser
  );
}
