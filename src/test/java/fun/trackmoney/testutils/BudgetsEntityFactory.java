package fun.trackmoney.testutils;

import fun.trackmoney.account.entity.AccountEntity;
import fun.trackmoney.budget.entity.BudgetsEntity;
import fun.trackmoney.category.entity.CategoryEntity;

public class BudgetsEntityFactory {

    public static BudgetsEntity defaultBudget() {
        return new BudgetsEntity(
            1,
            defaultCategory(),
            AccountEntityFactory.defaultAccount(),
            (short) 50
        );
    }

    public static BudgetsEntity secondaryBudget() {
        return new BudgetsEntity(
            2,
            customCategory(2, "Lazer"),
            AccountEntityFactory.savingsAccount(),
            (short) 30
        );
    }

    public static BudgetsEntity customBudget(
            Integer budgetId,
            CategoryEntity category,
            AccountEntity account,
            short percent
    ) {
        return new BudgetsEntity(budgetId, category, account, percent);
    }

    private static CategoryEntity defaultCategory() {
        return customCategory(1, "Alimentação");
    }

    private static CategoryEntity customCategory(Integer id, String name) {
        CategoryEntity category = new CategoryEntity();
        category.setCategoryId(id);
        category.setName(name);
        return category;
    }
}
