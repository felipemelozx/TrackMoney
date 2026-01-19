package fun.trackmoney.metrics.projection;

import java.math.BigDecimal;

/**
 * Projection interface for monthly aggregated transaction data.
 * Used by TransactionRepository to return aggregated income and expense per month.
 */
public interface MonthAggregateProjection {

  /**
   * Gets the month number (1-12).
   *
   * @return the month number
   */
  int getMonth();

  /**
   * Gets the year.
   *
   * @return the year
   */
  default Integer getYear() {
    return null;
  }

  /**
   * Gets the total income for the month.
   *
   * @return the income amount
   */
  BigDecimal getIncome();

  /**
   * Gets the total expense for the month.
   *
   * @return the expense amount
   */
  BigDecimal getExpense();
}
