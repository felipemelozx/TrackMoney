package fun.trackmoney.account.entity;

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

/**
 * Represents a bank account entity associated with a user.
 * The entity is mapped to the {@code tb_account} table in the database.
 */
@Entity
@Table(name = "tb_account")
public class AccountEntity {

  /**
   * The unique ID of the bank account.
   * The primary key is automatically generated.
   */
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer accountId;

  /**
   * The user associated with this bank account.
   * A user can have multiple accounts.
   */
  @ManyToOne
  @JoinColumn(name = "user_id", nullable = false)
  private UserEntity user;

  /**
   * The name given to the bank account.
   */
  private String name;

  /**
   * The current balance of the bank account.
   */
  private BigDecimal balance;

  /**
   * Indicates whether this account is the user's default account.
   * Can be {@code true} or {@code false}.
   */
  @Column(name = "is_account_default")
  private Boolean isAccountDefault;

  /**
   * Default constructor for creating an instance of {@link AccountEntity}.
   * This constructor is required for JPA persistence.
   */
  public AccountEntity() {
  }

  /**
   * Full constructor to create an instance of {@link AccountEntity}.
   *
   * @param accountId        The unique ID of the bank account.
   * @param user             The user associated with this account.
   * @param name             The name of the account.
   * @param balance          The balance of the account.
   * @param isAccountDefault Indicates whether this is the user's default account.
   */
  public AccountEntity(Integer accountId, UserEntity user, String name,
                       BigDecimal balance, Boolean isAccountDefault) {
    this.accountId = accountId;
    this.user = user;
    this.name = name;
    this.balance = balance;
    this.isAccountDefault = isAccountDefault;
  }

  /**
   * Gets the account ID.
   *
   * @return The account ID.
   */
  public Integer getAccountId() {
    return accountId;
  }

  /**
   * Sets the account ID.
   *
   * @param accountId The account ID to set.
   */
  public void setAccountId(Integer accountId) {
    this.accountId = accountId;
  }

  /**
   * Gets the user associated with this bank account.
   *
   * @return The user associated with the account.
   */
  public UserEntity getUser() {
    return user;
  }

  /**
   * Sets the user associated with this bank account.
   *
   * @param user The user to associate with the account.
   */
  public void setUser(UserEntity user) {
    this.user = user;
  }

  /**
   * Gets the name of the bank account.
   *
   * @return The name of the account.
   */
  public String getName() {
    return name;
  }

  /**
   * Sets the name of the bank account.
   *
   * @param name The name of the account.
   */
  public void setName(String name) {
    this.name = name;
  }

  /**
   * Gets the balance of the bank account.
   *
   * @return The balance of the account.
   */
  public BigDecimal getBalance() {
    return balance;
  }

  /**
   * Sets the balance of the bank account.
   *
   * @param balance The balance of the account.
   */
  public void setBalance(BigDecimal balance) {
    this.balance = balance;
  }

  /**
   * Gets the status indicating whether this is the default account.
   *
   * @return {@code true} if this is the default account, {@code false} otherwise.
   */
  public Boolean getIsAccountDefault() {
    return isAccountDefault;
  }

  /**
   * Sets the status of whether this is the default account.
   *
   * @param accountDefault {@code true} if this is the default account, {@code false} otherwise.
   */
  public void setIsAccountDefault(Boolean accountDefault) {
    isAccountDefault = accountDefault;
  }
}
