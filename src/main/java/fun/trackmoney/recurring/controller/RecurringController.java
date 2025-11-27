package fun.trackmoney.recurring.controller;

import fun.trackmoney.recurring.dtos.CreateRecurringRequest;
import fun.trackmoney.recurring.dtos.RecurringResponse;
import fun.trackmoney.recurring.service.RecurringService;
import fun.trackmoney.user.entity.UserEntity;
import fun.trackmoney.utils.CustomFieldError;
import fun.trackmoney.utils.response.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("recurring")
public class RecurringController {

  private final RecurringService recurringService;

  public RecurringController(RecurringService recurringService) {
    this.recurringService = recurringService;
  }

  @PostMapping
  public ResponseEntity<ApiResponse<RecurringResponse>> createRecurring(@Valid
                                                                        @RequestBody
                                                                        CreateRecurringRequest request,
                                                                        @AuthenticationPrincipal
                                                                        UserEntity currentUser) {
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

}
