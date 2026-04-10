package fun.trackmoney.recurring.controller;

import fun.trackmoney.dto.recurring.CreateRecurringRequest;
import fun.trackmoney.dto.recurring.RecurringResponse;
import fun.trackmoney.service.RecurringService;
import fun.trackmoney.dto.transaction.BillResponseDTO;
import fun.trackmoney.entity.UserEntity;
import fun.trackmoney.utils.CustomFieldError;
import fun.trackmoney.utils.response.ApiResponse;
import jakarta.validation.Valid;
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
@RequestMapping("recurring")
public class RecurringController {

  private final RecurringService recurringService;

  public RecurringController(RecurringService recurringService) {
    this.recurringService = recurringService;
  }

  @PostMapping
  public ResponseEntity<ApiResponse<RecurringResponse>> createRecurring(
      @Valid @RequestBody
      CreateRecurringRequest request,
      @AuthenticationPrincipal
      UserEntity currentUser
  ) {
    RecurringResponse result = recurringService.create(request, currentUser);

    if (result == null) {
      String message = "Category with this id: " + request.categoryId() + " not found.";
      return ResponseEntity.badRequest().body(
          ApiResponse.<RecurringResponse>failure()
              .message("Failure when try to create the recurring transaction")
              .errors(new CustomFieldError("categoryId", message))
              .build()
      );
    }
    return ResponseEntity.ok().body(
        ApiResponse.<RecurringResponse>success()
            .message("Recurring created with successfully")
            .data(result)
            .build()
    );
  }

  @GetMapping
  public ResponseEntity<ApiResponse<List<RecurringResponse>>> findAll(
      @AuthenticationPrincipal UserEntity currentUser
  ) {
    List<RecurringResponse> recurringResponses = recurringService.findAll(currentUser);
    var body = ApiResponse.<List<RecurringResponse>>success()
        .message("Successfully retrieved all recurring transactions")
        .data(recurringResponses)
        .build();
    return ResponseEntity.ok(body);
  }

  @PutMapping("/{id}")
  public ResponseEntity<ApiResponse<RecurringResponse>> update(
      @PathVariable
      Long id,
      @Valid @RequestBody
      CreateRecurringRequest request,
      @AuthenticationPrincipal
      UserEntity currentUser
  ) {
    RecurringResponse result = recurringService.update(id, request, currentUser);

    if (result == null) {
      String message = "Category with this id: " + request.categoryId() + " not found.";
      return ResponseEntity.badRequest().body(
          ApiResponse.<RecurringResponse>failure()
              .message("Failure when try to update the recurring transaction")
              .errors(new CustomFieldError("categoryId", message))
              .build()
      );
    }
    return ResponseEntity.ok().body(
        ApiResponse.<RecurringResponse>success()
            .message("Recurring update with successfully")
            .data(result)
            .build()
    );
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<ApiResponse<Void>> delete(
      @PathVariable Long id,
      @AuthenticationPrincipal UserEntity currentUser
  ) {
    recurringService.delete(id, currentUser);
    return ResponseEntity.ok().body(ApiResponse.<Void>successWithNoContent().build());
  }

  @GetMapping("/bills")
  public ResponseEntity<ApiResponse<BillResponseDTO>> getBills(@AuthenticationPrincipal UserEntity currentUser) {
    BillResponseDTO bills = recurringService.getBill(currentUser);
    return ResponseEntity.ok().body(ApiResponse.<BillResponseDTO>success()
        .message("Successfully retrieved all bills")
        .data(bills)
        .build());
  }
}
