package fun.trackmoney.pots.repository;

import fun.trackmoney.pots.entity.PotsEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PotsRepository extends JpaRepository<PotsEntity, Long> {
}
