package fun.trackmoney.transaction.repository;

import fun.trackmoney.entity.AccountEntity;
import fun.trackmoney.metrics.projection.CategoryAggregateProjection;
import fun.trackmoney.metrics.projection.MonthAggregateProjection;
import fun.trackmoney.entity.TransactionEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.NativeQuery;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface TransactionRepository extends JpaRepository<TransactionEntity, Integer> {

  @Query("SELECT a FROM TransactionEntity a WHERE a.account.accountId = :accountId ORDER BY a.transactionDate DESC")
  List<TransactionEntity> findAllByAccountId(@Param("accountId") Integer accountId);

  @Query("SELECT t FROM TransactionEntity t WHERE t.account.accountId = :accountId ORDER BY t.transactionDate DESC")
  Page<TransactionEntity> findAllByAccountId(@Param("accountId") Integer accountId, Pageable pageable);


  @Query("""
          SELECT t FROM TransactionEntity t
          WHERE t.account.accountId = :accountId
            AND (:transactionName IS NULL OR LOWER(t.transactionName) LIKE LOWER(CONCAT(:transactionName, '%')))
            AND (:categoryId IS NULL OR t.category.categoryId = :categoryId)
             AND (t.transactionDate >= COALESCE(:startDate, t.transactionDate))
            AND (t.transactionDate <= COALESCE(:endDate, t.transactionDate))
          ORDER BY t.transactionDate DESC
      """)
  Page<TransactionEntity> findAllByFilters(
      @Param("accountId") Integer accountId,
      @Param("transactionName") String transactionName,
      @Param("categoryId") Integer categoryId,
      @Param("startDate") LocalDateTime startDate,
      @Param("endDate") LocalDateTime endDate,
      Pageable pageable
  );

  @Query("SELECT t FROM TransactionEntity t WHERE t.account = :account AND t.transactionId = :id")
  Optional<TransactionEntity> findByIdAndAccount(@Param("id") Integer id, @Param("account") AccountEntity account);

  @Modifying
  @Query("DELETE FROM TransactionEntity t WHERE t.account = :account AND t.transactionId = :id")
  void deleteByIdAndAccountId(@Param("id") Integer id, @Param("account") AccountEntity account);

  @NativeQuery
  @Query(
      value = """
        SELECT * FROM (
            SELECT 
                t.*, 
                ROW_NUMBER() OVER (PARTITION BY t.category_id 
                                   ORDER BY t.transaction_date DESC) AS rn
            FROM tb_transaction t
            WHERE t.account_id = :accountId
        ) x
        WHERE x.rn <= 5
        ORDER BY x.category_id, x.transaction_date DESC
        """,
      nativeQuery = true
  )
  List<TransactionEntity> findLast5TransactionsPerCategory(@Param("accountId") Integer accountId);

  @Query("SELECT t FROM TransactionEntity t " +
      "WHERE t.transactionDate >= :startDate " +
      "AND t.transactionDate <= :endDate")
  List<TransactionEntity> findAllBetweenDates(LocalDateTime startDate, LocalDateTime endDate);

  @Query("""
      SELECT t FROM TransactionEntity t
      WHERE t.account.accountId = :accountId
        AND t.transactionDate >= :startDate
        AND t.transactionDate <= :endDate
      ORDER BY t.transactionDate ASC
      """)
  List<TransactionEntity> findAllByAccountIdAndDateRange(
      @Param("accountId") Integer accountId,
      @Param("startDate") LocalDateTime startDate,
      @Param("endDate") LocalDateTime endDate
  );

  @Query("""
      SELECT COALESCE(SUM(t.amount), 0)
      FROM TransactionEntity t
      WHERE t.account.accountId = :accountId
        AND t.category.categoryId = :categoryId
        AND t.transactionType = 'EXPENSE'
        AND t.transactionDate >= :startDate
        AND t.transactionDate <= :endDate
      """)
  BigDecimal sumExpensesByCategoryAndDateRange(
      @Param("accountId") Integer accountId,
      @Param("categoryId") Integer categoryId,
      @Param("startDate") LocalDateTime startDate,
      @Param("endDate") LocalDateTime endDate
  );

  @Query("""
      SELECT t FROM TransactionEntity t
      WHERE t.account.accountId = :accountId
        AND t.category.categoryId = :categoryId
        AND t.transactionType = 'EXPENSE'
        AND t.transactionDate >= :startDate
        AND t.transactionDate <= :endDate
      ORDER BY t.transactionDate DESC
      """)
  List<TransactionEntity> findExpensesByCategoryAndDateRange(
      @Param("accountId") Integer accountId,
      @Param("categoryId") Integer categoryId,
      @Param("startDate") LocalDateTime startDate,
      @Param("endDate") LocalDateTime endDate
  );

  @Query("""
      SELECT COUNT(t) > 0
      FROM TransactionEntity t
      WHERE t.account.accountId = :accountId
        AND t.transactionDate >= :startDate
        AND t.transactionDate <= :endDate
      """)
  boolean existsByAccountIdAndDateRange(
      @Param("accountId") Integer accountId,
      @Param("startDate") LocalDateTime startDate,
      @Param("endDate") LocalDateTime endDate
  );

  // ===== Metrics Queries =====

  /**
   * Aggregates income and expenses by month for a given year.
   * Uses native query for better performance with CASE expressions.
   */
  @Query(
      value = """
          SELECT EXTRACT(MONTH FROM t.transaction_date) as month,
                 EXTRACT(YEAR FROM t.transaction_date) as year,
                 COALESCE(SUM(CASE WHEN t.transaction_type = 'INCOME' THEN t.amount ELSE 0 END), 0) as income,
                 COALESCE(SUM(CASE WHEN t.transaction_type = 'EXPENSE' THEN t.amount ELSE 0 END), 0) as expense
          FROM tb_transaction t
          WHERE t.account_id = :accountId
            AND EXTRACT(YEAR FROM t.transaction_date) = :year
            AND (:categoryId IS NULL OR t.category_id = :categoryId)
          GROUP BY EXTRACT(MONTH FROM t.transaction_date), EXTRACT(YEAR FROM t.transaction_date)
          ORDER BY EXTRACT(MONTH FROM t.transaction_date)
          """,
      nativeQuery = true
  )
  List<MonthAggregateProjection> sumByMonthAndType(
      @Param("accountId") Integer accountId,
      @Param("year") int year,
      @Param("categoryId") Integer categoryId
  );

  /**
   * Aggregates income and expenses by month for a given date range.
   * Uses native query for better performance with CASE expressions.
   */
  @Query(
      value = """
          SELECT EXTRACT(MONTH FROM t.transaction_date) as month,
                 EXTRACT(YEAR FROM t.transaction_date) as year,
                 COALESCE(SUM(CASE WHEN t.transaction_type = 'INCOME' THEN t.amount ELSE 0 END), 0) as income,
                 COALESCE(SUM(CASE WHEN t.transaction_type = 'EXPENSE' THEN t.amount ELSE 0 END), 0) as expense
          FROM tb_transaction t
          WHERE t.account_id = :accountId
            AND t.transaction_date >= :startDate
            AND t.transaction_date <= :endDate
            AND (:categoryId IS NULL OR t.category_id = :categoryId)
          GROUP BY EXTRACT(MONTH FROM t.transaction_date), EXTRACT(YEAR FROM t.transaction_date)
          ORDER BY EXTRACT(YEAR FROM t.transaction_date), EXTRACT(MONTH FROM t.transaction_date)
          """,
      nativeQuery = true
  )
  List<MonthAggregateProjection> sumByMonthAndTypeForDateRange(
      @Param("accountId") Integer accountId,
      @Param("startDate") LocalDateTime startDate,
      @Param("endDate") LocalDateTime endDate,
      @Param("categoryId") Integer categoryId
  );

  /**
   * Aggregates expenses by category for a specific month.
   * Uses native query for correct projection handling.
   */
  @Query(
      value = """
          SELECT c.category_id as categoryId,
                 c.name as categoryName,
                 c.color as color,
                 COALESCE(SUM(t.amount), 0) as amount
          FROM tb_category c
          LEFT JOIN tb_transaction t ON t.category_id = c.category_id
              AND t.account_id = :accountId
              AND t.transaction_type = 'EXPENSE'
              AND EXTRACT(YEAR FROM t.transaction_date) = :year
              AND EXTRACT(MONTH FROM t.transaction_date) = :month
              AND (:categoryId IS NULL OR c.category_id = :categoryId)
          GROUP BY c.category_id, c.name, c.color
          ORDER BY COALESCE(SUM(t.amount), 0) DESC
          """,
      nativeQuery = true
  )
  List<CategoryAggregateProjection> sumByCategoryForMonth(
      @Param("accountId") Integer accountId,
      @Param("year") int year,
      @Param("month") int month,
      @Param("categoryId") Integer categoryId
  );

  /**
   * Aggregates expenses by category for a date range.
   * Uses native query for correct projection handling.
   */
  @Query(
      value = """
          SELECT c.category_id as categoryId,
                 c.name as categoryName,
                 c.color as color,
                 COALESCE(SUM(t.amount), 0) as amount
          FROM tb_category c
          LEFT JOIN tb_transaction t ON t.category_id = c.category_id
              AND t.account_id = :accountId
              AND t.transaction_type = 'EXPENSE'
              AND t.transaction_date >= :startDate
              AND t.transaction_date <= :endDate
              AND (:categoryId IS NULL OR c.category_id = :categoryId)
          GROUP BY c.category_id, c.name, c.color
          ORDER BY COALESCE(SUM(t.amount), 0) DESC
          """,
      nativeQuery = true
  )
  List<CategoryAggregateProjection> sumByCategoryForDateRange(
      @Param("accountId") Integer accountId,
      @Param("startDate") LocalDateTime startDate,
      @Param("endDate") LocalDateTime endDate,
      @Param("categoryId") Integer categoryId
  );
}
