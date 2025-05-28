package fun.trackmoney.transaction.repository;

import fun.trackmoney.transaction.entity.TransactionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TransactionRepository extends JpaRepository<TransactionEntity, Integer> {
  @Query("SELECT a FROM TransactionEntity a WHERE a.account.accountId = :accountId")
  List<TransactionEntity> findAllByUserEmail(@Param("accountId") Integer accountId);
}
