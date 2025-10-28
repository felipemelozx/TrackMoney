package fun.trackmoney.user.entity;

import fun.trackmoney.account.entity.AccountEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

@Entity
@Table(name = "tb_user")
public class UserEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID userId;

  @NotNull
  @NotBlank
  private String name;

  @NotNull
  @Email
  private String email;

  @NotNull
  @NotBlank
  private String password;

  @OneToOne(cascade = CascadeType.ALL)
  @JoinColumn(name = "account_id")
  private AccountEntity account;

  @Column(name = "is_active", nullable = false)
  private boolean isActive = false;


  public UserEntity() {
  }

  public UserEntity(UUID userId, String name, String email, String password) {
    this.userId = userId;
    this.name = name;
    this.email = email;
    this.password = password;
  }

  public UserEntity(UUID userId, String name, String email, String password, boolean isActive) {
    this(userId, name, email, password);
    this.isActive = isActive;
  }

  public UserEntity(UUID userId, String name, String email, String password, AccountEntity account, boolean isActive) {
    this(userId, name, email, password, isActive);
    this.account = account;
  }

  public AccountEntity getAccount() {
    return account;
  }

  public UserEntity setAccount(AccountEntity account) {
    this.account = account;
    return this;
  }

  public UUID getUserId() {
    return userId;
  }

  public UserEntity setUserId(UUID userId) {
    this.userId = userId;
    return this;
  }

  public String getName() {
    return name;
  }

  public UserEntity setName(String name) {
    this.name = name;
    return this;
  }

  public String getEmail() {
    return email;
  }

  public UserEntity setEmail(String email) {
    this.email = email;
    return this;
  }

  public String getPassword() {
    return password;
  }

  public UserEntity setPassword(String password) {
    this.password = password;
    return this;
  }

  public boolean isActive() {
    return isActive;
  }

  public UserEntity activate() {
    this.isActive = true;
    return this;
  }
}
