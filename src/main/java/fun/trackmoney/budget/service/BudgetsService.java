package fun.trackmoney.budget.service;

import fun.trackmoney.account.mapper.AccountMapper;
import fun.trackmoney.account.service.AccountService;
import fun.trackmoney.budget.dtos.BudgetCreateDTO;
import fun.trackmoney.budget.dtos.BudgetResponseDTO;
import fun.trackmoney.budget.entity.BudgetsEntity;
import fun.trackmoney.budget.exception.BudgetsNotFoundException;
import fun.trackmoney.budget.mapper.BudgetMapper;
import fun.trackmoney.budget.repository.BudgetsRepository;
import fun.trackmoney.category.service.CategoryService;
import fun.trackmoney.user.service.UserService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class BudgetsService {

  private final BudgetsRepository budgetsRepository;
  private final BudgetMapper budgetMapper;
  private final AccountService accountService;
  private final AccountMapper accountMapper;
  private final CategoryService categoryService;
  private final UserService userService;

  public BudgetsService(BudgetsRepository budgetsRepository,
                        BudgetMapper budgetMapper,
                        AccountService accountService,
                        AccountMapper accountMapper,
                        CategoryService categoryService,
                        UserService userService) {
    this.budgetsRepository = budgetsRepository;
    this.budgetMapper = budgetMapper;
    this.accountService = accountService;
    this.accountMapper = accountMapper;
    this.categoryService = categoryService;
    this.userService = userService;
  }

  public BudgetResponseDTO create(BudgetCreateDTO dto) {
    BudgetsEntity budgets = budgetMapper.createDtoTOEntity(dto);

    budgets.setUserEntity(userService.findUserById(dto.userId()));
    budgets.setAccount(accountMapper.accountResponseToEntity(accountService.findAccountById(dto.accountId())));
    budgets.setCategory(categoryService.findById(dto.categoryId()));
    return budgetMapper.entityToResponseDTO(budgetsRepository.save(budgets));
  }

  public List<BudgetResponseDTO> findAllByAccountId(Integer accountId) {
    return budgetMapper.entityListToResponseList(
            budgetsRepository.findAllByAccountAccountId(accountId)
        ).stream()
        .map(budget -> {
          BigDecimal currentAmount = budget.currentAmount() == null
              ? BigDecimal.valueOf(100)
              : budget.currentAmount();

          return new BudgetResponseDTO(
              budget.budgetId(),
              budget.category(),
              budget.account(),
              budget.targetAmount(),
              budget.resetDay(),
              currentAmount
          );
        })
        .toList();
  }

  public BudgetResponseDTO findById(Integer id) {
    return budgetMapper.entityToResponseDTO(budgetsRepository.findById(id)
        .orElseThrow(() -> new BudgetsNotFoundException("Budget not found")));
  }

  public BudgetResponseDTO update(BudgetCreateDTO dto, Integer id) {
    budgetsRepository.findById(id)
        .orElseThrow(() -> new BudgetsNotFoundException(("Budget not found")));

    BudgetsEntity budgets = budgetMapper.createDtoTOEntity(dto);
    budgets.setBudgetId(id);
    budgets.setUserEntity(userService.findUserById(dto.userId()));
    budgets.setAccount(accountMapper.accountResponseToEntity(accountService.findAccountById(dto.accountId())));
    budgets.setCategory(categoryService.findById(dto.categoryId()));
    budgets.setTargetAmount(dto.targetAmount());
    budgets.setResetDay(dto.resetDay());

    return budgetMapper.entityToResponseDTO(budgetsRepository.save(budgets));
  }

  public void deleteById(Integer id) {
    budgetsRepository.deleteById(id);
  }
}
