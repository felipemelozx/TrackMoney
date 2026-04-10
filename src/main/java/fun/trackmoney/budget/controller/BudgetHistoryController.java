package fun.trackmoney.budget.controller;

import fun.trackmoney.budget.dtos.BudgetHistoryGenerateDTO;
import fun.trackmoney.budget.dtos.BudgetHistoryGenerationResponse;
import fun.trackmoney.budget.dtos.BudgetHistoryResponseDTO;
import fun.trackmoney.budget.dtos.GenerationResultDTO;
import fun.trackmoney.entity.BudgetHistoryEntity;
import fun.trackmoney.budget.service.BudgetHistoryService;
import fun.trackmoney.entity.UserEntity;
import fun.trackmoney.utils.response.ApiResponse;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("budgets/history")
public class BudgetHistoryController {

  private final BudgetHistoryService budgetHistoryService;

  public BudgetHistoryController(BudgetHistoryService budgetHistoryService) {
    this.budgetHistoryService = budgetHistoryService;
  }

  @PostMapping("/generate")
  public ResponseEntity<ApiResponse<BudgetHistoryGenerationResponse>> generateHistory(
      @RequestBody @Valid BudgetHistoryGenerateDTO dto,
      @AuthenticationPrincipal UserEntity currentUser) {

    GenerationResultDTO result = budgetHistoryService.generateHistoryForMonth(
        currentUser, dto.month(), dto.year()
    );

    return buildGenerationResponse(result);
  }

  private ResponseEntity<ApiResponse<BudgetHistoryGenerationResponse>> buildGenerationResponse(
      GenerationResultDTO result) {

    if (result.isSuccess()) {
      return ResponseEntity.status(HttpStatus.CREATED).body(
          ApiResponse.<BudgetHistoryGenerationResponse>success()
              .message("Generated " + result.generatedCount() + " budget history entries")
              .data(new BudgetHistoryGenerationResponse(result.generatedCount()))
              .build()
      );
    }

    if (result.isAlreadyExists()) {
      return ResponseEntity.status(HttpStatus.OK).body(
          ApiResponse.<BudgetHistoryGenerationResponse>success()
              .message("History already exists for this month")
              .data(new BudgetHistoryGenerationResponse(result.generatedCount()))
              .build()
      );
    }

    if (result.isCurrentMonthNotAllowed()) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
          ApiResponse.<BudgetHistoryGenerationResponse>failure()
              .message("Cannot generate history for the current month")
              .data(new BudgetHistoryGenerationResponse(result.generatedCount()))
              .build()
      );
    }

    if (result.isNoTransactions()) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
          ApiResponse.<BudgetHistoryGenerationResponse>failure()
              .message("Cannot generate history: no transactions found for this month")
              .data(new BudgetHistoryGenerationResponse(result.generatedCount()))
              .build()
      );
    }

    return ResponseEntity.status(HttpStatus.OK).body(
        ApiResponse.<BudgetHistoryGenerationResponse>success()
            .message("No budget history entries generated")
            .data(new BudgetHistoryGenerationResponse(result.generatedCount()))
            .build()
    );
  }

  @GetMapping
  public ResponseEntity<ApiResponse<List<BudgetHistoryResponseDTO>>> getHistory(
      @AuthenticationPrincipal UserEntity currentUser,
      @RequestParam(required = false) Short startMonth,
      @RequestParam(required = false) Integer startYear,
      @RequestParam(required = false) Short endMonth,
      @RequestParam(required = false) Integer endYear,
      @RequestParam(required = false) Integer categoryId) {

    List<BudgetHistoryEntity> history;

    if (startMonth != null && startYear != null && endMonth != null && endYear != null) {
      history = budgetHistoryService.getHistoryByDateRange(
          currentUser, startMonth, startYear, endMonth, endYear, categoryId
      );
    } else {
      history = budgetHistoryService.getAllHistory(currentUser, categoryId);
    }

    List<BudgetHistoryResponseDTO> dtos = budgetHistoryService.enrichWithTransactions(history);

    return ResponseEntity.ok().body(
        ApiResponse.<List<BudgetHistoryResponseDTO>>success()
            .message("Retrieved budget history")
            .data(dtos)
            .build()
    );
  }

  @GetMapping("/{month}/{year}")
  public ResponseEntity<ApiResponse<List<BudgetHistoryResponseDTO>>> getHistoryByMonth(
      @PathVariable Short month,
      @PathVariable Integer year,
      @RequestParam(required = false) Integer categoryId,
      @AuthenticationPrincipal UserEntity currentUser) {

    List<BudgetHistoryEntity> history = budgetHistoryService.getHistoryByDateRange(
        currentUser, month, year, month, year, categoryId
    );

    List<BudgetHistoryResponseDTO> dtos = budgetHistoryService.enrichWithTransactions(history);

    return ResponseEntity.status(HttpStatus.OK).body(
        ApiResponse.<List<BudgetHistoryResponseDTO>>success()
            .message("Retrieved budget history for " + month + "/" + year)
            .data(dtos)
            .build()
    );
  }

  @DeleteMapping("/{historyId}")
  public ResponseEntity<ApiResponse<Void>> deleteHistory(
      @PathVariable Integer historyId,
      @AuthenticationPrincipal UserEntity currentUser) {

    budgetHistoryService.deleteHistoryById(currentUser, historyId);

    return ResponseEntity.status(HttpStatus.NO_CONTENT).body(
        ApiResponse.<Void>success()
            .message("Budget history deleted successfully")
            .build()
    );
  }
}
