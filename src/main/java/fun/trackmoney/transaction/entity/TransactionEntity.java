package fun.trackmoney.transaction.entity;

import fun.trackmoney.account.entity.AccountEntity;
import fun.trackmoney.category.entity.CategoryEntity;
import fun.trackmoney.enums.TransactionType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.sql.Timestamp;

/**
 * Entity representing a financial transaction.
 * The transaction includes details about the associated account, category, type of transaction,
 * the amount, description, and transaction date.
 */
@Entity
@Table(name = "tb_transaction")
public class TransactionEntity {

  /**
   * Unique identifier for the transaction.
   */
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer transactionId;

  /**
   * The account associated with the transaction.
   */
  @ManyToOne
  @JoinColumn(name = "account_id", nullable = false)
  private AccountEntity account;

  /**
   * The category associated with the transaction.
   */
  @ManyToOne
  @JoinColumn(name = "category_id", nullable = false)
  private CategoryEntity category;

  /**
   * The type of the transaction (e.g., income, expense).
   */
  @Enumerated(EnumType.STRING)
  private TransactionType transactionType;

  /**
   * The amount of the transaction.
   */
  private BigDecimal amount;

  /**
   * A description of the transaction.
   */
  private String description;

  /**
   * The date and time when the transaction occurred.
   */
  private Timestamp transactionDate;

  /**
   * Default constructor for the TransactionEntity class.
   */
  public TransactionEntity() {
  }

  /**
   * Constructor to initialize a transaction entity with the provided values.
   *
   * @param transactionId The unique identifier for the transaction.
   * @param account The account associated with the transaction.
   * @param category The category associated with the transaction.
   * @param transactionType The type of the transaction (income or expense).
   * @param amount The amount of the transaction.
   * @param description The description of the transaction.
   * @param transactionDate The date and time when the transaction occurred.
   */
  public TransactionEntity(Integer transactionId, AccountEntity account, CategoryEntity category,
                           TransactionType transactionType, BigDecimal amount, String description,
                           Timestamp transactionDate) {
    this.transactionId = transactionId;
    this.account = account;
    this.category = category;
    this.transactionType = transactionType;
    this.amount = amount;
    this.description = description;
    this.transactionDate = transactionDate;
  }

  /**
   * Gets the unique identifier for the transaction.
   *
   * @return The transaction ID.
   */
  public Integer getTransactionId() {
    return transactionId;
  }

  /**
   * Sets the unique identifier for the transaction.
   *
   * @param transactionId The transaction ID to set.
   */
  public void setTransactionId(Integer transactionId) {
    this.transactionId = transactionId;
  }

  /**
   * Gets the account associated with the transaction.
   *
   * @return The associated account entity.
   */
  public AccountEntity getAccount() {
    return account;
  }

  /**
   * Sets the account associated with the transaction.
   *
   * @param account The account to associate with the transaction.
   */
  public void setAccount(AccountEntity account) {
    this.account = account;
  }

  /**
   * Gets the category associated with the transaction.
   *
   * @return The associated category entity.
   */
  public CategoryEntity getCategory() {
    return category;
  }

  /**
   * Sets the category associated with the transaction.
   *
   * @param category The category to associate with the transaction.
   */
  public void setCategory(CategoryEntity category) {
    this.category = category;
  }

  /**
   * Gets the type of the transaction (e.g., income or expense).
   *
   * @return The transaction type.
   */
  public TransactionType getTransactionType() {
    return transactionType;
  }

  /**
   * Sets the type of the transaction (e.g., income or expense).
   *
   * @param transactionType The type of the transaction.
   */
  public void setTransactionType(TransactionType transactionType) {
    this.transactionType = transactionType;
  }

  /**
   * Gets the amount of the transaction.
   *
   * @return The amount of the transaction.
   */
  public BigDecimal getAmount() {
    return amount;
  }

  /**
   * Sets the amount of the transaction.
   *
   * @param amount The amount to set for the transaction.
   */
  public void setAmount(BigDecimal amount) {
    this.amount = amount;
  }

  /**
   * Gets the description of the transaction.
   *
   * @return The description of the transaction.
   */
  public String getDescription() {
    return description;
  }

  /**
   * Sets the description for the transaction.
   *
   * @param description The description to set for the transaction.
   */
  public void setDescription(String description) {
    this.description = description;
  }

  /**
   * Gets the date and time when the transaction occurred.
   *
   * @return The transaction date and time.
   */
  public Timestamp getTransactionDate() {
    return transactionDate;
  }

  /**
   * Sets the date and time when the transaction occurred.
   *
   * @param transactionDate The transaction date and time to set.
   */
  public void setTransactionDate(Timestamp transactionDate) {
    this.transactionDate = transactionDate;
  }
}
