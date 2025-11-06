package fun.trackmoney.testutils;

import fun.trackmoney.account.dtos.AccountResponseDTO;
import fun.trackmoney.category.entity.CategoryEntity;
import fun.trackmoney.enums.TransactionType;
import fun.trackmoney.transaction.dto.TransactionResponseDTO;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class TransactionResponseDTOFactory {
    public static TransactionResponseDTO defaultTransactionResponse() {
        return new TransactionResponseDTO(
            1,
            "Some Name",
            "buy bread",
            BigDecimal.valueOf(120.50),
            AccountResponseDTOFactory.defaultAccountResponse(),
            TransactionType.EXPENSE,
            CategoryEntityFactory.defaultCategory(),
            LocalDate.now().atStartOfDay()
        );
    }

    public static TransactionResponseDTO incomeTransactionResponse() {
        return new TransactionResponseDTO(
            2,
            "some name",
            "Salário mensal",
            BigDecimal.valueOf(3000.00),
            AccountResponseDTOFactory.defaultAccountResponse(),
            TransactionType.EXPENSE,
            CategoryEntityFactory.defaultCategory(),
             LocalDate.now().atStartOfDay()
        );
    }

    public static TransactionResponseDTO customTransactionResponse(
            Integer transactionId,
            String description,
            BigDecimal amount,
            AccountResponseDTO account,
            TransactionType type,
            CategoryEntity category,
            LocalDateTime transactionDate
    ) {
        return new TransactionResponseDTO(transactionId,"Some Name", description, amount, account, type, category, transactionDate);
    }
}
