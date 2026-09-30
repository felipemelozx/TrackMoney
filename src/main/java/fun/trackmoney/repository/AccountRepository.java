package fun.trackmoney.repository;

import fun.trackmoney.entity.AccountEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AccountRepository extends JpaRepository<AccountEntity, Integer> {
  @Query("SELECT a FROM AccountEntity a WHERE a.user.userId = :userId")
  List<AccountEntity> findAllByUserEmail(@Param("userId") UUID userId);

  @Query("SELECT a FROM AccountEntity a WHERE a.user.userId = :userId")
  Optional<AccountEntity> findDefaultAccountByUserId(@Param("userId") UUID userId);

  @Query("SELECT a FROM AccountEntity a WHERE a.accountId = :accountId AND a.user.userId = :userId")
  Optional<AccountEntity> findByAccountIdAndUserId(@Param("accountId") Integer accountId,
                                                   @Param("userId") UUID userId);

  @Query(value = """
      SELECT EXISTS (
        SELECT 1 FROM tb_transaction WHERE account_id = :accountId
        UNION ALL SELECT 1 FROM tb_budget WHERE account_id = :accountId
        UNION ALL SELECT 1 FROM tb_budget_history WHERE account_id = :accountId
        UNION ALL SELECT 1 FROM tb_pots WHERE account_id = :accountId
        UNION ALL SELECT 1 FROM tb_recurring WHERE account_id = :accountId
        UNION ALL SELECT 1 FROM tb_report WHERE account_id = :accountId
        UNION ALL SELECT 1 FROM tb_transfer
          WHERE from_account_id = :accountId OR to_account_id = :accountId
      )
      """, nativeQuery = true)
  boolean hasRelatedData(@Param("accountId") Integer accountId);

  @Modifying
  @Query(value = "DELETE FROM tb_report WHERE account_id = :accountId", nativeQuery = true)
  void deleteLegacyReportsByAccountId(@Param("accountId") Integer accountId);

  @Modifying
  @Query(value = "DELETE FROM tb_transfer WHERE from_account_id = :accountId OR to_account_id = :accountId",
      nativeQuery = true)
  void deleteLegacyTransfersByAccountId(@Param("accountId") Integer accountId);

  @Modifying
  @Query("DELETE FROM AccountEntity a WHERE a.accountId = :accountId")
  void deleteByAccountIdDirect(@Param("accountId") Integer accountId);
}
