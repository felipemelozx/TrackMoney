package fun.trackmoney.testutils;

import fun.trackmoney.account.dtos.AccountResponseDTO;
import fun.trackmoney.category.entity.CategoryEntity;
import fun.trackmoney.enums.TransactionType;
import fun.trackmoney.transaction.dto.TransactionResponseDTO;
import fun.trackmoney.user.dtos.UserResponseDTO;

import java.math.BigDecimal;

public class TransactionResponseDTOFactory {

    public static TransactionResponseDTO defaultTransactionResponse() {
        return new TransactionResponseDTO(
            1,
            "Some Name",
            "buy bread",
            BigDecimal.valueOf(120.50),
            AccountResponseDTOFactory.defaultAccountResponse(),
            TransactionType.EXPENSE,
            CategoryEntityFactory.defaultCategory()
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
            CategoryEntityFactory.defaultCategory()
        );
    }

    public static TransactionResponseDTO customTransactionResponse(
            Integer transactionId,
            String description,
            BigDecimal amount,
            AccountResponseDTO account,
            TransactionType type,
            CategoryEntity category
    ) {
        return new TransactionResponseDTO(transactionId,"Some Name", description, amount, account, type, category);
    }
}
