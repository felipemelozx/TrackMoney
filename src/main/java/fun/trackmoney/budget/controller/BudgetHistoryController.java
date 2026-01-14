package fun.trackmoney.budget.controller;

import fun.trackmoney.budget.dtos.BudgetHistoryGenerateDTO;
import fun.trackmoney.budget.dtos.BudgetHistoryGenerationResponse;
import fun.trackmoney.budget.dtos.BudgetHistoryResponseDTO;
import fun.trackmoney.budget.entity.BudgetHistoryEntity;
import fun.trackmoney.budget.mapper.BudgetHistoryMapper;
import fun.trackmoney.budget.service.BudgetHistoryService;
import fun.trackmoney.user.entity.UserEntity;
import fun.trackmoney.utils.response.ApiResponse;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
  private final BudgetHistoryMapper budgetHistoryMapper;

  public BudgetHistoryController(BudgetHistoryService budgetHistoryService,
                                   BudgetHistoryMapper budgetHistoryMapper) {
    this.budgetHistoryService = budgetHistoryService;
    this.budgetHistoryMapper = budgetHistoryMapper;
  }

  @PostMapping("/generate")
  public ResponseEntity<ApiResponse<BudgetHistoryGenerationResponse>> generateHistory(
      @RequestBody @Valid BudgetHistoryGenerateDTO dto,
      @AuthenticationPrincipal UserEntity currentUser) {

    int generatedCount = budgetHistoryService.generateHistoryForMonth(
        currentUser, dto.month(), dto.year()
    );

    if (generatedCount == 0) {
      return ResponseEntity.status(HttpStatus.OK).body(
          ApiResponse.<BudgetHistoryGenerationResponse>success()
              .message("History already exists for this month or no budgets found")
              .data(new BudgetHistoryGenerationResponse(generatedCount))
              .build()
      );
    }

    return ResponseEntity.status(HttpStatus.CREATED).body(
        ApiResponse.<BudgetHistoryGenerationResponse>success()
            .message("Generated " + generatedCount + " budget history entries")
            .data(new BudgetHistoryGenerationResponse(generatedCount))
            .build()
    );
  }

  @GetMapping
  public ResponseEntity<ApiResponse<List<BudgetHistoryResponseDTO>>> getHistory(
      @AuthenticationPrincipal UserEntity currentUser,
      @RequestParam(required = false) Short startMonth,
      @RequestParam(required = false) Integer startYear,
      @RequestParam(required = false) Short endMonth,
      @RequestParam(required = false) Integer endYear) {

    List<BudgetHistoryEntity> history;

    if (startMonth != null && startYear != null && endMonth != null && endYear != null) {
      history = budgetHistoryService.getHistoryByDateRange(
          currentUser, startMonth, startYear, endMonth, endYear
      );
    } else {
      history = budgetHistoryService.getAllHistory(currentUser);
    }

    List<BudgetHistoryResponseDTO> dtos = budgetHistoryMapper.entityListToResponseList(history);

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
      @AuthenticationPrincipal UserEntity currentUser) {

    List<BudgetHistoryResponseDTO> dtos = budgetHistoryMapper.entityListToResponseList(
        budgetHistoryService.getHistoryByDateRange(
            currentUser, month, year, month, year
        )
    );

    return ResponseEntity.status(HttpStatus.OK).body(
        ApiResponse.<List<BudgetHistoryResponseDTO>>success()
            .message("Retrieved budget history for " + month + "/" + year)
            .data(dtos)
            .build()
    );
  }
}
