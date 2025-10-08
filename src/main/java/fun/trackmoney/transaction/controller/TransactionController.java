package fun.trackmoney.transaction.controller;

import fun.trackmoney.transaction.dto.BillResponseDTO;
import fun.trackmoney.transaction.dto.CreateTransactionDTO;
import fun.trackmoney.transaction.dto.TransactionDTO;
import fun.trackmoney.transaction.dto.TransactionResponseDTO;
import fun.trackmoney.transaction.dto.TransactionUpdateDTO;
import fun.trackmoney.transaction.dto.TransactionsError;
import fun.trackmoney.transaction.dto.internal.TransactionFailure;
import fun.trackmoney.transaction.dto.internal.TransactionResult;
import fun.trackmoney.transaction.dto.internal.TransactionSuccess;
import fun.trackmoney.transaction.service.TransactionService;
import fun.trackmoney.user.entity.UserEntity;
import fun.trackmoney.utils.CustomFieldError;
import fun.trackmoney.utils.response.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("transaction")
public class TransactionController {

  private final TransactionService transactionService;

  public TransactionController(TransactionService transactionService) {
    this.transactionService = transactionService;
  }
  
  @PostMapping
  public ResponseEntity<ApiResponse<TransactionResponseDTO>> createTransaction(@RequestBody
                                                                               @Valid
                                                                               CreateTransactionDTO dto,
                                                                               @AuthenticationPrincipal
                                                                               UserEntity currentUser) {
    TransactionResult result = transactionService.createTransaction(dto, currentUser.getUserId());

    if(result instanceof TransactionSuccess) {
      TransactionResponseDTO responseDTO = ((TransactionSuccess) result).response();
      var body = ApiResponse.<TransactionResponseDTO>success()
          .message("Transfer created")
          .data(responseDTO)
          .build();
      return ResponseEntity.status(HttpStatus.CREATED).body(body);
    }
    TransactionFailure failure = (TransactionFailure) result;

    if(failure.error() == TransactionsError.ACCOUNT_NOT_FOUND) {
      ApiResponse<TransactionResponseDTO> bodyData = ApiResponse.<TransactionResponseDTO>failure()
          .message("Transaction create error.")
          .errors(new CustomFieldError("Account", "Account not found to update balance"))
          .build();
      return ResponseEntity.badRequest().body(bodyData);
    }

    ApiResponse<TransactionResponseDTO> bodyData = ApiResponse.<TransactionResponseDTO>failure()
        .message("Transaction create error.")
        .errors(new CustomFieldError("Category", "Category not found."))
        .build();
    return ResponseEntity.badRequest().body(bodyData);
  }

  @GetMapping
  public ResponseEntity<ApiResponse<List<TransactionResponseDTO>>> findAllTransaction() {
    return ResponseEntity.ok().body(
      ApiResponse.<List<TransactionResponseDTO>>success()
          .message("All transactions")
          .data(transactionService.findAllTransaction())
          .build()
    );
  }

  @GetMapping("/page")
  public ResponseEntity<ApiResponse<Page<TransactionDTO>>> getPaginatedTransactions(Pageable pageable) {
    Page<TransactionResponseDTO> page = transactionService.getPaginatedTransactions(pageable);
    Page<TransactionDTO> dtoPage = page.map(TransactionDTO::from);
    return ResponseEntity.ok().body(
      ApiResponse.<Page<TransactionDTO>>success()
          .message("Paginated transactions")
          .data(dtoPage)
          .build()
    );
  }

  @GetMapping("/{id}")
  public ResponseEntity<ApiResponse<TransactionResponseDTO>> findTransactionById(@PathVariable Integer id) {
    return ResponseEntity.ok().body(
        ApiResponse.<TransactionResponseDTO>success()
            .message("Get transaction by id")
            .data(transactionService.findById(id))
            .build()
    );
  }

  @GetMapping("/income")
  public ResponseEntity<ApiResponse<BigDecimal>> getIncome(@AuthenticationPrincipal UserEntity actualUser) {
    var result = transactionService.getIncome(actualUser.getUserId());

    return ResponseEntity.ok().body(
      ApiResponse.<BigDecimal>success()
          .message("Get income")
          .data(result)
          .build()
    );
  }

  @GetMapping("/expense")
  public ResponseEntity<ApiResponse<BigDecimal>> getExpense(@AuthenticationPrincipal UserEntity actualUser) {
    var result = transactionService.getExpense(actualUser.getUserId());
    return ResponseEntity.ok().body(
      ApiResponse.<BigDecimal>success()
          .message("Get expense")
          .data(result)
          .build()
    );
  }

  @GetMapping("/bill/{id}")
  public ResponseEntity<ApiResponse<BillResponseDTO>> getBill(@PathVariable Integer id) {
    return ResponseEntity.ok().body(
      ApiResponse.<BillResponseDTO>success()
          .message("Get expense")
          .data(transactionService.getBill(id))
          .build()
    );
  }

  @PutMapping("/{id}")
  public ResponseEntity<ApiResponse<TransactionResponseDTO>> updateTransaction(@PathVariable Integer id,
                                                                               @RequestBody TransactionUpdateDTO dto) {
    return ResponseEntity.ok().body(
      ApiResponse.<TransactionResponseDTO>success()
          .message("Update transaction")
          .data(transactionService.update(id, dto))
          .build()
    );
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<ApiResponse<Void>> deleteTransaction(@PathVariable Integer id) {
    transactionService.delete(id);
    return ResponseEntity.ok().body(
      ApiResponse.<Void>success()
          .message("Deleted transaction")
          .build()
    );
  }
}
