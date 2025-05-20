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
        new ApiResponse<>(true, "Goals Created!", goalsService.createGoals(dto), null));
  }

  @GetMapping
  public ResponseEntity<ApiResponse<List<GoalsResponseDTO>>> findAll(){
    return ResponseEntity.status(HttpStatus.OK).body(
        new ApiResponse<>(true, "All goals", goalsService.findAllGoals(), null));
  }

  @GetMapping("/{id}")
  public ResponseEntity<ApiResponse<GoalsResponseDTO>> findById(@PathVariable Integer id){
    return ResponseEntity.status(HttpStatus.OK).body(
        new ApiResponse<>(true, "Get goal", goalsService.findById(id), null));
  }

  @PutMapping("/{id}")
  public ResponseEntity<ApiResponse<GoalsResponseDTO>> update(@PathVariable Integer id,
                                                                @RequestBody CreateGoalsDTO dto){
    return ResponseEntity.status(HttpStatus.OK).body(
        new ApiResponse<>(true, "Update goals", goalsService.update(id, dto), null));
  }


  @DeleteMapping("/{id}")
  public ResponseEntity<ApiResponse<String>> delete(@PathVariable Integer id){
    goalsService.deleteById(id);
    return ResponseEntity.status(HttpStatus.OK).body(
        new ApiResponse<>(true, "deleted goals","Goals deleted", null));
  }
}
