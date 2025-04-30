package fun.trackmoney.goal.entity;

import fun.trackmoney.account.entity.AccountEntity;
import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(name = "tb_goals")
public class GoalsEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer goalsId;

  private String goal;
  @ManyToOne
  @JoinColumn(name = "account_id", nullable = false)
  private AccountEntity account;
  private BigDecimal targetAmount;
  private BigDecimal currentAmount;
  private Integer progress;

  public GoalsEntity() {
  }

  public GoalsEntity(Integer goalsId, String goal, AccountEntity account, BigDecimal targetAmount, BigDecimal currentAmount, Integer progress) {
    this.goalsId = goalsId;
    this.goal = goal;
    this.account = account;
    this.targetAmount = targetAmount;
    this.currentAmount = currentAmount;
    this.progress = progress;
  }

  public Integer getGoalsId() {
    return goalsId;
  }

  public void setGoalsId(Integer goalsId) {
    this.goalsId = goalsId;
  }

  public String getGoal() {
    return goal;
  }

  public void setGoal(String goal) {
    this.goal = goal;
  }

  public AccountEntity getAccount() {
    return account;
  }

  public void setAccount(AccountEntity account) {
    this.account = account;
  }

  public BigDecimal getTargetAmount() {
    return targetAmount;
  }

  public void setTargetAmount(BigDecimal targetAmount) {
    this.targetAmount = targetAmount;
  }

  public BigDecimal getCurrentAmount() {
    return currentAmount;
  }

  public void setCurrentAmount(BigDecimal currentAmount) {
    this.currentAmount = currentAmount;
  }

  public Integer getProgress() {
    return progress;
  }

  public void setProgress(Integer progress) {
    this.progress = progress;
  }
}
