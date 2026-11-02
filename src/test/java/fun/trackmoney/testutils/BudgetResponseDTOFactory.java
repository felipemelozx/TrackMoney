package fun.trackmoney.testutils;

import fun.trackmoney.dto.account.AccountResponseDTO;
import fun.trackmoney.dto.budget.BudgetResponseDTO;
import fun.trackmoney.entity.CategoryEntity;
import fun.trackmoney.dto.transaction.TransactionResponseDTO;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

public class BudgetResponseDTOFactory {


    public static BudgetResponseDTO defaultResponse() {
        return new BudgetResponseDTO(
            1,
            CategoryEntityFactory.defaultCategory(),
            AccountResponseDTOFactory.defaultAccountResponse(),
            (short) 50,
            BigDecimal.valueOf(1000),
            BigDecimal.valueOf(250),
            Collections.emptyList()
        );
    }

    public static BudgetResponseDTO secondaryResponse() {
        return new BudgetResponseDTO(
            2,
            CategoryEntityFactory.defaultCategory(),
            AccountResponseDTOFactory.defaultAccountResponse(),
            (short) 30,
            BigDecimal.valueOf(500),
            BigDecimal.valueOf(100),
            List.of(
                TransactionResponseDTOFactory.defaultTransactionResponse(),
                TransactionResponseDTOFactory.defaultTransactionResponse()
            )
        );
    }

    public static BudgetResponseDTO customResponse(
            Integer budgetId,
            CategoryEntity category,
            AccountResponseDTO account,
            short percent,
            BigDecimal targetAmount,
            BigDecimal currentAmount,
            List<TransactionResponseDTO> lastTransactions
    ) {
        return new BudgetResponseDTO(
            budgetId,
            category,
            account,
            percent,
            targetAmount,
            currentAmount,
            lastTransactions
        );
    }
}
