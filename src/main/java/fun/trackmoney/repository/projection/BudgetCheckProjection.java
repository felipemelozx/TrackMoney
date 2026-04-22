package fun.trackmoney.repository.projection;

public interface BudgetCheckProjection {
  boolean getCategoryExists();

  Integer getTotalPercent();
}