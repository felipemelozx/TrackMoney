package fun.trackmoney.pots.entity;

import fun.trackmoney.account.entity.AccountEntity;
import fun.trackmoney.pots.enums.ColorPick;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import org.hibernate.validator.constraints.Length;

import java.math.BigDecimal;

@Entity
@Table(name = "tb_pots")
public class PotsEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "pot_id")
  private Long potId;

  @Length(min = 3, max = 30)
  private String name;

  private BigDecimal targetAmount;
  private BigDecimal currentAmount;

  @ManyToOne
  @JoinColumn(name = "account_id")
  private AccountEntity account;

  @Enumerated(EnumType.STRING)
  @Column(length = 7)
  private ColorPick color;

  public PotsEntity() {
  }

  public PotsEntity(Long potId,
                    String name,
                    BigDecimal targetAmount,
                    BigDecimal currentAmount,
                    AccountEntity account) {
    this.potId = potId;
    this.name = name;
    this.targetAmount = targetAmount;
    this.currentAmount = currentAmount;
    this.account = account;
  }

  public PotsEntity(Long potId,
                    String name,
                    BigDecimal targetAmount,
                    BigDecimal currentAmount,
                    AccountEntity account,
                    ColorPick color) {

    this(potId, name, targetAmount, currentAmount, account);
    this.color = color;
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

  public BigDecimal getTargetAmount() {
    return targetAmount;
  }

  public PotsEntity setTargetAmount(BigDecimal targetAmount) {
    this.targetAmount = targetAmount;
    return this;
  }

  public BigDecimal getCurrentAmount() {
    return currentAmount;
  }

  public PotsEntity setCurrentAmount(BigDecimal currentAmount) {
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

  public ColorPick getColor() {
    return color;
  }

  public PotsEntity setColor(ColorPick color) {
    this.color = color;
    return this;
  }
}
