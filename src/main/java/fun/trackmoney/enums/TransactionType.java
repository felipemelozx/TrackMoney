package fun.trackmoney.enums;

public enum TransactionType {
  INCOME(0, "Income"),
  EXPENSE(1, "Expense");

  private final int code;
  private final String description;

  TransactionType(int code, String description) {
    this.code = code;
    this.description = description;
  }

  public int getCode() {
    return code;
  }

  public String getDescription() {
    return description;
  }

  public static TransactionType getByCode(int code) {
    for (TransactionType type : values()) {
      if (type.code == code) {
        return type;
      }
    }
    throw new IllegalArgumentException("Invalid code for TransactionType: " + code);
  }

  @Override
  public String toString() {
    return description;
  }
}
