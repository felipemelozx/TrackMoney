package fun.trackmoney.budget.repository;

import fun.trackmoney.account.entity.AccountEntity;
import fun.trackmoney.budget.entity.BudgetsEntity;
import fun.trackmoney.category.entity.CategoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BudgetsRepository extends JpaRepository<BudgetsEntity, Integer> {
  List<BudgetsEntity> findAllByAccountAccountId(Integer accountId);

  @Query("""
    SELECT
        (COUNT(b) FILTER (WHERE b.category = :category) > 0) AS categoryExists,
        COALESCE(SUM(b.percent), 0) AS totalPercent
    FROM BudgetsEntity b
    WHERE b.account = :account
""")
  BudgetCheckProjection checkBudget(@Param("account") AccountEntity account,
                                    @Param("category") CategoryEntity category);

}
