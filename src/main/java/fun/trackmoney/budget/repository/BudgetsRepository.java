package fun.trackmoney.budget.repository;

import fun.trackmoney.budget.entity.BudgetsEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BudgetsRepository extends JpaRepository<BudgetsEntity, Integer> {
  List<BudgetsEntity> findAllByAccountAccountId(Integer accountId);
}
