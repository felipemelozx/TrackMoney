package fun.trackmoney.transaction.entity;

import fun.trackmoney.account.entity.AccountEntity;
import fun.trackmoney.category.entity.CategoryEntity;
import fun.trackmoney.enums.TransactionType;
import jakarta.persistence.Column;
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
import java.time.LocalDateTime;

@Entity
@Table(name = "tb_transaction")
public class TransactionEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer transactionId;

  @Column(name = "transaction_name")
  private String transactionName;

  @ManyToOne
  @JoinColumn(name = "account_id", nullable = false)
  private AccountEntity account;

  @ManyToOne
  @JoinColumn(name = "category_id", nullable = false)
  private CategoryEntity category;

  @Enumerated(EnumType.STRING)
  private TransactionType transactionType;

  private BigDecimal amount;

  private String description;

  private LocalDateTime transactionDate;

  public TransactionEntity() {
  }

  public TransactionEntity(Integer transactionId, AccountEntity account, CategoryEntity category,
                           TransactionType transactionType, BigDecimal amount, String description,
                           LocalDateTime transactionDate) {
    this.transactionId = transactionId;
    this.account = account;
    this.category = category;
    this.transactionType = transactionType;
    this.amount = amount;
    this.description = description;
    this.transactionDate = transactionDate;
  }

  public Integer getTransactionId() {
    return transactionId;
  }

  public TransactionEntity setTransactionId(Integer transactionId) {
    this.transactionId = transactionId;
    return this;
  }

  public AccountEntity getAccount() {
    return account;
  }

  public TransactionEntity setAccount(AccountEntity account) {
    this.account = account;
    return this;
  }

  public CategoryEntity getCategory() {
    return category;
  }


  public TransactionEntity setCategory(CategoryEntity category) {
    this.category = category;
    return this;
  }

  public TransactionType getTransactionType() {
    return transactionType;
  }

  public TransactionEntity setTransactionType(TransactionType transactionType) {
    this.transactionType = transactionType;
    return this;
  }

  public BigDecimal getAmount() {
    return amount;
  }

  public TransactionEntity setAmount(BigDecimal amount) {
    this.amount = amount;
    return this;
  }

  public String getDescription() {
    return description;
  }

  public TransactionEntity setDescription(String description) {
    this.description = description;
    return this;
  }

  public LocalDateTime getTransactionDate() {
    return transactionDate;
  }

  public TransactionEntity setTransactionDate(LocalDateTime transactionDate) {
    this.transactionDate = transactionDate;
    return this;
  }

  public String getTransactionName() {
    return transactionName;
  }

  public TransactionEntity setTransactionName(String transactionName) {
    this.transactionName = transactionName;
    return this;
  }
}
