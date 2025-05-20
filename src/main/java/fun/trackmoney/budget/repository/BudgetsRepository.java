package fun.trackmoney.budget.repository;

import fun.trackmoney.budget.entity.BudgetsEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BudgetsRepository extends JpaRepository<BudgetsEntity, Integer> {
}
