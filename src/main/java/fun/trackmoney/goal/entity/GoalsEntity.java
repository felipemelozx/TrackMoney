package fun.trackmoney.goal.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(name = "tb_transaction")
public class GoalsEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer goalsId;

  private String goal;

  private Integer accountId;
  private BigDecimal targetAmount;
  private BigDecimal currentAmount;
  private Integer progress;

  public GoalsEntity() {
  }

  public GoalsEntity(Integer goalsId, String goal, Integer accountId, BigDecimal targetAmount, BigDecimal currentAmount, Integer progress) {
    this.goalsId = goalsId;
    this.goal = goal;
    this.accountId = accountId;
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

  public Integer getAccountId() {
    return accountId;
  }

  public void setAccountId(Integer accountId) {
    this.accountId = accountId;
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
