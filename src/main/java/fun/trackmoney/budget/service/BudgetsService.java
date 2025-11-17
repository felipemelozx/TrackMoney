package fun.trackmoney.budget.service;

import fun.trackmoney.budget.dtos.BudgetCreateDTO;
import fun.trackmoney.budget.dtos.BudgetResponseDTO;
import fun.trackmoney.budget.dtos.internal.BudgetFailure;
import fun.trackmoney.budget.dtos.internal.BudgetResult;
import fun.trackmoney.budget.dtos.internal.BudgetSuccess;
import fun.trackmoney.budget.entity.BudgetsEntity;
import fun.trackmoney.budget.enums.BudgetError;
import fun.trackmoney.budget.mapper.BudgetMapper;
import fun.trackmoney.budget.repository.BudgetsRepository;
import fun.trackmoney.category.service.CategoryService;
import fun.trackmoney.transaction.entity.TransactionEntity;
import fun.trackmoney.transaction.repository.TransactionRepository;
import fun.trackmoney.user.entity.UserEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class BudgetsService {

  private final BudgetsRepository budgetsRepository;
  private final BudgetMapper budgetMapper;
  private final CategoryService categoryService;
  private final TransactionRepository transactionRepository;

  public BudgetsService(BudgetsRepository budgetsRepository,
                        BudgetMapper budgetMapper,
                        CategoryService categoryService, TransactionRepository transactionRepository) {
    this.budgetsRepository = budgetsRepository;
    this.budgetMapper = budgetMapper;
    this.categoryService = categoryService;
    this.transactionRepository = transactionRepository;
  }

  @Transactional
  public BudgetResult create(BudgetCreateDTO dto, UserEntity currentUser) {
    var account = currentUser.getAccount();
    var category = categoryService.findById(dto.categoryId());

    if (category == null) {
      String message = "Category not found with this id: " + dto.categoryId();
      return new BudgetFailure(BudgetError.CATEGORY_NOT_FOUND, message);
    }

    var check = budgetsRepository.checkBudget(account, category);

    if (check.getCategoryExists()) {
      return new BudgetFailure(BudgetError.EXIST_BUDGET, "Já existe um budget para esta categoria.");
    }

    if (check.getTotalPercent() + dto.percent() > 100) {
      return new BudgetFailure(BudgetError.PERCENT_LIMIT_EXCEEDED, "A soma das porcentagens ultrapassa 100%. Soma atual: "
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

  public List<BudgetResponseDTO> findAllBudgets(UserEntity currentUser) {
    Integer accountId = currentUser.getAccount().getAccountId();
    var budgets = budgetsRepository.findAllByAccountAccountId(accountId);

    var allTransactions = transactionRepository.findAllByAccountId(accountId);
    Map<String, List<TransactionEntity>> categoryMap = new HashMap<>();

    allTransactions.stream().forEach((transaction) -> {
      if (categoryMap.containsKey(transaction.getCategory())) {
        categoryMap.get(transaction.getCategory().getName()).add(transaction);
      } else {
        categoryMap.put(transaction.getCategory().getName(), List.of(transaction));
      }
    });

    return budgetMapper.entityListToResponseList(budgets);
  }

  public BudgetResponseDTO findById(Integer id, UserEntity currentUser) {
    var budget = budgetsRepository.findByBudgetIdAndAccount(id, currentUser.getAccount()).orElse(null);
    if (budget == null) {
      return null;
    }
    return budgetMapper.entityToResponseDTO(budget);
  }

  @Transactional
  public BudgetResult update(BudgetCreateDTO dto, Integer id, UserEntity currentUser) {

    var category = categoryService.findById(dto.categoryId());
    if (category == null) {
      return new BudgetFailure(
          BudgetError.CATEGORY_NOT_FOUND,
          "The category with this id: " + dto.categoryId() + " was not found."
      );
    }

    var account = currentUser.getAccount();
    var budget = budgetsRepository.findByBudgetIdAndAccount(id, account).orElse(null);


    if (budget == null) {
      return new BudgetFailure(
          BudgetError.BUDGET_NOT_FOUND,
          "The budget with this id: " + id + " was not found."
      );
    }

    int totalWithoutCurrent = budgetsRepository.getTotalPercentExcludingId(account, id);
    int newTotal = totalWithoutCurrent + dto.percent();

    if (newTotal > 100) {
      int maxAllowed = 100 - totalWithoutCurrent;
      return new BudgetFailure(
          BudgetError.PERCENT_LIMIT_EXCEEDED,
          "The new percent exceeds the limit of 100%. The max value allowed is: " + maxAllowed + "%"
      );
    }

    budget.setCategory(category);
    budget.setPercent(dto.percent());
    var budgetUpdated = budgetsRepository.save(budget);
    var budgetResponseDTO = budgetMapper.entityToResponseDTO(budgetUpdated);

    return new BudgetSuccess(budgetResponseDTO);
  }

  @Transactional
  public void deleteById(Integer id, UserEntity currentUser) {
    budgetsRepository.deleteByBudgetIdAndAccount(id, currentUser.getAccount());
  }
}
