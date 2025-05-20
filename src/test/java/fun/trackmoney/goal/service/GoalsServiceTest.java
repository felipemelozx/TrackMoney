package fun.trackmoney.goal.service;

import fun.trackmoney.account.dtos.AccountResponseDTO;
import fun.trackmoney.account.entity.AccountEntity;
import fun.trackmoney.account.mapper.AccountMapper;
import fun.trackmoney.account.service.AccountService;
import fun.trackmoney.goal.dtos.CreateGoalsDTO;
import fun.trackmoney.goal.dtos.GoalsResponseDTO;
import fun.trackmoney.goal.entity.GoalsEntity;
import fun.trackmoney.goal.exception.GoalsNotFoundException;
import fun.trackmoney.goal.mapper.GoalsMapper;
import fun.trackmoney.goal.repository.GoalsRepository;
import fun.trackmoney.user.dtos.UserResponseDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class GoalsServiceTest {

  @Mock
  private GoalsRepository goalsRepository;
  @Mock
  private AccountService accountService;
  @Mock
  private AccountMapper accountMapper;
  @Mock
  private GoalsMapper goalsMapper;
  @InjectMocks
  private GoalsService goalsService;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  void createGoals_shouldSaveAndReturnResponse() {
    CreateGoalsDTO dto = new CreateGoalsDTO("Viagem", 1, new BigDecimal("1000"), new BigDecimal("100"));
    GoalsEntity goalsEntity = new GoalsEntity();
    GoalsEntity savedGoals = new GoalsEntity();
    AccountEntity accountEntity = new AccountEntity();

    UserResponseDTO userDTO = new UserResponseDTO(UUID.randomUUID(), "user@email.com", "User");
    AccountResponseDTO accountDTO = new AccountResponseDTO(1, userDTO, "Conta 1", new BigDecimal("5000"), true);

    GoalsResponseDTO responseDTO = new GoalsResponseDTO(
        1,
        "Viagem",
        accountDTO,
        new BigDecimal("1000"),
        new BigDecimal("100"),
        10
    );

    when(goalsMapper.toEntity(dto)).thenReturn(goalsEntity);
    when(accountService.findAccountById(dto.accountId())).thenReturn(accountDTO);
    when(accountMapper.accountResponseToEntity(accountDTO)).thenReturn(accountEntity);
    when(goalsRepository.save(goalsEntity)).thenReturn(savedGoals);
    when(goalsMapper.toResponseDTO(savedGoals)).thenReturn(responseDTO);

    GoalsResponseDTO result = goalsService.createGoals(dto);

    assertThat(result).isEqualTo(responseDTO);
    assertThat(goalsEntity.getAccount()).isEqualTo(accountEntity);
  }

  @Test
  void findAllGoals_shouldReturnListOfResponses() {
    GoalsEntity g1 = new GoalsEntity();
    GoalsEntity g2 = new GoalsEntity();
    List<GoalsEntity> entities = List.of(g1, g2);

    UserResponseDTO userDTO = new UserResponseDTO(UUID.randomUUID(), "user@email.com", "User");

    AccountResponseDTO acc = new AccountResponseDTO(1, userDTO, "Conta", new BigDecimal("2000"), true);

    List<GoalsResponseDTO> dtoList = List.of(
        new GoalsResponseDTO(1, "Meta1", acc, new BigDecimal("1000"), new BigDecimal("500"), 50),
        new GoalsResponseDTO(2, "Meta2", acc, new BigDecimal("2000"), new BigDecimal("1000"), 50)
    );

    when(goalsRepository.findAll()).thenReturn(entities);
    when(goalsMapper.toListResponseDTO(entities)).thenReturn(dtoList);

    List<GoalsResponseDTO> result = goalsService.findAllGoals();

    assertThat(result)
        .hasSize(2)
        .isEqualTo(dtoList);
  }

  @Test
  void findById_shouldReturnGoalResponse_whenExists() {
    GoalsEntity entity = new GoalsEntity();
    UserResponseDTO user = new UserResponseDTO(UUID.randomUUID(), "Usuário", "mail@mail.com");
    AccountResponseDTO acc = new AccountResponseDTO(1, user, "Conta", new BigDecimal("2000"), true);
    GoalsResponseDTO dto = new GoalsResponseDTO(1, "Meta", acc, new BigDecimal("1000"), new BigDecimal("500"), 50);

    when(goalsRepository.findById(1)).thenReturn(Optional.of(entity));
    when(goalsMapper.toResponseDTO(entity)).thenReturn(dto);

    GoalsResponseDTO result = goalsService.findById(1);

    assertThat(result).isEqualTo(dto);
  }

  @Test
  void findById_shouldThrowException_whenNotFound() {
    when(goalsRepository.findById(1)).thenReturn(Optional.empty());

    assertThrows(GoalsNotFoundException.class, () -> goalsService.findById(1));
  }

  @Test
  void update_shouldUpdateAndReturnGoalResponse_whenFound() {
    CreateGoalsDTO dto = new CreateGoalsDTO("Nova Meta", 1, new BigDecimal("3000"), new BigDecimal("1000"));
    GoalsEntity entity = new GoalsEntity();
    GoalsEntity saved = new GoalsEntity();

    UserResponseDTO user = new UserResponseDTO(UUID.randomUUID(), "Usuário","mail@mail.com");
    AccountResponseDTO acc = new AccountResponseDTO(1, user, "Conta", new BigDecimal("2000"), true);
    GoalsResponseDTO responseDTO = new GoalsResponseDTO(1, "Nova Meta", acc, new BigDecimal("3000"), new BigDecimal("1000"), 33);

    when(goalsRepository.findById(1)).thenReturn(Optional.of(entity));
    when(goalsRepository.save(entity)).thenReturn(saved);
    when(goalsMapper.toResponseDTO(saved)).thenReturn(responseDTO);

    GoalsResponseDTO result = goalsService.update(1, dto);

    assertThat(result).isEqualTo(responseDTO);
    assertThat(entity.getGoal()).isEqualTo(dto.goal());
    assertThat(entity.getCurrentAmount()).isEqualTo(dto.currentAmount());
    assertThat(entity.getTargetAmount()).isEqualTo(dto.targetAmount());
  }

  @Test
  void update_shouldThrowException_whenNotFound() {
    CreateGoalsDTO dto = new CreateGoalsDTO("Meta", 1, new BigDecimal("1000"), new BigDecimal("100"));

    when(goalsRepository.findById(1)).thenReturn(Optional.empty());

    assertThrows(GoalsNotFoundException.class, () -> goalsService.update(1, dto));
  }

  @Test
  void deleteById_shouldCallRepositoryDelete() {
    goalsService.deleteById(1);
    verify(goalsRepository).deleteById(1);
  }
}
