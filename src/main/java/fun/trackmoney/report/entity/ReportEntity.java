package fun.trackmoney.report.entity;

import fun.trackmoney.account.entity.AccountEntity;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.sql.Timestamp;

@Entity
@Table(name = "tb_report")
public class ReportEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer reportId;

  @ManyToOne
  private AccountEntity accountId;
  private Timestamp startDate;
  private Timestamp endDate;
  private BigDecimal totalIncome;
  private BigDecimal totalExpense;

  public ReportEntity() {
  }

  public ReportEntity(Integer reportId, AccountEntity accountId, Timestamp startDate, Timestamp endDate, BigDecimal totalIncome, BigDecimal totalExpense) {
    this.reportId = reportId;
    this.accountId = accountId;
    this.startDate = startDate;
    this.endDate = endDate;
    this.totalIncome = totalIncome;
    this.totalExpense = totalExpense;
  }

  public Integer getReportId() {
    return reportId;
  }

  public void setReportId(Integer reportId) {
    this.reportId = reportId;
  }

  public AccountEntity getAccountId() {
    return accountId;
  }

  public void setAccountId(AccountEntity accountId) {
    this.accountId = accountId;
  }

  public Timestamp getStartDate() {
    return startDate;
  }

  public void setStartDate(Timestamp startDate) {
    this.startDate = startDate;
  }

  public Timestamp getEndDate() {
    return endDate;
  }

  public void setEndDate(Timestamp endDate) {
    this.endDate = endDate;
  }

  public BigDecimal getTotalIncome() {
    return totalIncome;
  }

  public void setTotalIncome(BigDecimal totalIncome) {
    this.totalIncome = totalIncome;
  }

  public BigDecimal getTotalExpense() {
    return totalExpense;
  }

  public void setTotalExpense(BigDecimal totalExpense) {
    this.totalExpense = totalExpense;
  }
}
