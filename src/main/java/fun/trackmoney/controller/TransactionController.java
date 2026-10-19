package fun.trackmoney.controller;

import fun.trackmoney.dto.transaction.CreateTransactionDTO;
import fun.trackmoney.dto.transaction.TransactionResponseDTO;
import fun.trackmoney.dto.transaction.TransactionUpdateDTO;
import fun.trackmoney.common.result.HttpErrorMapper;
import fun.trackmoney.common.result.AppError;
import fun.trackmoney.common.result.ErrorCode;
import fun.trackmoney.common.result.Result;
import fun.trackmoney.service.TransactionService;
import fun.trackmoney.mapper.TransactionMapper;
import fun.trackmoney.entity.UserEntity;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("transaction")
public class TransactionController {

  private final TransactionService transactionService;
  private final TransactionMapper transactionMapper;

  public TransactionController(TransactionService transactionService, TransactionMapper transactionMapper) {
    this.transactionService = transactionService;
    this.transactionMapper = transactionMapper;
  }

  @PostMapping
  public ResponseEntity<ApiResponse<TransactionResponseDTO>> createTransaction(@RequestBody
                                                                               @Valid
                                                                               CreateTransactionDTO dto,
                                                                               @AuthenticationPrincipal
                                                                               UserEntity currentUser) {
    Result<TransactionResponseDTO> result = transactionService.createTransaction(dto, currentUser);
    return result.fold(responseDTO -> {
      var body = ApiResponse.<TransactionResponseDTO>success()
          .message("Transfer created")
          .data(responseDTO)
          .build();
      return ResponseEntity.status(HttpStatus.CREATED).body(body);
    }, error -> HttpErrorMapper.toResponse(error));
  }

  @GetMapping
  public ResponseEntity<ApiResponse<List<TransactionResponseDTO>>> findAllTransaction(@AuthenticationPrincipal
                                                                                      UserEntity currentUser) {
    var transactionsList = transactionService.findAllTransaction(currentUser);
    var body = ApiResponse.<List<TransactionResponseDTO>>success()
        .message("All transactions")
        .data(transactionsList)
        .build();
    return ResponseEntity.ok().body(body);
  }

  @GetMapping("/page")
  public ResponseEntity<ApiResponse<Page<TransactionResponseDTO>>> getPaginatedTransactions(
      Pageable pageable,
      @RequestParam(required = false)
      String transactionName,
      @RequestParam(required = false)
      Integer categoryId,
      @RequestParam(required = false)
      LocalDate startDate,
      @RequestParam(required = false)
      LocalDate endDate,
      @AuthenticationPrincipal
      UserEntity actualUser
  ) {
    var data = transactionService.getPaginatedTransactions(
        pageable, actualUser, transactionName, categoryId, startDate, endDate);
    return ResponseEntity.ok().body(
        ApiResponse.<Page<TransactionResponseDTO>>success()
            .message("Paginated transactions")
            .data(data)
            .build()
    );
  }

  @GetMapping("/{id}")
  public ResponseEntity<ApiResponse<TransactionResponseDTO>> findTransactionById(@PathVariable Integer id,
                                                                                 @AuthenticationPrincipal
                                                                                 UserEntity currentUser) {
    return transactionService.findById(id, currentUser)
        .map(transactionMapper::toResponseDTO)
        .map(responseDTO -> ResponseEntity.ok(ApiResponse.<TransactionResponseDTO>success()
            .message("Get transaction by id")
            .data(responseDTO)
            .build()))
        .orElseGet(() -> HttpErrorMapper.toResponse(new AppError(ErrorCode.TRANSACTION_NOT_FOUND)));
  }

  @GetMapping("/income")
  public ResponseEntity<ApiResponse<BigDecimal>> getIncome(@AuthenticationPrincipal UserEntity actualUser) {
    var result = transactionService.getIncome(actualUser.getUserId());
    var body = ApiResponse.<BigDecimal>success()
        .message("Get income")
        .data(result)
        .build();
    return ResponseEntity.ok().body(body);
  }

  @GetMapping("/expense")
  public ResponseEntity<ApiResponse<BigDecimal>> getExpense(@AuthenticationPrincipal UserEntity actualUser) {
    var result = transactionService.getExpense(actualUser);
    var body = ApiResponse.<BigDecimal>success()
        .message("Get expense")
        .data(result)
        .build();
    return ResponseEntity.ok().body(body);
  }

  @PutMapping("/{id}")
  public ResponseEntity<ApiResponse<TransactionResponseDTO>> updateTransaction(@PathVariable Integer id,
                                                                               @RequestBody TransactionUpdateDTO dto,
                                                                               @AuthenticationPrincipal
                                                                               UserEntity currentUser) {

    return transactionService.update(id, dto, currentUser).fold(
        responseDTO -> ResponseEntity.ok(ApiResponse.<TransactionResponseDTO>success()
            .message("Update transaction")
            .data(responseDTO)
            .build()),
        error -> HttpErrorMapper.toResponse(error));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<ApiResponse<Void>> deleteTransaction(@PathVariable Integer id,
                                                             @AuthenticationPrincipal
                                                             UserEntity currentUser) {
    transactionService.delete(id, currentUser);
    return ResponseEntity.ok().body(
        ApiResponse.<Void>success()
            .message("Deleted transaction")
            .build()
    );
  }
}
