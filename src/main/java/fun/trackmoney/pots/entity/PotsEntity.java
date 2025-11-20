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
import org.hibernate.validator.constraints.Length;

@Entity
@Table(name = "tb_pots")
public class PotsEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "pot_id")
  private Long potId;

  @Length(min = 3, max = 30)
  private String name;

  private Long targetAmount;
  private Long currentAmount;

  @ManyToOne
  @JoinColumn(name = "account_id")
  private AccountEntity account;

  public PotsEntity() {
  }

  public PotsEntity(Long potId,
                    String name,
                    Long targetAmount,
                    Long currentAmount,
                    AccountEntity accountId) {
    this.potId = potId;
    this.name = name;
    this.targetAmount = targetAmount;
    this.currentAmount = currentAmount;
    this.account = accountId;
  }

  public Long getPotId() {
    return potId;
  }

  public PotsEntity setPotId(Long potId) {
    this.potId = potId;
    return this;
  }

  public String getName() {
    return name;
  }

  public PotsEntity setName(String name) {
    this.name = name;
    return this;
  }

  public Long getTargetAmount() {
    return targetAmount;
  }

  public PotsEntity setTargetAmount(Long targetAmount) {
    this.targetAmount = targetAmount;
    return this;
  }

  public Long getCurrentAmount() {
    return currentAmount;
  }

  public PotsEntity setCurrentAmount(Long currentAmount) {
    this.currentAmount = currentAmount;
    return this;
  }

  public AccountEntity getAccount() {
    return account;
  }

  public PotsEntity setAccount(AccountEntity account) {
    this.account = account;
    return this;
  }
}
