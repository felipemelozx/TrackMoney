package fun.trackmoney.testutils;

import fun.trackmoney.transaction.entity.TransactionEntity;
import fun.trackmoney.account.entity.AccountEntity;
import fun.trackmoney.category.entity.CategoryEntity;
import fun.trackmoney.enums.TransactionType;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.Instant;

public class TransactionEntityFactory {

    public static TransactionEntity defaultTransaction() {
        return new TransactionEntity(
            1,
            AccountEntityFactory.defaultAccount(),
            CategoryEntityFactory.defaultCategory(),
            TransactionType.EXPENSE,
            BigDecimal.valueOf(100.00),
            "Buy bread",
            Timestamp.from(Instant.now())
        );
    }

    public static TransactionEntity incomeTransaction() {
        return new TransactionEntity(
            2,
            AccountEntityFactory.defaultAccount(),
            CategoryEntityFactory.customCategory(3, "Salário", "#00FF00"),
            TransactionType.INCOME,
            BigDecimal.valueOf(2500.00),
            "Recebimento de salário",
            Timestamp.from(Instant.now())
        );
    }

    public static TransactionEntity customTransaction(
            Integer transactionId,
            AccountEntity account,
            CategoryEntity category,
            TransactionType transactionType,
            BigDecimal amount,
            String description,
            Timestamp transactionDate
    ) {
        return new TransactionEntity(transactionId, account, category, transactionType, amount, description, transactionDate);
    }
}
