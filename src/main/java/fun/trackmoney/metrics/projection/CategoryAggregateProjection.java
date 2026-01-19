package fun.trackmoney.metrics.projection;

import java.math.BigDecimal;

/**
 * Projection interface for category-aggregated transaction data.
 * Used by TransactionRepository to return expenses grouped by category.
 */
public interface CategoryAggregateProjection {

  /**
   * Gets the category ID.
   *
   * @return the category ID
   */
  Integer getCategoryId();

  /**
   * Gets the category name.
   *
   * @return the category name
   */
  String getCategoryName();

  /**
   * Gets the category color.
   *
   * @return the category color
   */
  String getColor();

  /**
   * Gets the total expense amount for this category.
   *
   * @return the expense amount
   */
  BigDecimal getAmount();
}
