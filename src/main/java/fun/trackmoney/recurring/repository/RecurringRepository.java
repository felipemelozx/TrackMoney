package fun.trackmoney.recurring.repository;

import fun.trackmoney.account.entity.AccountEntity;
import fun.trackmoney.recurring.entity.RecurringEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface RecurringRepository extends JpaRepository<RecurringEntity, Long> {

  @Query(
      value = "SELECT * FROM tb_recurring r WHERE DATE(r.next_date) <= CURRENT_DATE",
      nativeQuery = true
  )
  List<RecurringEntity> findByNextDateBefore();

  @Query("SELECT r FROM RecurringEntity r WHERE r.id = :id AND r.account.id = :accountId")
  Optional<RecurringEntity> findByIdAndAccount(@Param("id") Long id, @Param("accountId") Integer accountId);
}
