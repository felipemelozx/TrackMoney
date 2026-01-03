package fun.trackmoney.recurring.repository;

import fun.trackmoney.recurring.entity.RecurringEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface RecurringRepository extends JpaRepository<RecurringEntity, Long> {

  @Query("""
        SELECT r
        FROM RecurringEntity r 
        WHERE r.nextDate <= :currentDate
      """)
  List<RecurringEntity> findByNextDateBefore(@Param("currentDate") LocalDateTime currentDate);

  @Query("SELECT r FROM RecurringEntity r WHERE r.id = :id AND r.account.id = :accountId")
  Optional<RecurringEntity> findByIdAndAccount(@Param("id") Long id, @Param("accountId") Integer accountId);

  @Query("SELECT r FROM RecurringEntity r WHERE r.account.accountId = :accountId")
  List<RecurringEntity> findAllByAccountId(@Param("accountId") Integer accountId);

  @Modifying
  @Query("DELETE FROM RecurringEntity r WHERE r.id = :id AND r.account.accountId = :accountId")
  void deleteByIdAndAccountId(Long id, Integer accountId);
}
