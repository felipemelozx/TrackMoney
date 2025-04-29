package fun.trackmoney.transaction.entity;

import fun.trackmoney.account.entity.AccountEntity;
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
  private AccountEntity accountId;
  @OneToMany
  private AccountEntity categoryId;

  private TransactionType transactionType;
  private BigDecimal amount;
  private String description;
  private Timestamp transactionDate;

  public TransactionEntity() {
  }

  public TransactionEntity(Integer transactionId, AccountEntity accountId, AccountEntity categoryId, TransactionType transactionType, BigDecimal amount, String description, Timestamp transactionDate) {
    this.transactionId = transactionId;
    this.accountId = accountId;
    this.categoryId = categoryId;
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

  public AccountEntity getAccountId() {
    return accountId;
  }

  public void setAccountId(AccountEntity accountId) {
    this.accountId = accountId;
  }

  public AccountEntity getCategoryId() {
    return categoryId;
  }

  public void setCategoryId(AccountEntity categoryId) {
    this.categoryId = categoryId;
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
