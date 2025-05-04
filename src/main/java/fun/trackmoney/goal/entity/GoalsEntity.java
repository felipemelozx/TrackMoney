package fun.trackmoney.goal.entity;

import fun.trackmoney.account.entity.AccountEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;

/**
 * Represents a financial goal entity in the system.
 * Each goal is associated with an account and tracks the progress towards a target amount.
 */
@Entity
@Table(name = "tb_goals")
public class GoalsEntity {

  /**
   * The unique identifier for the goal.
   */
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer goalsId;

  /**
   * The name or description of the goal.
   */
  private String goal;

  /**
   * The account associated with the goal.
   * A goal belongs to a specific account.
   */
  @ManyToOne
  @JoinColumn(name = "account_id", nullable = false)
  private AccountEntity account;

  /**
   * The target amount for the goal.
   */
  private BigDecimal targetAmount;

  /**
   * The current amount achieved toward the goal.
   */
  private BigDecimal currentAmount;

  /**
   * The progress percentage toward the goal.
   * This value is typically calculated based on the targetAmount and currentAmount.
   */
  private Integer progress;

  /**
   * Default constructor for JPA.
   */
  public GoalsEntity() {
  }

  /**
   * Constructor to initialize a goal with specified values.
   *
   * @param goalsId The unique identifier for the goal.
   * @param goal The name or description of the goal.
   * @param account The account associated with the goal.
   * @param targetAmount The target amount for the goal.
   * @param currentAmount The current amount towards the goal.
   * @param progress The progress percentage towards the goal.
   */
  public GoalsEntity(Integer goalsId, String goal, AccountEntity account, BigDecimal targetAmount,
                     BigDecimal currentAmount, Integer progress) {
    this.goalsId = goalsId;
    this.goal = goal;
    this.account = account;
    this.targetAmount = targetAmount;
    this.currentAmount = currentAmount;
    this.progress = progress;
  }

  /**
   * Gets the unique identifier of the goal.
   *
   * @return The goal ID.
   */
  public Integer getGoalsId() {
    return goalsId;
  }

  /**
   * Sets the unique identifier of the goal.
   *
   * @param goalsId The goal ID to set.
   */
  public void setGoalsId(Integer goalsId) {
    this.goalsId = goalsId;
  }

  /**
   * Gets the name or description of the goal.
   *
   * @return The goal description.
   */
  public String getGoal() {
    return goal;
  }

  /**
   * Sets the name or description of the goal.
   *
   * @param goal The goal description to set.
   */
  public void setGoal(String goal) {
    this.goal = goal;
  }

  /**
   * Gets the account associated with the goal.
   *
   * @return The associated account.
   */
  public AccountEntity getAccount() {
    return account;
  }

  /**
   * Sets the account associated with the goal.
   *
   * @param account The account to associate with the goal.
   */
  public void setAccount(AccountEntity account) {
    this.account = account;
  }

  /**
   * Gets the target amount for the goal.
   *
   * @return The target amount.
   */
  public BigDecimal getTargetAmount() {
    return targetAmount;
  }

  /**
   * Sets the target amount for the goal.
   *
   * @param targetAmount The target amount to set.
   */
  public void setTargetAmount(BigDecimal targetAmount) {
    this.targetAmount = targetAmount;
  }

  /**
   * Gets the current amount achieved toward the goal.
   *
   * @return The current amount.
   */
  public BigDecimal getCurrentAmount() {
    return currentAmount;
  }

  /**
   * Sets the current amount achieved toward the goal.
   *
   * @param currentAmount The current amount to set.
   */
  public void setCurrentAmount(BigDecimal currentAmount) {
    this.currentAmount = currentAmount;
  }

  /**
   * Gets the progress percentage toward the goal.
   *
   * @return The progress percentage.
   */
  public Integer getProgress() {
    return progress;
  }

  /**
   * Sets the progress percentage toward the goal.
   *
   * @param progress The progress percentage to set.
   */
  public void setProgress(Integer progress) {
    this.progress = progress;
  }
}
