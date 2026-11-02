package fun.trackmoney.seed.service.model;

public record SeedDataSummary(
    int userCount,
    int accountCount,
    int budgetCount,
    int potCount,
    int recurringCount,
    int transactionCount
) {
  @Override
  public String toString() {
    return "SeedDataSummary[userCount=%s, accountCount=%s, budgetCount=%s, "
        + "potCount=%s, recurringCount=%s, transactionCount=%s]".formatted(
            userCount,
            accountCount,
            budgetCount,
            potCount,
            recurringCount,
            transactionCount
        );
  }
}
