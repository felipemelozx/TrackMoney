package fun.trackmoney.enums;

/**
 * Enumeration representing the types of financial transactions.
 * There are two types: {@code INCOME} and {@code EXPENSE}.
 */
public enum TransactionType {

  /**
   * Transaction type for income.
   */
  INCOME(0, "Income"),

  /**
   * Transaction type for expenses.
   */
  EXPENSE(1, "Expense");

  /**
   * The code associated with the transaction type.
   */
  private final int code;

  /**
   * The description of the transaction type.
   */
  private final String description;

  /**
   * Constructor to initialize the code and description
   * for each transaction type.
   *
   * @param code        The code of the transaction type.
   * @param description The description of the transaction type.
   */
  TransactionType(int code, String description) {
    this.code = code;
    this.description = description;
  }

  /**
   * Gets the code associated with the transaction type.
   *
   * @return The code of the transaction type.
   */
  public int getCode() {
    return code;
  }

  /**
   * Gets the description associated with the transaction type.
   *
   * @return The description of the transaction type.
   */
  public String getDescription() {
    return description;
  }

  /**
   * Gets the transaction type associated with the given code.
   *
   * @param code The code of the transaction type.
   * @return The transaction type corresponding to the provided code.
   * @throws IllegalArgumentException If the provided code does not
   *                                  match any valid transaction type.
   */
  public static TransactionType getByCode(int code) {
    for (TransactionType type : values()) {
      if (type.code == code) {
        return type;
      }
    }
    throw new IllegalArgumentException("Invalid code for TransactionType: " + code);
  }

  /**
   * Returns the description of the transaction type as its string representation.
   *
   * @return The description of the transaction type.
   */
  @Override
  public String toString() {
    return description;
  }
}
