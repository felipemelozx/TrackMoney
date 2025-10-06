package fun.trackmoney.testutils;

import fun.trackmoney.account.dtos.AccountResponseDTO;
import fun.trackmoney.transaction.dto.TransactionResponseDTO;
import fun.trackmoney.user.dtos.UserResponseDTO;

import java.math.BigDecimal;

public class TransactionResponseDTOFactory {

    public static TransactionResponseDTO defaultTransactionResponse() {
        return new TransactionResponseDTO(
            1,
            "buy bread",
            BigDecimal.valueOf(120.50),
            AccountResponseDTOFactory.defaultAccountResponse()
        );
    }

    public static TransactionResponseDTO incomeTransactionResponse() {
        return new TransactionResponseDTO(
            2,
            "Salário mensal",
            BigDecimal.valueOf(3000.00),
            AccountResponseDTOFactory.defaultAccountResponse()
        );
    }

    public static TransactionResponseDTO customTransactionResponse(
            Integer transactionId,
            String description,
            BigDecimal amount,
            AccountResponseDTO account
    ) {
        return new TransactionResponseDTO(transactionId, description, amount, account);
    }
}
