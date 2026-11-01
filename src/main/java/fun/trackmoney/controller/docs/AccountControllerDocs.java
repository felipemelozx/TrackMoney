package fun.trackmoney.controller.docs;

import fun.trackmoney.dto.account.AccountRequestDTO;
import fun.trackmoney.dto.account.AccountResponseDTO;
import fun.trackmoney.dto.account.AccountUpdateRequestDTO;
import fun.trackmoney.utils.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;

import java.util.List;

@Tag(name = "Accounts", description = "Endpoints for managing user financial accounts")
public interface AccountControllerDocs {

  @Operation(summary = "Create account", description = "Creates a new financial account for the authenticated user")
  @ApiResponses(value = {
      @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201",
          description = "Account successfully created"),
      @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400",
          description = "Invalid account data provided")
  })
  ResponseEntity<ApiResponse<AccountResponseDTO>> createAccount(AccountRequestDTO dto);

  @Operation(summary = "List all accounts", description = "Retrieves all accounts for the authenticated user")
  @ApiResponses(value = {
      @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200",
          description = "Account list retrieved successfully")
  })
  ResponseEntity<ApiResponse<List<AccountResponseDTO>>> findAllAccounts();

  @Operation(summary = "Get account by ID", description = "Retrieves an account by its unique identifier")
  @ApiResponses(value = {
      @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200",
          description = "Account retrieved successfully"),
      @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404",
          description = "Account not found")
  })
  ResponseEntity<ApiResponse<AccountResponseDTO>> findAccountById(
      @Parameter(description = "Account ID", example = "1") Integer id
  );

  @Operation(summary = "Update account", description = "Updates an existing account by its ID")
  @ApiResponses(value = {
      @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200",
          description = "Account updated successfully"),
      @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400",
          description = "Invalid update payload provided"),
      @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404",
          description = "Account not found")
  })
  ResponseEntity<ApiResponse<AccountResponseDTO>> updateAccountById(
      @Parameter(description = "Account ID", example = "1") Integer id,
      AccountUpdateRequestDTO dto
  );

  @Operation(summary = "Delete account", description = "Deletes an account by its ID")
  @ApiResponses(value = {
      @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200",
          description = "Account deleted successfully"),
      @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404",
          description = "Account not found")
  })
  ResponseEntity<ApiResponse<Void>> deleteAccountById(
      @Parameter(description = "Account ID", example = "1") Integer id
  );
}
