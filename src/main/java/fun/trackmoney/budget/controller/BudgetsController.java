package fun.trackmoney.budget.controller;

import fun.trackmoney.budget.dtos.BudgetCreateDTO;
import fun.trackmoney.budget.dtos.BudgetResponseDTO;
import fun.trackmoney.budget.dtos.internal.BudgetFailure;
import fun.trackmoney.budget.dtos.internal.BudgetSuccess;
import fun.trackmoney.budget.service.BudgetsService;
import fun.trackmoney.user.entity.UserEntity;
import fun.trackmoney.utils.CustomFieldError;
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

    var budgetResult = budgetsService.create(dto, currentUser);

    if (budgetResult instanceof BudgetSuccess success) {
      return ResponseEntity
          .status(HttpStatus.CREATED)
          .body(ApiResponse.<BudgetResponseDTO>success()
              .message("Budget created")
              .data(success.response())
              .build());
    }

    BudgetFailure failure = (BudgetFailure) budgetResult;

    String field = switch (failure.reason()) {
      case CATEGORY_NOT_FOUND -> "categoryId";
      case PERCENT_LIMIT_EXCEEDED -> "percent";
      default -> "category";
    };

    return ResponseEntity
        .badRequest()
        .body(ApiResponse.<BudgetResponseDTO>failure()
            .message("Error while trying to save budget")
            .errors(new CustomFieldError(field, failure.message()))
            .build());
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

    var budgetResult = budgetsService.update(dto, id, currentUser);

    if (budgetResult instanceof BudgetSuccess success) {
      return ResponseEntity
          .status(HttpStatus.OK)
          .body(ApiResponse.<BudgetResponseDTO>success()
              .message("Budget updated successfully")
              .data(success.response())
              .build());
    }

    BudgetFailure failure = (BudgetFailure) budgetResult;

    String field = switch (failure.reason()) {
      case CATEGORY_NOT_FOUND -> "categoryId";
      case BUDGET_NOT_FOUND -> "id";
      case PERCENT_LIMIT_EXCEEDED -> "percent";
      default -> "category";
    };

    HttpStatus status = switch (failure.reason()) {
      case CATEGORY_NOT_FOUND, BUDGET_NOT_FOUND -> HttpStatus.NOT_FOUND;
      case PERCENT_LIMIT_EXCEEDED -> HttpStatus.BAD_REQUEST;
      default -> HttpStatus.BAD_REQUEST;
    };

    return ResponseEntity
        .status(status)
        .body(ApiResponse.<BudgetResponseDTO>failure()
            .message("Error while trying to update budget")
            .errors(new CustomFieldError(field, failure.message()))
            .build());
  }

}

