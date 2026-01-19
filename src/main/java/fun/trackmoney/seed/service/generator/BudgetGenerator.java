package fun.trackmoney.seed.service.generator;

import fun.trackmoney.account.entity.AccountEntity;
import fun.trackmoney.budget.entity.BudgetsEntity;
import fun.trackmoney.category.entity.CategoryEntity;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class BudgetGenerator {

  private static final List<String> EXPENSE_CATEGORIES = List.of(
      "Alimentação",
      "Transporte",
      "Moradia",
      "Educação",
      "Saúde",
      "Lazer",
      "Supermercado",
      "Contas (água, luz, internet)"
  );

  private static final short[] BUDGET_PERCENTS = {20, 15, 15, 12, 10, 10, 10, 8};

  public List<BudgetsEntity> generate(AccountEntity account, List<CategoryEntity> categories) {
    List<BudgetsEntity> budgets = new ArrayList<>();

    var expenseCategories = categories.stream()
        .filter(c -> EXPENSE_CATEGORIES.contains(c.getName()))
        .toList();

    for (int i = 0; i < expenseCategories.size() && i < BUDGET_PERCENTS.length; i++) {
      BudgetsEntity budget = new BudgetsEntity();
      budget.setAccount(account);
      budget.setCategory(expenseCategories.get(i));
      budget.setPercent(BUDGET_PERCENTS[i]);
      budgets.add(budget);
    }

    return budgets;
  }
}
