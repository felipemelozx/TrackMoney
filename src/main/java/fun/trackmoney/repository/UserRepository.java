package fun.trackmoney.repository;

import fun.trackmoney.entity.UserEntity;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserRepository extends JpaRepository<UserEntity, UUID> {
  Optional<UserEntity> findByEmail(String email);

  @Modifying
  @Query("UPDATE UserEntity u SET u.account = null WHERE u.userId = :userId")
  void clearAccountReference(@Param("userId") UUID userId);

  @Modifying
  @Query("DELETE FROM UserEntity u WHERE u.userId = :userId")
  void deleteByUserIdDirect(@Param("userId") UUID userId);
}
