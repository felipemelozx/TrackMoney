package fun.trackmoney.testutils;

import fun.trackmoney.entity.AccountEntity;
import fun.trackmoney.entity.BudgetHistoryEntity;
import fun.trackmoney.entity.BudgetsEntity;
import fun.trackmoney.entity.CategoryEntity;
import fun.trackmoney.enums.BudgetStatus;

import java.math.BigDecimal;

public class BudgetHistoryEntityFactory {

    public static BudgetHistoryEntity defaultBudgetHistory() {
        return new BudgetHistoryEntity()
            .setHistoryId(1)
            .setBudget(defaultBudget())
            .setAccount(AccountEntityFactory.defaultAccount())
            .setCategory(CategoryEntityFactory.defaultCategory())
            .setReferenceMonth((short) 1)
            .setReferenceYear(2025)
            .setPercent((short) 50)
            .setTargetAmount(new BigDecimal("5000.0000"))
            .setSpentAmount(new BigDecimal("2500.0000"))
            .setRemainingAmount(new BigDecimal("2500.0000"))
            .setTotalIncome(new BigDecimal("10000.0000"))
            .setStatus(BudgetStatus.WITHIN_LIMIT);
    }

    public static BudgetHistoryEntity exceededBudgetHistory() {
        return new BudgetHistoryEntity()
            .setHistoryId(2)
            .setBudget(defaultBudget())
            .setAccount(AccountEntityFactory.defaultAccount())
            .setCategory(CategoryEntityFactory.defaultCategory())
            .setReferenceMonth((short) 2)
            .setReferenceYear(2025)
            .setPercent((short) 50)
            .setTargetAmount(new BigDecimal("5000.0000"))
            .setSpentAmount(new BigDecimal("6000.0000"))
            .setRemainingAmount(new BigDecimal("-1000.0000"))
            .setTotalIncome(new BigDecimal("10000.0000"))
            .setStatus(BudgetStatus.EXCEEDED);
    }

    private static BudgetsEntity defaultBudget() {
        return BudgetsEntityFactory.defaultBudget();
    }
}
