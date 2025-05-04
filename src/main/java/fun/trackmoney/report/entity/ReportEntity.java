package fun.trackmoney.report.entity;

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
 * Entity representing a financial report.
 * The report contains details like the associated account, 
 * the start and end dates, as well as the total income and total expenses during that period.
 */
@Entity
@Table(name = "tb_report")
public class ReportEntity {

  /**
   * Unique identifier for the report.
   */
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer reportId;

  /**
   * The account associated with this report.
   */
  @ManyToOne
  @JoinColumn(name = "account_id", nullable = false)
  private AccountEntity account;

  /**
   * The start date of the report period.
   */
  private Timestamp startDate;

  /**
   * The end date of the report period.
   */
  private Timestamp endDate;

  /**
   * The total income for the period covered by this report.
   */
  private BigDecimal totalIncome;

  /**
   * The total expense for the period covered by this report.
   */
  private BigDecimal totalExpense;

  /**
   * Default constructor for the ReportEntity class.
   */
  public ReportEntity() {
  }

  /**
   * Constructor to initialize a report entity with the provided values.
   *
   * @param reportId The unique identifier for the report.
   * @param account The account associated with the report.
   * @param startDate The start date of the report period.
   * @param endDate The end date of the report period.
   * @param totalIncome The total income during the report period.
   * @param totalExpense The total expense during the report period.
   */
  public ReportEntity(Integer reportId, AccountEntity account, Timestamp startDate,
                      Timestamp endDate, BigDecimal totalIncome, BigDecimal totalExpense) {
    this.reportId = reportId;
    this.account = account;
    this.startDate = startDate;
    this.endDate = endDate;
    this.totalIncome = totalIncome;
    this.totalExpense = totalExpense;
  }

  /**
   * Gets the report ID.
   *
   * @return The unique identifier for the report.
   */
  public Integer getReportId() {
    return reportId;
  }

  /**
   * Sets the report ID.
   *
   * @param reportId The unique identifier for the report.
   */
  public void setReportId(Integer reportId) {
    this.reportId = reportId;
  }

  /**
   * Gets the account associated with the report.
   *
   * @return The account associated with the report.
   */
  public AccountEntity getAccount() {
    return account;
  }

  /**
   * Sets the account associated with the report.
   *
   * @param account The account to associate with the report.
   */
  public void setAccount(AccountEntity account) {
    this.account = account;
  }

  /**
   * Gets the start date of the report period.
   *
   * @return The start date of the report period.
   */
  public Timestamp getStartDate() {
    return startDate;
  }

  /**
   * Sets the start date of the report period.
   *
   * @param startDate The start date of the report period.
   */
  public void setStartDate(Timestamp startDate) {
    this.startDate = startDate;
  }

  /**
   * Gets the end date of the report period.
   *
   * @return The end date of the report period.
   */
  public Timestamp getEndDate() {
    return endDate;
  }

  /**
   * Sets the end date of the report period.
   *
   * @param endDate The end date of the report period.
   */
  public void setEndDate(Timestamp endDate) {
    this.endDate = endDate;
  }

  /**
   * Gets the total income during the report period.
   *
   * @return The total income for the report period.
   */
  public BigDecimal getTotalIncome() {
    return totalIncome;
  }

  /**
   * Sets the total income for the report period.
   *
   * @param totalIncome The total income during the report period.
   */
  public void setTotalIncome(BigDecimal totalIncome) {
    this.totalIncome = totalIncome;
  }

  /**
   * Gets the total expense during the report period.
   *
   * @return The total expense for the report period.
   */
  public BigDecimal getTotalExpense() {
    return totalExpense;
  }

  /**
   * Sets the total expense for the report period.
   *
   * @param totalExpense The total expense during the report period.
   */
  public void setTotalExpense(BigDecimal totalExpense) {
    this.totalExpense = totalExpense;
  }
}
