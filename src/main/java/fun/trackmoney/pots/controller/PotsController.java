package fun.trackmoney.pots.controller;

import fun.trackmoney.pots.dtos.MoneyRequest;
import fun.trackmoney.pots.dtos.CreatePotsDTO;
import fun.trackmoney.pots.dtos.PotsResponseDTO;
import fun.trackmoney.pots.dtos.internal.PotsFailure;
import fun.trackmoney.pots.dtos.internal.PotsResult;
import fun.trackmoney.pots.dtos.internal.PotsSuccess;
import fun.trackmoney.pots.service.PotsService;
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
@RequestMapping("pots")
public class PotsController {

  private final PotsService potsService;

  public PotsController(PotsService potsService) {
    this.potsService = potsService;
  }

  @PostMapping
  public ResponseEntity<ApiResponse<PotsResponseDTO>> createPots(@Valid @RequestBody CreatePotsDTO dto,
                                                                 @AuthenticationPrincipal UserEntity currentUser) {

    var data = potsService.create(dto, currentUser);
    return ResponseEntity.ok().body(
        ApiResponse.<PotsResponseDTO>success()
            .message("Pots register successfully")
            .data(data)
            .build());
  }

  @GetMapping
  public ResponseEntity<ApiResponse<List<PotsResponseDTO>>> getPots(@AuthenticationPrincipal UserEntity currentUser) {
    return ResponseEntity.ok(
        ApiResponse.<List<PotsResponseDTO>>success()
            .message("Pots retrieved successfully")
            .data(potsService.findAllPots(currentUser))
            .build()
    );
  }

  @PutMapping("/{id}/balance")
  public ResponseEntity<ApiResponse<PotsResponseDTO>> addMoney(
      @PathVariable Integer id,
      @Valid @RequestBody MoneyRequest moneyRequest,
      @AuthenticationPrincipal UserEntity currentUser) {

    PotsResult result = potsService.addMoney(id, moneyRequest, currentUser);

    if (result instanceof PotsSuccess success) {
      return ResponseEntity.ok(
          ApiResponse.<PotsResponseDTO>success()
              .message("Money added successfully!")
              .data(success.responseDTO())
              .build()
      );
    }

    PotsFailure failure = (PotsFailure) result;

    HttpStatus status = switch (failure.type()) {
      case NOT_FOUND -> HttpStatus.NOT_FOUND;
      case BAD_REQUEST -> HttpStatus.BAD_REQUEST;
      case FORBIDDEN -> HttpStatus.FORBIDDEN;
    };

    var body = ApiResponse.<PotsResponseDTO>failure()
        .message(failure.message())
        .errors(new CustomFieldError(failure.field(), failure.message()))
        .build();

    return ResponseEntity.status(status).body(body);
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Integer id,
                                                  @AuthenticationPrincipal UserEntity currentUser) {

    potsService.delete(id, currentUser);
    return ResponseEntity.ok().body(
        ApiResponse.<Void>success()
            .message("Pot deleted with successfully")
            .build()
    );
  }

}
