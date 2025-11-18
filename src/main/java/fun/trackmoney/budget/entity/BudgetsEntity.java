package fun.trackmoney.budget.entity;

import fun.trackmoney.account.entity.AccountEntity;
import fun.trackmoney.category.entity.CategoryEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

@Entity
@Table(name = "tb_budget")
public class BudgetsEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "budget_id")
  private Integer budgetId;

  @NotNull
  @ManyToOne
  @JoinColumn(name = "category_id")
  private CategoryEntity category;

  @ManyToOne
  @JoinColumn(name = "account_id")
  @NotNull
  private AccountEntity account;

  @Positive
  @Min(1)
  private short percent;

  public BudgetsEntity() {
  }

  public BudgetsEntity(Integer budgetId, CategoryEntity category) {
    this.budgetId = budgetId;
    this.category = category;
  }

  public BudgetsEntity(Integer budgetId, CategoryEntity category, AccountEntity account) {
    this(budgetId, category);
    this.account = account;
  }

  public BudgetsEntity(Integer budgetId, CategoryEntity category, AccountEntity account, short percent) {
    this(budgetId, category, account);
    this.percent = percent;
  }

  public Integer getBudgetId() {
    return budgetId;
  }

  public BudgetsEntity setBudgetId(Integer budgetId) {
    this.budgetId = budgetId;
    return this;
  }

  public CategoryEntity getCategory() {
    return category;
  }

  public BudgetsEntity setCategory(CategoryEntity category) {
    this.category = category;
    return this;
  }

  public AccountEntity getAccount() {
    return account;
  }

  public BudgetsEntity setAccount(AccountEntity account) {
    this.account = account;
    return this;
  }

  public short getPercent() {
    return percent;
  }

  public BudgetsEntity setPercent(short percent) {
    this.percent = percent;
    return this;
  }
}


