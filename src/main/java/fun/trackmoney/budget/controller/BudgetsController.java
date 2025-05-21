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
        new ApiResponse<>(true, "Budget created", budgetsService.create(dto), null));
  }

  @GetMapping
  public ResponseEntity<ApiResponse<List<BudgetResponseDTO>>> findAll() {
    var list = budgetsService.findAll();
    return ResponseEntity.status(HttpStatus.OK).body(
        new ApiResponse<>(true, "Get all Budget",list , null));
  }

  @GetMapping("/{id}")
  public ResponseEntity<ApiResponse<BudgetResponseDTO>> findById(@PathVariable Integer id) {
    return ResponseEntity.status(HttpStatus.OK).body(
        new ApiResponse<>(true, "Get Budget by Id", budgetsService.findById(id), null));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<ApiResponse<String>> deleteById(@PathVariable Integer id) {
    budgetsService.findById(id);
    return ResponseEntity.status(HttpStatus.OK).body(
        new ApiResponse<>(true, "Delete Budget by id", "Budget deleted", null));
  }

  @PutMapping("/{id}")
  public ResponseEntity<ApiResponse<BudgetResponseDTO>> updateById(@PathVariable Integer id,
                                                                   @RequestBody BudgetCreateDTO dto) {
    return ResponseEntity.status(HttpStatus.OK).body(
        new ApiResponse<>(true, "Update Budget", budgetsService.update(dto,id), null));
  }
}

