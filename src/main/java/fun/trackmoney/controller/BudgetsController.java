package fun.trackmoney.controller;

import fun.trackmoney.dto.budget.BudgetCreateDTO;
import fun.trackmoney.dto.budget.BudgetResponseDTO;
import fun.trackmoney.common.result.HttpErrorMapper;
import fun.trackmoney.common.result.Result;
import fun.trackmoney.service.BudgetsService;
import fun.trackmoney.entity.UserEntity;
import fun.trackmoney.utils.response.ApiResponse;
import jakarta.validation.Valid;
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

import java.util.List;

@RestController
@RequestMapping("budgets")
public class BudgetsController {

  private final BudgetsService budgetsService;

  public BudgetsController(BudgetsService budgetsService) {
    this.budgetsService = budgetsService;
  }

  @PostMapping
  public ResponseEntity<ApiResponse<BudgetResponseDTO>> create(@RequestBody
                                                               @Valid
                                                               BudgetCreateDTO dto,
                                                               @AuthenticationPrincipal UserEntity currentUser) {

    Result<BudgetResponseDTO> budgetResult = budgetsService.create(dto, currentUser);
    return budgetResult.fold(data -> ResponseEntity
          .status(HttpStatus.CREATED)
          .body(ApiResponse.<BudgetResponseDTO>success()
              .message("Budget created")
              .data(data)
              .build()), HttpErrorMapper::toResponse);
  }

  @GetMapping
  public ResponseEntity<ApiResponse<List<BudgetResponseDTO>>> findAllByAccountId(@AuthenticationPrincipal
                                                                                 UserEntity currentUser) {
    var list = budgetsService.findAllBudgets(currentUser);
    return ResponseEntity.status(HttpStatus.OK).body(
        ApiResponse.<List<BudgetResponseDTO>>success()
            .message("Get all Budget")
            .data(list)
            .build()
    );
  }

  @GetMapping("/{id}")
  public ResponseEntity<ApiResponse<BudgetResponseDTO>> findById(@PathVariable Integer id,
                                                                 @AuthenticationPrincipal UserEntity currentUser) {
    var budget = budgetsService.findById(id, currentUser);

    if (budget == null) {
      var body = ApiResponse.<BudgetResponseDTO>failure()
          .message("Budget not found")
          .build();
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
    }
    var body = ApiResponse.<BudgetResponseDTO>success()
        .message("Get Budget by id")
        .data(budget)
        .build();
    return ResponseEntity.status(HttpStatus.OK).body(body);
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<ApiResponse<Void>> deleteById(@PathVariable Integer id,
                                                      @AuthenticationPrincipal UserEntity currentUser) {
    budgetsService.deleteById(id, currentUser);
    return ResponseEntity.status(HttpStatus.OK).body(
        ApiResponse.<Void>success()
            .message("Delete Budget by id")
            .build()
    );
  }

  @PutMapping("/{id}")
  public ResponseEntity<ApiResponse<BudgetResponseDTO>> updateById(@PathVariable Integer id,
                                                                   @RequestBody
                                                                   @Valid
                                                                   BudgetCreateDTO dto,
                                                                   @AuthenticationPrincipal UserEntity currentUser) {

    Result<BudgetResponseDTO> budgetResult = budgetsService.update(dto, id, currentUser);
    return budgetResult.fold(data -> ResponseEntity
          .status(HttpStatus.OK)
          .body(ApiResponse.<BudgetResponseDTO>success()
              .message("Budget updated successfully")
              .data(data)
              .build()), HttpErrorMapper::toResponse);
  }

}
