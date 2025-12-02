package fun.trackmoney.testutils;

import fun.trackmoney.category.entity.CategoryEntity;
import fun.trackmoney.enums.Frequency;
import fun.trackmoney.enums.TransactionType;
import fun.trackmoney.recurring.dtos.RecurringResponse;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class RecurringResponseFactory {

    public static RecurringResponse defaultResponse() {
        return new RecurringResponse(
                1L,
                Frequency.MONTHLY,
                CategoryEntityFactory.defaultCategory(),
                TransactionType.INCOME,
                LocalDateTime.now().plusDays(1),
                null,
                BigDecimal.valueOf(150.75),
                "Assinatura mensal",
                "Netflix"
        );
    }

    public static RecurringResponse customResponse(
            Long id,
            Frequency frequency,
            CategoryEntity category,
            TransactionType transactionType,
            LocalDateTime nextDate,
            LocalDateTime lastDate,
            BigDecimal amount,
            String description,
            String transactionName
    ) {
        return new RecurringResponse(
                id,
                frequency,
                category,
                transactionType,
                nextDate,
                lastDate,
                amount,
                description,
                transactionName
        );
    }
}
