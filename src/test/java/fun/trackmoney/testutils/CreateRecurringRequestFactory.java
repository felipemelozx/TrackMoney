package fun.trackmoney.testutils;

import fun.trackmoney.enums.Frequency;
import fun.trackmoney.enums.TransactionType;
import fun.trackmoney.recurring.dtos.CreateRecurringRequest;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class CreateRecurringRequestFactory {

    public static CreateRecurringRequest defaultRequest() {
        return new CreateRecurringRequest(
                Frequency.MONTHLY,
                1,
                TransactionType.INCOME,
                LocalDateTime.now().plusDays(1),
                BigDecimal.valueOf(150.75),
                "Descrição padrão",
                "Transação Padrão"
        );
    }

    public static CreateRecurringRequest customRequest(
            Frequency frequency,
            Integer categoryId,
            TransactionType transactionType,
            LocalDateTime recurrenceDay,
            BigDecimal amount,
            String description,
            String transactionName
    ) {
        return new CreateRecurringRequest(
                frequency,
                categoryId,
                transactionType,
                recurrenceDay,
                amount,
                description,
                transactionName
        );
    }
}
