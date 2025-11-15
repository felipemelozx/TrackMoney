package fun.trackmoney.transaction.repository;

import fun.trackmoney.account.entity.AccountEntity;
import fun.trackmoney.transaction.entity.TransactionEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface TransactionRepository extends JpaRepository<TransactionEntity, Integer> {

  @Query("SELECT a FROM TransactionEntity a WHERE a.account.accountId = :accountId")
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
      @Param("categoryId") Long categoryId,
      @Param("startDate") LocalDateTime startDate,
      @Param("endDate") LocalDateTime endDate,
      Pageable pageable
  );

  @Query("SELECT t FROM TransactionEntity t WHERE t.account = :account AND t.transactionId = :id")
  Optional<TransactionEntity> findByIdAndAccount(@Param("id") Integer id, @Param("account") AccountEntity account);

  @Modifying
  @Query("DELETE FROM TransactionEntity t WHERE t.account = :account AND t.transactionId = :id")
  void deleteByIdAndAccountId(@Param("id") Integer id, @Param("account") AccountEntity account);
}
