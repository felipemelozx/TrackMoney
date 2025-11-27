package fun.trackmoney.recurring.entity;


import fun.trackmoney.account.entity.AccountEntity;
import fun.trackmoney.category.entity.CategoryEntity;
import fun.trackmoney.enums.Frequency;
import fun.trackmoney.enums.TransactionType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "tb_recurring")
public class RecurringEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "recurring_id")
  private Long id;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 50)
  private Frequency frequency;

  @Column(name = "next_date", nullable = false)
  private LocalDateTime nextDate;

  @Column(name = "last_date")
  private LocalDateTime lastDate;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "account_id", nullable = false)
  private AccountEntity account;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "category_id", nullable = false)
  private CategoryEntity category;

  @Enumerated(EnumType.STRING)
  @Column(name = "transaction_type", nullable = false, length = 50)
  private TransactionType transactionType;

  @Column(nullable = false, precision = 19, scale = 4)
  private BigDecimal amount;

  @Column(length = 255)
  private String description;

  @Column(name = "transaction_name", length = 50)
  private String transactionName;

  public RecurringEntity() {
  }

  public RecurringEntity(Long id,
                         Frequency frequency,
                         LocalDateTime nextDate,
                         LocalDateTime lastDate,
                         AccountEntity account,
                         CategoryEntity category,
                         TransactionType transactionType,
                         BigDecimal amount,
                         String description,
                         String transactionName) {
    this.id = id;
    this.frequency = frequency;
    this.nextDate = nextDate;
    this.lastDate = lastDate;
    this.account = account;
    this.category = category;
    this.transactionType = transactionType;
    this.amount = amount;
    this.description = description;
    this.transactionName = transactionName;
  }

  public Long getId() {
    return id;
  }

  public RecurringEntity setId(Long id) {
    this.id = id;
    return this;
  }

  public Frequency getFrequency() {
    return frequency;
  }

  public RecurringEntity setFrequency(Frequency frequency) {
    this.frequency = frequency;
    return this;
  }

  public LocalDateTime getNextDate() {
    return nextDate;
  }

  public RecurringEntity setNextDate(LocalDateTime nextDate) {
    this.nextDate = nextDate;
    return this;
  }

  public LocalDateTime getLastDate() {
    return lastDate;
  }

  public RecurringEntity setLastDate(LocalDateTime lastDate) {
    this.lastDate = lastDate;
    return this;
  }

  public AccountEntity getAccount() {
    return account;
  }

  public RecurringEntity setAccount(AccountEntity account) {
    this.account = account;
    return this;
  }

  public CategoryEntity getCategory() {
    return category;
  }

  public RecurringEntity setCategory(CategoryEntity category) {
    this.category = category;
    return this;
  }

  public TransactionType getTransactionType() {
    return transactionType;
  }

  public RecurringEntity setTransactionType(TransactionType transactionType) {
    this.transactionType = transactionType;
    return this;
  }

  public BigDecimal getAmount() {
    return amount;
  }

  public RecurringEntity setAmount(BigDecimal amount) {
    this.amount = amount;
    return this;
  }

  public String getDescription() {
    return description;
  }

  public RecurringEntity setDescription(String description) {
    this.description = description;
    return this;
  }

  public String getTransactionName() {
    return transactionName;
  }

  public RecurringEntity setTransactionName(String transactionName) {
    this.transactionName = transactionName;
    return this;
  }
}
