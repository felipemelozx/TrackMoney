package fun.trackmoney.recurring.repository;

import fun.trackmoney.recurring.entity.RecurringEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface RecurringRepository extends JpaRepository<RecurringEntity, Long> {

  @Query(
      value = "SELECT * FROM tb_recurring r WHERE DATE(r.next_date) <= CURRENT_DATE",
      nativeQuery = true
  )
  List<RecurringEntity> findByNextDateBefore();

}
