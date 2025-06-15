package fun.trackmoney.pots.entity;

import fun.trackmoney.account.entity.AccountEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "tb_pots")
public class PotsEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "pot_id")
  private Long potId;

  private String name;
  private String description;
  private Long targetAmount;
  private Long currentAmount;
  @ManyToOne
  @JoinColumn(name = "account_id")
  private AccountEntity account;

  public PotsEntity() {
  }

  public PotsEntity(Long potId,
                    String name,
                    String description,
                    Long targetAmount,
                    Long currentAmount,
                    AccountEntity accountId) {
    this.potId = potId;
    this.name = name;
    this.description = description;
    this.targetAmount = targetAmount;
    this.currentAmount = currentAmount;
    this.account = accountId;
  }

  public Long getPotId() {
    return potId;
  }

  public void setPotId(Long potId) {
    this.potId = potId;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public Long getTargetAmount() {
    return targetAmount;
  }

  public void setTargetAmount(Long targetAmount) {
    this.targetAmount = targetAmount;
  }

  public Long getCurrentAmount() {
    return currentAmount;
  }

  public void setCurrentAmount(Long currentAmount) {
    this.currentAmount = currentAmount;
  }

  public AccountEntity getAccount() {
    return account;
  }

  public void setAccount(AccountEntity account) {
    this.account = account;
  }
}
