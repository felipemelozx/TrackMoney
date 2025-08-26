package fun.trackmoney.budget.controller;

import fun.trackmoney.budget.dtos.BudgetCreateDTO;
import fun.trackmoney.budget.dtos.BudgetResponseDTO;
import fun.trackmoney.budget.service.BudgetsService;
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

import java.util.List;

@RestController
@RequestMapping("budgets")
public class BudgetsController {

  private final BudgetsService budgetsService;

  public BudgetsController(BudgetsService budgetsService) {
    this.budgetsService = budgetsService;
  }

  @PostMapping
  public ResponseEntity<ApiResponse<BudgetResponseDTO>> create(@RequestBody BudgetCreateDTO dto) {
    return ResponseEntity.status(HttpStatus.CREATED).body(
        ApiResponse.<BudgetResponseDTO>success()
            .message("Budget created")
            .data(budgetsService.create(dto))
            .build()
    );
  }

  @GetMapping("/findAll/{accountId}")
  public ResponseEntity<ApiResponse<List<BudgetResponseDTO>>> findAllByAccountId(@PathVariable Integer accountId) {
    var list = budgetsService.findAllByAccountId(accountId);
    return ResponseEntity.status(HttpStatus.OK).body(
        ApiResponse.<List<BudgetResponseDTO>>success()
            .message("Get all Budget")
            .data(list)
            .build()
    );
  }

  @GetMapping("/{id}")
  public ResponseEntity<ApiResponse<BudgetResponseDTO>> findById(@PathVariable Integer id) {
    return ResponseEntity.status(HttpStatus.OK).body(
        ApiResponse.<BudgetResponseDTO>success()
            .message("Get Budget by id")
            .data(budgetsService.findById(id))
            .build()
    );
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<ApiResponse<Void>> deleteById(@PathVariable Integer id) {
    budgetsService.findById(id);
    return ResponseEntity.status(HttpStatus.OK).body(
        ApiResponse.<Void>success()
            .message("Delete Budget by id")
            .build()
    );
  }

  @PutMapping("/{id}")
  public ResponseEntity<ApiResponse<BudgetResponseDTO>> updateById(@PathVariable Integer id,
                                                                   @RequestBody BudgetCreateDTO dto) {
    return ResponseEntity.status(HttpStatus.OK).body(
        ApiResponse.<BudgetResponseDTO>success()
            .message("Update budget")
            .data(budgetsService.update(dto, id))
            .build()
    );
  }
}

