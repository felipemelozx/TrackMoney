package fun.trackmoney.pots.controller;

import fun.trackmoney.pots.dtos.CreatePotsDTO;
import fun.trackmoney.pots.dtos.PotsResponseDTO;
import fun.trackmoney.pots.service.PotsService;
import fun.trackmoney.utils.response.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
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

  @GetMapping("/{id}")
  public ResponseEntity<ApiResponse<List<PotsResponseDTO>>> getPots(@PathVariable Integer id) {
    return ResponseEntity.ok(
        ApiResponse.<List<PotsResponseDTO>>success()
            .message("Pots retrieved successfully")
            .data(potsService.findAllPots(id))
            .build()
    );
  }

  @PostMapping
  public ResponseEntity<ApiResponse<PotsResponseDTO>> createPots(@RequestBody CreatePotsDTO dto) {
    return ResponseEntity.ok(
        ApiResponse.<PotsResponseDTO>success()
            .message("Pots register successfully")
            .data(potsService.create(dto))
            .build()
    );
  }
}
