package fun.trackmoney.dto.metrics.response;

import java.math.BigDecimal;
import java.util.List;

/**
 * DTO for budget performance history by year.
 * Contains budget vs actual spending data grouped by month and category.
 */
public record BudgetPerformanceDTO(
    int year,
    List<BudgetMonthPerformance> monthlyPerformance
) {

  /**
   * Represents budget performance data for a single month.
   */
  public record BudgetMonthPerformance(
      int month,
      List<CategoryBudgetPerformance> categories
  ) {
  }

  /**
   * Represents budget performance data for a single category.
   */
  public record CategoryBudgetPerformance(
      String categoryName,
      String color,
      BigDecimal targetAmount,
      BigDecimal spentAmount,
      BigDecimal remainingAmount,
      String status
  ) {
  }
}
