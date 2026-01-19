package fun.trackmoney.metrics.dto.response;

import java.math.BigDecimal;

/**
 * DTO for dashboard overview with financial metrics.
 * Provides a high-level snapshot of the user's financial status.
 * Works for both current month and custom date ranges.
 */
public record DashboardOverviewDTO(
    // Financial totals
    BigDecimal totalIncome,
    BigDecimal totalExpense,
    BigDecimal totalBalance,

    // Budget status
    int exceededBudgetsCount,
    int totalBudgetsCount,

    // Top category
    String topExpenseCategory,
    BigDecimal topExpenseAmount,

    // Period metadata
    int periodStartMonth,
    int periodStartYear
) {
}
