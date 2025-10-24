package fun.trackmoney.account.entity;

import fun.trackmoney.user.entity.UserEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;

@Entity
@Table(name = "tb_account")
public class AccountEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer accountId;

  @OneToOne
  @JoinColumn(name = "user_id", nullable = false)
  private UserEntity user;

  private String name;

  private BigDecimal balance;

  public AccountEntity() {
  }

  public AccountEntity(Integer accountId, UserEntity user, String name,
                       BigDecimal balance) {
    this.accountId = accountId;
    this.user = user;
    this.name = name;
    this.balance = balance;
  }

  public Integer getAccountId() {
    return accountId;
  }

  public AccountEntity setAccountId(Integer accountId) {
    this.accountId = accountId;
    return this;
  }

  public UserEntity getUser() {
    return user;
  }

  public AccountEntity setUser(UserEntity user) {
    this.user = user;
    return this;
  }

  public String getName() {
    return name;
  }

  public AccountEntity setName(String name) {
    this.name = name;
    return this;
  }

  public BigDecimal getBalance() {
    return balance;
  }

  public AccountEntity setBalance(BigDecimal balance) {
    this.balance = balance;
    return this;
  }

}
