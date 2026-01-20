package fun.trackmoney.metrics.dto.response;

import java.math.BigDecimal;
import java.util.List;

/**
 * DTO for monthly income vs expense summary.
 * Contains aggregated financial data grouped by month for a specific year.
 */
public record MonthlySummaryDTO(
    List<MonthSummary> months
) {

  /**
   * Represents aggregated financial data for a single month.
   */
  public record MonthSummary(
      int year,
      int month,
      BigDecimal income,
      BigDecimal expense,
      BigDecimal balance
  ) {
  }
}
