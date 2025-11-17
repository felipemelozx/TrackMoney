package fun.trackmoney.testutils;

import fun.trackmoney.enums.TransactionType;
import fun.trackmoney.transaction.dto.TransactionUpdateDTO;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class TransactionUpdateDTOFactory {

    public static TransactionUpdateDTO defaultUpdateTransaction() {
        return new TransactionUpdateDTO(
            "Updated Transaction Name",
            "Updated transaction description",
            new BigDecimal("199.99"),
            1,
            TransactionType.EXPENSE,
            LocalDateTime.now()
        );
    }

  public static TransactionUpdateDTO defaultUpdateTransactionIncome() {
    return new TransactionUpdateDTO(
        "Updated Transaction Name",
        "Updated transaction description",
        new BigDecimal("199.99"),
        1,
        TransactionType.EXPENSE,
        LocalDateTime.now()
    );
  }
}
