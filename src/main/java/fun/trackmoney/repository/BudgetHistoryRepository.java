package fun.trackmoney.repository;

import fun.trackmoney.entity.BudgetHistoryEntity;
import fun.trackmoney.entity.BudgetsEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface BudgetHistoryRepository extends JpaRepository<BudgetHistoryEntity, Integer> {

  Optional<BudgetHistoryEntity> findByBudgetAndReferenceMonthAndReferenceYear(
      BudgetsEntity budget,
      Short month,
      Integer year
  );

  List<BudgetHistoryEntity> findByAccountAccountIdOrderByReferenceYearDescReferenceMonthDesc(
      Integer accountId
  );

  @Query("""
      SELECT bh FROM BudgetHistoryEntity bh
      WHERE bh.account.accountId = :accountId
        AND bh.referenceYear >= :startYear
        AND bh.referenceMonth >= :startMonth
        AND bh.referenceYear <= :endYear
        AND bh.referenceMonth <= :endMonth
      ORDER BY bh.referenceYear DESC, bh.referenceMonth DESC
      """)
  List<BudgetHistoryEntity> findByAccountAndDateRange(
      @Param("accountId") Integer accountId,
      @Param("startYear") Integer startYear,
      @Param("startMonth") Short startMonth,
      @Param("endYear") Integer endYear,
      @Param("endMonth") Short endMonth
  );

  @Query("""
      SELECT COUNT(bh) > 0 FROM BudgetHistoryEntity bh
      WHERE bh.account.accountId = :accountId
        AND bh.referenceMonth = :month
        AND bh.referenceYear = :year
      """)
  boolean existsHistoryForAccountAndMonth(
      @Param("accountId") Integer accountId,
      @Param("month") Short month,
      @Param("year") Integer year
  );

  @Query("""
      SELECT (SELECT COUNT(b) FROM BudgetsEntity b WHERE b.account.accountId = :accountId) = COUNT(bh)
      FROM BudgetHistoryEntity bh
      WHERE bh.account.accountId = :accountId
        AND bh.referenceMonth = :month
        AND bh.referenceYear = :year
      """)
  boolean hasAllBudgetHistoriesForMonth(
      @Param("accountId") Integer accountId,
      @Param("month") Short month,
      @Param("year") Integer year
  );

  @Query("""
      SELECT bh FROM BudgetHistoryEntity bh
      WHERE bh.account.accountId = :accountId
        AND (:categoryId IS NULL OR bh.category.categoryId = :categoryId)
      ORDER BY bh.referenceYear DESC, bh.referenceMonth DESC
      """)
  List<BudgetHistoryEntity> findByAccountAndOptionalCategoryId(
      @Param("accountId") Integer accountId,
      @Param("categoryId") Integer categoryId
  );

  @Query("""
      SELECT bh FROM BudgetHistoryEntity bh
      WHERE bh.account.accountId = :accountId
        AND bh.referenceYear >= :startYear
        AND bh.referenceMonth >= :startMonth
        AND bh.referenceYear <= :endYear
        AND bh.referenceMonth <= :endMonth
        AND (:categoryId IS NULL OR bh.category.categoryId = :categoryId)
      ORDER BY bh.referenceYear DESC, bh.referenceMonth DESC
      """)
  List<BudgetHistoryEntity> findByAccountAndDateRangeAndOptionalCategoryId(
      @Param("accountId") Integer accountId,
      @Param("startYear") Integer startYear,
      @Param("startMonth") Short startMonth,
      @Param("endYear") Integer endYear,
      @Param("endMonth") Short endMonth,
      @Param("categoryId") Integer categoryId
  );

  void deleteByBudgetBudgetId(Integer budgetId);

  // ===== Metrics Queries =====

  /**
   * Finds all budget histories for an account and year.
   * Used for budget performance metrics.
   *
   * @param accountId the account ID
   * @param year      the year
   * @return list of budget histories
   */
  @Query("""
      SELECT bh FROM BudgetHistoryEntity bh
      WHERE bh.account.accountId = :accountId
        AND bh.referenceYear = :year
        AND (:categoryId IS NULL OR bh.category.categoryId = :categoryId)
      ORDER BY bh.referenceMonth DESC
      """)
  List<BudgetHistoryEntity> findByAccountAndYear(
      @Param("accountId") Integer accountId,
      @Param("year") Integer year,
      @Param("categoryId") Integer categoryId
  );

  /**
   * Finds all budget histories for an account and date range.
   * Used for budget performance metrics with flexible date ranges.
   *
   * @param accountId  the account ID
   * @param startYear  the start year
   * @param startMonth the start month
   * @param endYear    the end year
   * @param endMonth   the end month
   * @return list of budget histories
   */
  @Query("""
      SELECT bh FROM BudgetHistoryEntity bh
      WHERE bh.account.accountId = :accountId
        AND (
          (bh.referenceYear = :startYear AND bh.referenceMonth >= :startMonth) OR
          (bh.referenceYear > :startYear AND bh.referenceYear < :endYear) OR
          (bh.referenceYear = :endYear AND bh.referenceMonth <= :endMonth)
        )
        AND (:categoryId IS NULL OR bh.category.categoryId = :categoryId)
      ORDER BY bh.referenceYear DESC, bh.referenceMonth DESC
      """)
  List<BudgetHistoryEntity> findByAccountAndYearMonthRange(
      @Param("accountId") Integer accountId,
      @Param("startYear") Integer startYear,
      @Param("startMonth") Short startMonth,
      @Param("endYear") Integer endYear,
      @Param("endMonth") Short endMonth,
      @Param("categoryId") Integer categoryId
  );

  /**
   * Finds all budget histories for an account, year, and month.
   * Used for budget overview metrics.
   *
   * @param accountId the account ID
   * @param year      the year
   * @param month     the month
   * @return list of budget histories
   */
  @Query("""
      SELECT bh FROM BudgetHistoryEntity bh
      WHERE bh.account.accountId = :accountId
        AND bh.referenceYear = :year
        AND bh.referenceMonth = :month
        AND (:categoryId IS NULL OR bh.category.categoryId = :categoryId)
      ORDER BY bh.category.name ASC
      """)
  List<BudgetHistoryEntity> findByAccountAndYearAndMonth(
      @Param("accountId") Integer accountId,
      @Param("year") Integer year,
      @Param("month") Short month,
      @Param("categoryId") Integer categoryId
  );
}
