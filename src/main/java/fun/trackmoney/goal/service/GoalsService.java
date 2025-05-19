package fun.trackmoney.goal.service;

import fun.trackmoney.account.entity.AccountEntity;
import fun.trackmoney.account.mapper.AccountMapper;
import fun.trackmoney.account.service.AccountService;
import fun.trackmoney.goal.dtos.CreateGoalsDTO;
import fun.trackmoney.goal.dtos.GoalsResponseDTO;
import fun.trackmoney.goal.entity.GoalsEntity;
import fun.trackmoney.goal.mapper.GoalsMapper;
import fun.trackmoney.goal.repository.GoalsRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GoalsService {

  private final GoalsRepository goalsRepository;
  private final AccountService accountService;
  private final AccountMapper accountMapper;
  private final GoalsMapper goalsMapper;

  public GoalsService(GoalsRepository goalsRepository, AccountService accountService, AccountMapper accountMapper, GoalsMapper goalsMapper) {
    this.goalsRepository = goalsRepository;
    this.accountService = accountService;
    this.accountMapper = accountMapper;
    this.goalsMapper = goalsMapper;
  }

  public GoalsResponseDTO createGoals(CreateGoalsDTO dto){
    GoalsEntity goals = goalsMapper.toEntity(dto);
    AccountEntity account = accountMapper.accountResponseToEntity(accountService.findAccountById(dto.accountId()));
    goals.setAccount(account);
    return goalsMapper.toResponseDTO(goalsRepository.save(goals));
  }

  public List<GoalsResponseDTO> findAllGoals(){
    List<GoalsEntity> list = goalsRepository.findAll();
    return goalsMapper.toListResponseDTO(list);
  }

  public GoalsResponseDTO findById(Integer id) {
    return goalsMapper.toResponseDTO(goalsRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("Goals not found!")));
  }

  public GoalsResponseDTO update(Integer id, CreateGoalsDTO dto) {
    GoalsEntity goals =  goalsRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("Goals not found!"));
    goals.setGoal(dto.goal());
    goals.setCurrentAmount(dto.currentAmount());
    goals.setTargetAmount(dto.targetAmount());
    return goalsMapper.toResponseDTO(goalsRepository.save(goals));
  }

  public void deleteById(Integer id) {
    goalsRepository.deleteById(id);
  }
}
