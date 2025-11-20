package fun.trackmoney.pots.controller;

import fun.trackmoney.pots.dtos.CreatePotsDTO;
import fun.trackmoney.pots.dtos.PotsResponseDTO;
import fun.trackmoney.pots.service.PotsService;
import fun.trackmoney.user.entity.UserEntity;
import fun.trackmoney.utils.response.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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


}
