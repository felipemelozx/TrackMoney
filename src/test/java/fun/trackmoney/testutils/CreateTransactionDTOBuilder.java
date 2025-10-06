package fun.trackmoney.testutils;

import fun.trackmoney.enums.TransactionType;
import fun.trackmoney.transaction.dto.CreateTransactionDTO;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.Instant;

public class CreateTransactionDTOBuilder {
  public static CreateTransactionDTO defaultTransaction() {
    return new CreateTransactionDTO(
        1,
        TransactionType.EXPENSE,
        BigDecimal.valueOf(100.00),
        "Compra no supermercado",
        Timestamp.from(Instant.now())
    );
  }

  public static CreateTransactionDTO incomeTransaction() {
    return new CreateTransactionDTO(
        2,
        TransactionType.INCOME,
        BigDecimal.valueOf(2500.00),
        "Recebimento de salário",
        Timestamp.from(Instant.now())
    );
  }

  public static CreateTransactionDTO customTransaction(
      Integer categoryId,
      TransactionType transactionType,
      BigDecimal amount,
      String description,
      Timestamp transactionDate
  ) {
    return new CreateTransactionDTO(categoryId, transactionType, amount, description, transactionDate);
  }
}
