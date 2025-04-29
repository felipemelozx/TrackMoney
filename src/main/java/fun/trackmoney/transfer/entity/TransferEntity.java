package fun.trackmoney.transfer.entity;

import fun.trackmoney.account.entity.AccountEntity;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.sql.Timestamp;

@Entity
@Table(name = "tb_transfer")
public class TransferEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer transferId;

  private BigDecimal amount;

  private Timestamp transferDate;

  @ManyToOne
  private AccountEntity fromAccountId;

  @ManyToOne
  private AccountEntity toAccountId;

  public TransferEntity() {
  }

  public TransferEntity(Integer transferId, BigDecimal amount, Timestamp transferDate, AccountEntity fromAccountId, AccountEntity toAccountId) {
    this.transferId = transferId;
    this.amount = amount;
    this.transferDate = transferDate;
    this.fromAccountId = fromAccountId;
    this.toAccountId = toAccountId;
  }

  public Integer getTransferId() {
    return transferId;
  }

  public void setTransferId(Integer transferId) {
    this.transferId = transferId;
  }

  public BigDecimal getAmount() {
    return amount;
  }

  public void setAmount(BigDecimal amount) {
    this.amount = amount;
  }

  public Timestamp getTransferDate() {
    return transferDate;
  }

  public void setTransferDate(Timestamp transferDate) {
    this.transferDate = transferDate;
  }

  public AccountEntity getFromAccountId() {
    return fromAccountId;
  }

  public void setFromAccountId(AccountEntity fromAccountId) {
    this.fromAccountId = fromAccountId;
  }

  public AccountEntity getToAccountId() {
    return toAccountId;
  }

  public void setToAccountId(AccountEntity toAccountId) {
    this.toAccountId = toAccountId;
  }
}
