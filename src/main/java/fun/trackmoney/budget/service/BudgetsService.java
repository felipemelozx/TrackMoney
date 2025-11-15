package fun.trackmoney.budget.service;

import fun.trackmoney.account.mapper.AccountMapper;
import fun.trackmoney.account.service.AccountService;
import fun.trackmoney.budget.dtos.BudgetCreateDTO;
import fun.trackmoney.budget.dtos.BudgetResponseDTO;
import fun.trackmoney.budget.dtos.internal.BudgetFailure;
import fun.trackmoney.budget.dtos.internal.BudgetResult;
import fun.trackmoney.budget.dtos.internal.BudgetSuccess;
import fun.trackmoney.budget.entity.BudgetsEntity;
import fun.trackmoney.budget.enums.BudgetError;
import fun.trackmoney.budget.exception.BudgetsNotFoundException;
import fun.trackmoney.budget.mapper.BudgetMapper;
import fun.trackmoney.budget.repository.BudgetsRepository;
import fun.trackmoney.category.service.CategoryService;
import fun.trackmoney.user.entity.UserEntity;
import fun.trackmoney.user.service.UserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.Set;

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

  @Transactional
  public BudgetResult create(BudgetCreateDTO dto, UserEntity currentUser) {
    var account = currentUser.getAccount();
    var category = categoryService.findById(dto.categoryId());

    if(category == null)  {
      String message = "Category not foudn with this id: " + dto.categoryId();
      return new BudgetFailure(BudgetError.CATEGORY_NOT_FOUND, message);
    }

    var check = budgetsRepository.checkBudget(account, category);

    if (check.getCategoryExists()) {
      return new BudgetFailure(BudgetError.EXIST_BUDGET, "Já existe um budget para esta categoria.");
    }

    if (check.getTotalPercent() + dto.percent() > 100) {
     return new BudgetFailure(BudgetError.PERCENT_LIMIT_EXCEEDED,"A soma das porcentagens ultrapassa 100%. Soma atual: "
              + check.getTotalPercent() + "%, valor disponível: " + (100 - check.getTotalPercent())
      );
    }

    var budget = new BudgetsEntity()
        .setAccount(account)
        .setCategory(category)
        .setPercent(dto.percent());
    var data = budgetMapper.entityToResponseDTO(budgetsRepository.save(budget));
    return new BudgetSuccess(data);
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
    // TODO: Improve logic to use an interface instead of throwing exceptions
 /*   if(optionalUser.isEmpty()) {
      throw new UserNotFoundException("User not found.");
    }*/
    // budgets.setUserEntity(optionalUser.get());
    //budgets.setAccount(accountMapper.accountResponseToEntity(accountService.findAccountById(dto.accountId())));
    budgets.setCategory(categoryService.findById(dto.categoryId()));

    return budgetMapper.entityToResponseDTO(budgetsRepository.save(budgets));
  }

  public void deleteById(Integer id) {
    budgetsRepository.deleteById(id);
  }
}
