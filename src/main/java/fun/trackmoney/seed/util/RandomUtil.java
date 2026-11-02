package fun.trackmoney.seed.util;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.DayOfWeek;
import java.util.concurrent.ThreadLocalRandom;

public final class RandomUtil {

  private RandomUtil() {}

  public static int randomInt(int min, int max) {
    return ThreadLocalRandom.current().nextInt(min, max + 1);
  }

  public static double randomDouble(double min, double max) {
    return ThreadLocalRandom.current().nextDouble(min, max);
  }

  public static BigDecimal randomBigDecimal(double min, double max) {
    double value = randomDouble(min, max);
    return BigDecimal.valueOf(value).setScale(2, RoundingMode.HALF_UP);
  }

  public static BigDecimal randomBigDecimalWithVariance(double min, double max, double variancePercent) {
    double base = randomDouble(min, max);
    double variance = base * variancePercent * (randomDouble(-1, 1));
    double finalValue = base + variance;
    return BigDecimal.valueOf(finalValue).setScale(2, RoundingMode.HALF_UP);
  }

  public static LocalDateTime randomDateInMonth(YearMonth month) {
    int day = randomInt(1, month.lengthOfMonth());
    int hour = randomInt(9, 18);
    int minute = randomInt(0, 59);
    return month.atDay(day).atTime(hour, minute);
  }

  public static LocalDateTime randomDateInMonthWithDistribution(YearMonth month) {
    double random = randomDouble(0, 1);
    int day;

    if (random < 0.4) {
      // Days 1-5 (after payday)
      day = randomInt(1, 5);
    } else if (random < 0.7) {
      // Random weekend day
      day = getRandomWeekendDay(month);
    } else {
      // Random other day
      day = randomInt(1, month.lengthOfMonth());
    }

    int hour = randomInt(9, 18);
    int minute = randomInt(0, 59);

    return month.atDay(day).atTime(hour, minute);
  }

  private static int getRandomWeekendDay(YearMonth month) {
    int day;
    int attempts = 0;
    do {
      day = randomInt(1, month.lengthOfMonth());
      attempts++;
      if (attempts > 100) {
        return day; // Fallback to avoid infinite loop
      }
    } while (!isWeekend(month.atDay(day).getDayOfWeek()));

    return day;
  }

  private static boolean isWeekend(DayOfWeek dayOfWeek) {
    return dayOfWeek == DayOfWeek.SATURDAY || dayOfWeek == DayOfWeek.SUNDAY;
  }

  public static <T> T randomFromList(T[] array) {
    return array[randomInt(0, array.length - 1)];
  }

  public static boolean randomBoolean(double probability) {
    return randomDouble(0, 1) < probability;
  }
}
