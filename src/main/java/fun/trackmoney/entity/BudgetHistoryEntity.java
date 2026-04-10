package fun.trackmoney.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import fun.trackmoney.budget.enums.BudgetStatus;
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
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "tb_budget_history")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class BudgetHistoryEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "history_id")
  private Integer historyId;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "budget_id", nullable = false)
  private BudgetsEntity budget;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "account_id", nullable = false)
  private AccountEntity account;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "category_id", nullable = false)
  private CategoryEntity category;

  @Column(name = "reference_month", nullable = false)
  private Short referenceMonth;

  @Column(name = "reference_year", nullable = false)
  private Integer referenceYear;

  @Column(name = "percent", nullable = false)
  private Short percent;

  @Column(name = "target_amount", nullable = false, precision = 19, scale = 4)
  private BigDecimal targetAmount;

  @Column(name = "spent_amount", nullable = false, precision = 19, scale = 4)
  private BigDecimal spentAmount;

  @Column(name = "remaining_amount", nullable = false, precision = 19, scale = 4)
  private BigDecimal remainingAmount;

  @Column(name = "total_income", nullable = false, precision = 19, scale = 4)
  private BigDecimal totalIncome;

  @Enumerated(EnumType.STRING)
  @Column(name = "status", nullable = false, length = 20)
  private BudgetStatus status;

  @Column(name = "created_at", nullable = false, updatable = false)
  private LocalDateTime createdAt;

  public BudgetHistoryEntity() {
    // Default constructor required by JPA/Hibernate for entity instantiation.
  }

  @PrePersist
  protected void onCreate() {
    createdAt = LocalDateTime.now();
  }

  public Integer getHistoryId() {
    return historyId;
  }

  public BudgetHistoryEntity setHistoryId(Integer historyId) {
    this.historyId = historyId;
    return this;
  }

  public BudgetsEntity getBudget() {
    return budget;
  }

  public BudgetHistoryEntity setBudget(BudgetsEntity budget) {
    this.budget = budget;
    return this;
  }

  public AccountEntity getAccount() {
    return account;
  }

  public BudgetHistoryEntity setAccount(AccountEntity account) {
    this.account = account;
    return this;
  }

  public CategoryEntity getCategory() {
    return category;
  }

  public BudgetHistoryEntity setCategory(CategoryEntity category) {
    this.category = category;
    return this;
  }

  public Short getReferenceMonth() {
    return referenceMonth;
  }

  public BudgetHistoryEntity setReferenceMonth(Short referenceMonth) {
    this.referenceMonth = referenceMonth;
    return this;
  }

  public Integer getReferenceYear() {
    return referenceYear;
  }

  public BudgetHistoryEntity setReferenceYear(Integer referenceYear) {
    this.referenceYear = referenceYear;
    return this;
  }

  public Short getPercent() {
    return percent;
  }

  public BudgetHistoryEntity setPercent(Short percent) {
    this.percent = percent;
    return this;
  }

  public BigDecimal getTargetAmount() {
    return targetAmount;
  }

  public BudgetHistoryEntity setTargetAmount(BigDecimal targetAmount) {
    this.targetAmount = targetAmount;
    return this;
  }

  public BigDecimal getSpentAmount() {
    return spentAmount;
  }

  public BudgetHistoryEntity setSpentAmount(BigDecimal spentAmount) {
    this.spentAmount = spentAmount;
    return this;
  }

  public BigDecimal getRemainingAmount() {
    return remainingAmount;
  }

  public BudgetHistoryEntity setRemainingAmount(BigDecimal remainingAmount) {
    this.remainingAmount = remainingAmount;
    return this;
  }

  public BigDecimal getTotalIncome() {
    return totalIncome;
  }

  public BudgetHistoryEntity setTotalIncome(BigDecimal totalIncome) {
    this.totalIncome = totalIncome;
    return this;
  }

  public BudgetStatus getStatus() {
    return status;
  }

  public BudgetHistoryEntity setStatus(BudgetStatus status) {
    this.status = status;
    return this;
  }

  public LocalDateTime getCreatedAt() {
    return createdAt;
  }

  public BudgetHistoryEntity setCreatedAt(LocalDateTime createdAt) {
    this.createdAt = createdAt;
    return this;
  }
}
