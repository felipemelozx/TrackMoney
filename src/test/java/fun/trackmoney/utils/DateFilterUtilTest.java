package fun.trackmoney.utils;

import fun.trackmoney.transaction.enums.DateFilterEnum;
import org.junit.jupiter.api.Test;

import java.time.*;
import java.time.temporal.TemporalAdjusters;

import static org.junit.jupiter.api.Assertions.*;

class DateFilterUtilTest {

    @Test
    void testToday() {
        LocalDate now = LocalDate.now();
        DateFilterUtil.DateRange range = DateFilterUtil.getDateRange(DateFilterEnum.TODAY);

        assertEquals(now.atStartOfDay(), range.start());
        assertEquals(now.atTime(23, 59, 59), range.end());
    }

    @Test
    void testYesterday() {
        LocalDate yesterday = LocalDate.now().minusDays(1);
        DateFilterUtil.DateRange range = DateFilterUtil.getDateRange(DateFilterEnum.YESTERDAY);

        assertEquals(yesterday.atStartOfDay(), range.start());
        assertEquals(yesterday.atTime(23, 59, 59), range.end());
    }

    @Test
    void testThisWeek() {
        LocalDate now = LocalDate.now();
        LocalDate expectedStart = now.with(DayOfWeek.MONDAY);
        LocalDate expectedEnd = now.with(DayOfWeek.SUNDAY);

        DateFilterUtil.DateRange range = DateFilterUtil.getDateRange(DateFilterEnum.THIS_WEEK);

        assertEquals(expectedStart.atStartOfDay(), range.start());
        assertEquals(expectedEnd.atTime(23, 59, 59), range.end());
    }

    @Test
    void testThisMonth() {
        LocalDate now = LocalDate.now();
        LocalDate expectedStart = now.with(TemporalAdjusters.firstDayOfMonth());
        LocalDate expectedEnd = now.with(TemporalAdjusters.lastDayOfMonth());

        DateFilterUtil.DateRange range = DateFilterUtil.getDateRange(DateFilterEnum.THIS_MONTH);

        assertEquals(expectedStart.atStartOfDay(), range.start());
        assertEquals(expectedEnd.atTime(23, 59, 59), range.end());
    }

    @Test
    void testLastMonth() {
        LocalDate lastMonth = LocalDate.now().minusMonths(1);
        LocalDate expectedStart = lastMonth.with(TemporalAdjusters.firstDayOfMonth());
        LocalDate expectedEnd = lastMonth.with(TemporalAdjusters.lastDayOfMonth());

        DateFilterUtil.DateRange range = DateFilterUtil.getDateRange(DateFilterEnum.LAST_MONTH);

        assertEquals(expectedStart.atStartOfDay(), range.start());
        assertEquals(expectedEnd.atTime(23, 59, 59), range.end());
    }
}
