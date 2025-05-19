package fun.trackmoney.goal.repository;

import fun.trackmoney.goal.entity.GoalsEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GoalsRepository extends JpaRepository<GoalsEntity, Integer> {
}
