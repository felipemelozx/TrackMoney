package fun.trackmoney.transfer.entity;

import fun.trackmoney.account.entity.AccountEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.sql.Timestamp;

/**
 * Entity representing a financial transfer between two accounts.
 * The transfer includes details such as the amount, transfer date, and the accounts involved.
 */
@Entity
@Table(name = "tb_transfer")
public class TransferEntity {

  /**
   * Unique identifier for the transfer.
   */
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer transferId;

  /**
   * The amount of money being transferred.
   */
  private BigDecimal amount;

  /**
   * The date and time when the transfer occurred.
   */
  private Timestamp transferDate;

  /**
   * The account from which the money is being transferred.
   */
  @ManyToOne
  @JoinColumn(name = "from_account_id", nullable = false)
  private AccountEntity fromAccountId;

  /**
   * The account to which the money is being transferred.
   */
  @ManyToOne
  @JoinColumn(name = "to_account_id", nullable = false)
  private AccountEntity toAccountId;

  /**
   * Default constructor for the TransferEntity class.
   */
  public TransferEntity() {
  }

  /**
   * Constructor to initialize a transfer entity with the provided values.
   *
   * @param transferId The unique identifier for the transfer.
   * @param amount The amount of money being transferred.
   * @param transferDate The date and time when the transfer occurred.
   * @param fromAccountId The account from which the money is being transferred.
   * @param toAccountId The account to which the money is being transferred.
   */
  public TransferEntity(Integer transferId, BigDecimal amount, Timestamp transferDate,
                        AccountEntity fromAccountId, AccountEntity toAccountId) {
    this.transferId = transferId;
    this.amount = amount;
    this.transferDate = transferDate;
    this.fromAccountId = fromAccountId;
    this.toAccountId = toAccountId;
  }

  /**
   * Gets the unique identifier for the transfer.
   *
   * @return The transfer ID.
   */
  public Integer getTransferId() {
    return transferId;
  }

  /**
   * Sets the unique identifier for the transfer.
   *
   * @param transferId The transfer ID to set.
   */
  public void setTransferId(Integer transferId) {
    this.transferId = transferId;
  }

  /**
   * Gets the amount of money being transferred.
   *
   * @return The amount of the transfer.
   */
  public BigDecimal getAmount() {
    return amount;
  }

  /**
   * Sets the amount of money being transferred.
   *
   * @param amount The amount to set for the transfer.
   */
  public void setAmount(BigDecimal amount) {
    this.amount = amount;
  }

  /**
   * Gets the date and time when the transfer occurred.
   *
   * @return The transfer date and time.
   */
  public Timestamp getTransferDate() {
    return transferDate;
  }

  /**
   * Sets the date and time when the transfer occurred.
   *
   * @param transferDate The transfer date and time to set.
   */
  public void setTransferDate(Timestamp transferDate) {
    this.transferDate = transferDate;
  }

  /**
   * Gets the account from which the money is being transferred.
   *
   * @return The account from which the transfer originates.
   */
  public AccountEntity getFromAccountId() {
    return fromAccountId;
  }

  /**
   * Sets the account from which the money is being transferred.
   *
   * @param fromAccountId The account to set as the origin of the transfer.
   */
  public void setFromAccountId(AccountEntity fromAccountId) {
    this.fromAccountId = fromAccountId;
  }

  /**
   * Gets the account to which the money is being transferred.
   *
   * @return The account to which the transfer is going.
   */
  public AccountEntity getToAccountId() {
    return toAccountId;
  }

  /**
   * Sets the account to which the money is being transferred.
   *
   * @param toAccountId The account to set as the destination of the transfer.
   */
  public void setToAccountId(AccountEntity toAccountId) {
    this.toAccountId = toAccountId;
  }
}
