package fun.trackmoney.user.repository;

import fun.trackmoney.user.entity.UserEntity;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository interface for accessing and managing
 * {@link UserEntity} instances in the database.
 */
public interface UserRepository extends JpaRepository<UserEntity, UUID> {
}
