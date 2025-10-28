package fun.trackmoney.transaction.repository;

import fun.trackmoney.account.entity.AccountEntity;
import fun.trackmoney.transaction.entity.TransactionEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface TransactionRepository extends JpaRepository<TransactionEntity, Integer> {

  @Query("SELECT a FROM TransactionEntity a WHERE a.account.accountId = :accountId")
  List<TransactionEntity> findAllByAccountId(@Param("accountId") Integer accountId);

  @Query("SELECT t FROM TransactionEntity t WHERE t.account.accountId = :accountId ORDER BY t.transactionDate DESC")
  Page<TransactionEntity> findAllByAccountId(@Param("accountId") Integer accountId, Pageable pageable);

  @Query("SELECT t FROM TransactionEntity t WHERE t.account = :account AND t.transactionId = :id")
  Optional<TransactionEntity> findByIdAndAccount(@Param("id") Integer id, @Param("account") AccountEntity account);

  @Query("DELETE FROM TransactionEntity t WHERE t.account = :account AND t.transactionId = :id")
  void deleteByIdAndAccountId(@Param("id") Integer id, @Param("account") AccountEntity account);
}
