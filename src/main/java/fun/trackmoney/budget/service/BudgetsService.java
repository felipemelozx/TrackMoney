package fun.trackmoney.budget.service;

import fun.trackmoney.account.mapper.AccountMapper;
import fun.trackmoney.budget.dtos.BudgetCreateDTO;
import fun.trackmoney.budget.dtos.BudgetResponseDTO;
import fun.trackmoney.budget.dtos.internal.BudgetFailure;
import fun.trackmoney.budget.dtos.internal.BudgetResult;
import fun.trackmoney.budget.dtos.internal.BudgetSuccess;
import fun.trackmoney.budget.entity.BudgetsEntity;
import fun.trackmoney.budget.enums.BudgetError;
import fun.trackmoney.budget.mapper.BudgetMapper;
import fun.trackmoney.budget.repository.BudgetsRepository;
import fun.trackmoney.category.entity.CategoryEntity;
import fun.trackmoney.category.service.CategoryService;
import fun.trackmoney.transaction.entity.TransactionEntity;
import fun.trackmoney.transaction.service.TransactionService;
import fun.trackmoney.user.entity.UserEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class BudgetsService {

  private final BudgetsRepository budgetsRepository;
  private final BudgetMapper budgetMapper;
  private final CategoryService categoryService;
  private final TransactionService transactionService;
  private final AccountMapper accountMapper;

  public BudgetsService(BudgetsRepository budgetsRepository,
                        BudgetMapper budgetMapper,
                        CategoryService categoryService,
                        TransactionService transactionService, AccountMapper accountMapper) {
    this.budgetsRepository = budgetsRepository;
    this.budgetMapper = budgetMapper;
    this.categoryService = categoryService;
    this.transactionService = transactionService;
    this.accountMapper = accountMapper;
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
      return new BudgetFailure(BudgetError.PERCENT_LIMIT_EXCEEDED,
          "A soma das porcentagens ultrapassa 100%. Soma atual: "
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

    var allTransactions = transactionService.getLast5TransactionsPerCategory(accountId);
    List<BudgetResponseDTO> budgetDTOS = new ArrayList<>();
    List<TransactionEntity> currentMonthTransaction = transactionService.getCurrentMonthTransactions();

    int income = transactionService.getIncome(currentUser.getUserId()).intValue();

    for(BudgetsEntity budget : budgets) {
      CategoryEntity category = budget.getCategory();
      int currentAmount = getCurrentAmountWested(category, currentMonthTransaction);
      var categoryTransactionLast5 = allTransactions.get(category);
      int targetAmount = income * budget.getPercent() / 100;

      var accountDto = accountMapper.accountEntityToAccountResponse(currentUser.getAccount());

      var budgetDto = new BudgetResponseDTO(
          budget.getBudgetId(),
          category,
          accountDto,
          budget.getPercent(),
          BigDecimal.valueOf(targetAmount),
          BigDecimal.valueOf(currentAmount),
          categoryTransactionLast5
          );
      budgetDTOS.add(budgetDto);
    }

    return budgetDTOS;
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


  public int getCurrentAmountWested(CategoryEntity category, List<TransactionEntity> transactions) {
    int sum = 0;
    for(TransactionEntity transaction : transactions) {
      if(transaction.getCategory().equals(category)) {
        sum += transaction.getAmount().intValue();
      }
    }
    return sum;
  }
}
