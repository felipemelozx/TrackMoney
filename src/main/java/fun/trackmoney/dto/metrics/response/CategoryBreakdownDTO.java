package fun.trackmoney.dto.metrics.response;

import java.math.BigDecimal;
import java.util.List;

/**
 * DTO for expense breakdown by category for a specific month.
 * Shows how expenses are distributed across different categories.
 */
public record CategoryBreakdownDTO(
    int year,
    int month,
    BigDecimal totalExpense,
    List<CategoryBreakdown> categories
) {

  /**
   * Represents expense data for a single category.
   */
  public record CategoryBreakdown(
      String categoryName,
      String color,
      BigDecimal amount,
      BigDecimal percentage
  ) {
  }
}
