package fun.trackmoney.testutils;

import fun.trackmoney.account.entity.AccountEntity;
import fun.trackmoney.category.entity.CategoryEntity;
import fun.trackmoney.enums.Frequency;
import fun.trackmoney.enums.TransactionType;
import fun.trackmoney.recurring.entity.RecurringEntity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class RecurringEntityFactory {

    public static RecurringEntity defaultEntity() {
        return new RecurringEntity(
                1L,
                Frequency.MONTHLY,
                LocalDateTime.now().plusDays(1),
                null,
                AccountEntityFactory.defaultAccount(),
                CategoryEntityFactory.defaultCategory(),
                TransactionType.INCOME,
                BigDecimal.valueOf(150.75),
                "Assinatura mensal",
                "Netflix"
        );
    }

    public static RecurringEntity customEntity(
            Long id,
            Frequency frequency,
            LocalDateTime nextDate,
            LocalDateTime lastDate,
            AccountEntity account,
            CategoryEntity category,
            TransactionType transactionType,
            BigDecimal amount,
            String description,
            String transactionName
    ) {
        return new RecurringEntity(
                id,
                frequency,
                nextDate,
                lastDate,
                account,
                category,
                transactionType,
                amount,
                description,
                transactionName
        );
    }
}
