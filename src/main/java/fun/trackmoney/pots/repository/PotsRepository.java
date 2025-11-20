package fun.trackmoney.pots.repository;

import fun.trackmoney.account.entity.AccountEntity;
import fun.trackmoney.pots.entity.PotsEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PotsRepository extends JpaRepository<PotsEntity, Long> {
  @Query("SELECT p FROM PotsEntity p WHERE p.account = :account")
  List<PotsEntity> findAllPotsByAccountId(@Param("account") AccountEntity account);
}
