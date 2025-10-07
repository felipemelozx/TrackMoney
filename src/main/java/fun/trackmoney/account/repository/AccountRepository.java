package fun.trackmoney.account.repository;

import fun.trackmoney.account.entity.AccountEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AccountRepository extends JpaRepository<AccountEntity, Integer> {
  @Query("SELECT a FROM AccountEntity a WHERE a.user.userId = :userId")
  List<AccountEntity> findAllByUserEmail(@Param("userId") UUID userId);

  @Query("SELECT a FROM AccountEntity a WHERE a.user.userId = :userId AND a.isAccountDefault = true")
  Optional<AccountEntity> findDefaultAccountByUserId(@Param("userId") UUID userId);
}
