package fun.trackmoney.utils;

import fun.trackmoney.transaction.enums.DateFilterEnum;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;

public class DateFilterUtil {

  public static DateRange getDateRange(DateFilterEnum dateFilter) {
    LocalDate now = LocalDate.now();
    LocalDateTime startDate;
    LocalDateTime endDate;

    switch (dateFilter) {
      case TODAY:
        startDate = now.atStartOfDay();
        endDate = now.atTime(23, 59, 59);
        break;

      case YESTERDAY:
        LocalDate yesterday = now.minusDays(1);
        startDate = yesterday.atStartOfDay();
        endDate = yesterday.atTime(23, 59, 59);
        break;

      case THIS_WEEK:
        LocalDate weekStart = now.with(DayOfWeek.MONDAY);
        LocalDate weekEnd = now.with(DayOfWeek.SUNDAY);
        startDate = weekStart.atStartOfDay();
        endDate = weekEnd.atTime(23, 59, 59);
        break;

      case THIS_MONTH:
        LocalDate monthStart = now.with(TemporalAdjusters.firstDayOfMonth());
        LocalDate monthEnd = now.with(TemporalAdjusters.lastDayOfMonth());
        startDate = monthStart.atStartOfDay();
        endDate = monthEnd.atTime(23, 59, 59);
        break;

      case LAST_MONTH:
        LocalDate lastMonth = now.minusMonths(1);
        LocalDate lastMonthStart = lastMonth.with(TemporalAdjusters.firstDayOfMonth());
        LocalDate lastMonthEnd = lastMonth.with(TemporalAdjusters.lastDayOfMonth());
        startDate = lastMonthStart.atStartOfDay();
        endDate = lastMonthEnd.atTime(23, 59, 59);
        break;

      default:
        return null;
    }

    return new DateRange(startDate, endDate);
  }

  public static record DateRange(LocalDateTime start, LocalDateTime end) {}
}
