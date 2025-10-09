package fun.trackmoney.testutils;

import fun.trackmoney.transaction.entity.TransactionEntity;
import fun.trackmoney.account.entity.AccountEntity;
import fun.trackmoney.category.entity.CategoryEntity;
import fun.trackmoney.enums.TransactionType;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;

public class TransactionEntityFactory {

  public static TransactionEntity defaultExpenseNow() {
    return createTransaction(
        1,
        AccountEntityFactory.defaultAccount(),
        CategoryEntityFactory.defaultCategory(),
        TransactionType.EXPENSE,
        BigDecimal.valueOf(100.00),
        "Default expense now",
        LocalDateTime.now()
    );
  }

  public static TransactionEntity defaultIncomeNow() {
    return createTransaction(
        2,
        AccountEntityFactory.defaultAccount(),
        CategoryEntityFactory.customCategory(3, "Salário", "#00FF00"),
        TransactionType.INCOME,
        BigDecimal.valueOf(2500.00),
        "Income now",
        LocalDateTime.now()
    );
  }

  public static TransactionEntity expenseCurrentMonthAndYear(BigDecimal amount, String description) {
    return createTransaction(
        3,
        AccountEntityFactory.defaultAccount(),
        CategoryEntityFactory.defaultCategory(),
        TransactionType.EXPENSE,
        amount,
        description,
        LocalDateTime.now()
    );
  }

  public static TransactionEntity expenseCurrentMonthWrongYear(BigDecimal amount, String description, Integer codeTransactionType) {
    LocalDateTime now = LocalDateTime.now();
    LocalDateTime wrongYear = now.withYear(now.getYear() - 1); // mesmo mês, ano passado
    return createTransaction(
        4,
        AccountEntityFactory.defaultAccount(),
        CategoryEntityFactory.defaultCategory(),
        TransactionType.getByCode(codeTransactionType),
        amount,
        description,
        wrongYear
    );
  }

  public static TransactionEntity expenseWrongMonthCurrentYear(BigDecimal amount, String description, Integer codeTransactionType) {
    LocalDateTime now = LocalDateTime.now();
    LocalDateTime wrongMonth = now.minusMonths(1); // mês passado, mesmo ano
    return createTransaction(
        5,
        AccountEntityFactory.defaultAccount(),
        CategoryEntityFactory.defaultCategory(),
        TransactionType.getByCode(codeTransactionType),
        amount,
        description,
        wrongMonth
    );
  }

  public static TransactionEntity expenseWrongMonthAndYear(BigDecimal amount, String description, Integer codeTransactionType) {
    LocalDateTime date = LocalDateTime.of(2000, 1, 1, 0, 0);
    return createTransaction(
        6,
        AccountEntityFactory.defaultAccount(),
        CategoryEntityFactory.defaultCategory(),
        TransactionType.getByCode(codeTransactionType),
        amount,
        description,
        date
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

  private static TransactionEntity createTransaction(
      Integer transactionId,
      AccountEntity account,
      CategoryEntity category,
      TransactionType type,
      BigDecimal amount,
      String description,
      LocalDateTime dateTime
  ) {
    Timestamp timestamp = Timestamp.valueOf(dateTime);
    return new TransactionEntity(transactionId, account, category, type, amount, description, timestamp);
  }
}
