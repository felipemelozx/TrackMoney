package fun.trackmoney.budget.entity;

import fun.trackmoney.account.entity.AccountEntity;
import fun.trackmoney.category.entity.CategoryEntity;
import fun.trackmoney.user.entity.UserEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.math.BigDecimal;

@Entity
@Table(name = "tb_budget")
public class BudgetsEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "budget_id")
  private Integer budgetId;

  @ManyToOne
  @JoinColumn(name = "category_id")
  private CategoryEntity category;

  @ManyToOne
  @JoinColumn(name = "account_id")
  private AccountEntity account;
  @ManyToOne
  @JoinColumn(name = "user_id")
  private UserEntity userEntity;

  private BigDecimal targetAmount;
  @Column(name = "reset_day")
  private Integer resetDay;

  public AccountEntity getAccount() {
    return account;
  }

  public BudgetsEntity() {

  }

  public BudgetsEntity(Integer budgetId, CategoryEntity category, UserEntity userEntity, BigDecimal targetAmount, Integer resetDay) {
    this.budgetId = budgetId;
    this.category = category;
    this.userEntity = userEntity;
    this.targetAmount = targetAmount;
    this.resetDay = resetDay;
  }

  public void setAccount(AccountEntity account) {
    this.account = account;
  }

  public BudgetsEntity(UserEntity userEntity) {
    this.userEntity = userEntity;
  }

  public Integer getBudgetId() {
    return budgetId;
  }

  public void setBudgetId(Integer budgetId) {
    this.budgetId = budgetId;
  }

  public CategoryEntity getCategory() {
    return category;
  }

  public void setCategory(CategoryEntity category) {
    this.category = category;
  }

  public BigDecimal getTargetAmount() {
    return targetAmount;
  }

  public void setTargetAmount(BigDecimal targetAmount) {
    this.targetAmount = targetAmount;
  }

  public Integer getResetDay() {
    return resetDay;
  }

  public void setResetDay(Integer resetDay) {
    this.resetDay = resetDay;
  }

  public UserEntity getUserEntity() {
    return userEntity;
  }

  public void setUserEntity(UserEntity userEntity) {
    this.userEntity = userEntity;
  }
}
