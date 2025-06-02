package fun.trackmoney.transaction.controller;

import fun.trackmoney.transaction.dto.CreateTransactionDTO;
import fun.trackmoney.transaction.dto.TransactionResponseDTO;
import fun.trackmoney.transaction.dto.TransactionUpdateDTO;
import fun.trackmoney.transaction.service.TransactionService;
import fun.trackmoney.utils.response.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
  
  @PostMapping("/create")
  public ResponseEntity<ApiResponse<TransactionResponseDTO>> createTransaction(@RequestBody
                                                                                 CreateTransactionDTO transactionDTO) {
    TransactionResponseDTO res = transactionService.createTransaction(transactionDTO);
    return ResponseEntity.status(HttpStatus.CREATED).body(
        new ApiResponse<>(true,"Transfere created", res,null));
  }

  @GetMapping
  public ResponseEntity<ApiResponse<List<TransactionResponseDTO>>> findAllTransaction() {
    return ResponseEntity.ok().body(
        new ApiResponse<>(true,"All transaction", transactionService.findAllTransaction(),null));
  }

  @GetMapping("/{id}")
  public ResponseEntity<ApiResponse<TransactionResponseDTO>> findTransactionById(@PathVariable Integer id) {
    return ResponseEntity.ok().body(
        new ApiResponse<>(true,"Get transaction by id", transactionService.findById(id),null));
  }

  @GetMapping("/income/{id}")
  public ResponseEntity<ApiResponse<BigDecimal>> getIncome(@PathVariable Integer id) {
    return ResponseEntity.ok().body(
        new ApiResponse<>(true,"Get income", transactionService.getIncome(id),null));
  }

  @GetMapping("/expense/{id}")
  public ResponseEntity<ApiResponse<BigDecimal>> getExpense(@PathVariable Integer id) {
    return ResponseEntity.ok().body(
        new ApiResponse<>(true,"Get expense", transactionService.getExpense(id),null));
  }

  @PutMapping("/{id}")
  public ResponseEntity<ApiResponse<TransactionResponseDTO>> updateTransaction(@PathVariable Integer id,
                                                                               @RequestBody TransactionUpdateDTO dto) {
    return ResponseEntity.ok().body(
        new ApiResponse<>(true,"Update transaction", transactionService.update(id, dto),null));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<ApiResponse<String>> deleteTransaction(@PathVariable Integer id) {
    transactionService.delete(id);
    return ResponseEntity.ok().body(
        new ApiResponse<>(true,"delete transaction", "Transaction deleted.",null));
  }
}
