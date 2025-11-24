package fun.trackmoney.pots.repository;

import fun.trackmoney.account.entity.AccountEntity;
import fun.trackmoney.pots.entity.PotsEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PotsRepository extends JpaRepository<PotsEntity, Long> {
  @Query("SELECT p FROM PotsEntity p WHERE p.account = :account")
  List<PotsEntity> findAllByAccount(@Param("account") AccountEntity account);

  @Query("SELECT p FROM PotsEntity p WHERE p.account = :account AND p.potId = :id")
  Optional<PotsEntity> findByIdAndAccount(@Param("id") Long id, @Param("account") AccountEntity account);

  @Modifying
  @Query("DELETE FROM PotsEntity p WHERE p.account = :account AND p.potId = :id")
  void deleteByIdAccount(@Param("id") Long id, @Param("account") AccountEntity account);
}
