package fun.trackmoney.goal.controller;

import fun.trackmoney.goal.dtos.CreateGoalsDTO;
import fun.trackmoney.goal.dtos.GoalsResponseDTO;
import fun.trackmoney.goal.service.GoalsService;
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
@RequestMapping("goals")
public class GoalsController {

  private final GoalsService goalsService;

  public GoalsController(GoalsService goalsService) {
    this.goalsService = goalsService;
  }

  @PostMapping
  public ResponseEntity<ApiResponse<GoalsResponseDTO>> create(@RequestBody CreateGoalsDTO dto){
    return ResponseEntity.status(HttpStatus.CREATED).body(
      ApiResponse.<GoalsResponseDTO>success()
          .message("Goals Created!")
          .data(goalsService.createGoals(dto))
          .build()
    );
  }

  @GetMapping
  public ResponseEntity<ApiResponse<List<GoalsResponseDTO>>> findAll(){
    return ResponseEntity.status(HttpStatus.OK).body(
      ApiResponse.<List<GoalsResponseDTO>>success()
          .message("All goals")
          .data(goalsService.findAllGoals())
          .build()
    );
  }

  @GetMapping("/{id}")
  public ResponseEntity<ApiResponse<GoalsResponseDTO>> findById(@PathVariable Integer id){
    return ResponseEntity.status(HttpStatus.OK).body(
      ApiResponse.<GoalsResponseDTO>success()
          .message("Get goal")
          .data(goalsService.findById(id))
          .build()
    );
  }

  @PutMapping("/{id}")
  public ResponseEntity<ApiResponse<GoalsResponseDTO>> update(@PathVariable Integer id,
                                                                @RequestBody CreateGoalsDTO dto){
    return ResponseEntity.status(HttpStatus.OK).body(
      ApiResponse.<GoalsResponseDTO>success()
          .message("Update goals")
          .data(goalsService.update(id, dto))
          .build()
    );
  }


  @DeleteMapping("/{id}")
  public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Integer id){
    goalsService.deleteById(id);
    return ResponseEntity.status(HttpStatus.OK).body(
      ApiResponse.<Void>success()
          .message("Deleted goals")
          .build()
    );
  }
}
