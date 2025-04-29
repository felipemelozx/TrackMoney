package fun.trackmoney.account.entity;

import fun.trackmoney.user.entity.UserEntity;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "tb_account")
public class AccountEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer accountId;

  @ManyToOne
  private UserEntity userId;

  private String name;
  private BigDecimal balance;
  private Boolean isAccountDefault;

  public AccountEntity() {
  }

  public AccountEntity(Integer accountId, UserEntity userId, String name, BigDecimal balance, Boolean isAccountDefault) {
    this.accountId = accountId;
    this.userId = userId;
    this.name = name;
    this.balance = balance;
    this.isAccountDefault = isAccountDefault;
  }

  public Integer getAccountId() {
    return accountId;
  }

  public void setAccountId(Integer accountId) {
    this.accountId = accountId;
  }

  public UserEntity getUserId() {
    return userId;
  }

  public void setUserId(UserEntity userId) {
    this.userId = userId;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public BigDecimal getBalance() {
    return balance;
  }

  public void setBalance(BigDecimal balance) {
    this.balance = balance;
  }

  public Boolean getAccountDefault() {
    return isAccountDefault;
  }

  public void setAccountDefault(Boolean accountDefault) {
    isAccountDefault = accountDefault;
  }
}
