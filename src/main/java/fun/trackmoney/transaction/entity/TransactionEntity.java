package fun.trackmoney.transaction.entity;

import fun.trackmoney.account.entity.AccountEntity;
import fun.trackmoney.category.entity.CategoryEntity;
import fun.trackmoney.enums.TransactionType;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.sql.Timestamp;

@Entity
@Table(name = "tb_transaction")
public class TransactionEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer transactionId;
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
  private Timestamp transactionDate;

  public TransactionEntity() {
  }

  public TransactionEntity(Integer transactionId, AccountEntity account, CategoryEntity category, TransactionType transactionType, BigDecimal amount, String description, Timestamp transactionDate) {
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

  public void setTransactionId(Integer transactionId) {
    this.transactionId = transactionId;
  }

  public AccountEntity getaccount() {
    return account;
  }

  public void setaccount(AccountEntity account) {
    this.account = account;
  }

  public CategoryEntity getcategory() {
    return category;
  }

  public void setcategory(CategoryEntity category) {
    this.category = category;
  }

  public TransactionType getTransactionType() {
    return transactionType;
  }

  public void setTransactionType(TransactionType transactionType) {
    this.transactionType = transactionType;
  }

  public BigDecimal getAmount() {
    return amount;
  }

  public void setAmount(BigDecimal amount) {
    this.amount = amount;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public Timestamp getTransactionDate() {
    return transactionDate;
  }

  public void setTransactionDate(Timestamp transactionDate) {
    this.transactionDate = transactionDate;
  }
}
