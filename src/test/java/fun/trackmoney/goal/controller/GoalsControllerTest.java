package fun.trackmoney.goal.controller;

import fun.trackmoney.account.dtos.AccountResponseDTO;
import fun.trackmoney.goal.dtos.CreateGoalsDTO;
import fun.trackmoney.goal.dtos.GoalsResponseDTO;
import fun.trackmoney.goal.service.GoalsService;
import fun.trackmoney.user.dtos.UserResponseDTO;
import fun.trackmoney.utils.response.ApiResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class GoalsControllerTest {

  private GoalsService goalsService;
  private GoalsController controller;

  private GoalsResponseDTO goalResponse;

  @BeforeEach
  void setUp() {
    goalsService = mock(GoalsService.class);
    controller = new GoalsController(goalsService);

    UserResponseDTO user = new UserResponseDTO(UUID.randomUUID(), "User", "user@email.com");
    AccountResponseDTO account = new AccountResponseDTO(1, user, "Conta 1", new BigDecimal("1000"));

    goalResponse = new GoalsResponseDTO(1, "Viagem", account, new BigDecimal("1000"), new BigDecimal("200"), 20);
  }

  @Test
  void create_shouldReturnCreatedResponse() {
    CreateGoalsDTO dto = new CreateGoalsDTO("Viagem", 1, new BigDecimal("1000"), new BigDecimal("200"));

    when(goalsService.createGoals(dto)).thenReturn(goalResponse);

    var response = controller.create(dto);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    ApiResponse<GoalsResponseDTO> body = response.getBody();
    assertThat(body).isNotNull();
    assertThat(body.isSuccess()).isTrue();
    assertThat(body.getMessage()).isEqualTo("Goals Created!");
    assertThat(body.getData().goal()).isEqualTo("Viagem");

    verify(goalsService).createGoals(dto);
  }

  @Test
  void findAll_shouldReturnAllGoals() {
    when(goalsService.findAllGoals()).thenReturn(List.of(goalResponse));

    var response = controller.findAll();

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    ApiResponse<List<GoalsResponseDTO>> body = response.getBody();
    assertThat(body).isNotNull();
    assertThat(body.isSuccess()).isTrue();
    assertThat(body.getData()).hasSize(1);
    assertThat(body.getData().get(0).goal()).isEqualTo("Viagem");

    verify(goalsService).findAllGoals();
  }

  @Test
  void findById_shouldReturnGoal() {
    when(goalsService.findById(1)).thenReturn(goalResponse);

    var response = controller.findById(1);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    ApiResponse<GoalsResponseDTO> body = response.getBody();
    assertThat(body).isNotNull();
    assertThat(body.isSuccess()).isTrue();
    assertThat(body.getData().goal()).isEqualTo("Viagem");

    verify(goalsService).findById(1);
  }

  @Test
  void update_shouldReturnUpdatedGoal() {
    CreateGoalsDTO dto = new CreateGoalsDTO("Casa", 1, new BigDecimal("3000"), new BigDecimal("500"));
    GoalsResponseDTO updated = new GoalsResponseDTO(1, "Casa", goalResponse.account(), new BigDecimal("3000"), new BigDecimal("500"), 17);

    when(goalsService.update(1, dto)).thenReturn(updated);

    var response = controller.update(1, dto);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    ApiResponse<GoalsResponseDTO> body = response.getBody();
    assertThat(body).isNotNull();
    assertThat(body.isSuccess()).isTrue();
    assertThat(body.getData().goal()).isEqualTo("Casa");

    verify(goalsService).update(1, dto);
  }

  @Test
  void delete_shouldReturnSuccessMessage() {
    doNothing().when(goalsService).deleteById(1);

    var response = controller.delete(1);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    ApiResponse<Void> body = response.getBody();
    assertThat(body).isNotNull();
    assertThat(body.isSuccess()).isTrue();

    verify(goalsService).deleteById(1);
  }
}
