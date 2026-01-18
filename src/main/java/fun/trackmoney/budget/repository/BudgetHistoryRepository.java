package fun.trackmoney.budget.repository;

import fun.trackmoney.budget.entity.BudgetHistoryEntity;
import fun.trackmoney.budget.entity.BudgetsEntity;
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
      @Param("categoryId") Long categoryId
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
      @Param("categoryId") Long categoryId
  );

  void deleteByBudgetBudgetId(Integer budgetId);
}
