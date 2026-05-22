package fun.trackmoney.dto.budget;

public record GenerationResultDTO(
    int generatedCount,
    String reason
) {
  public static GenerationResultDTO success(int count) {
    return new GenerationResultDTO(count, null);
  }

  public static GenerationResultDTO alreadyExists() {
    return new GenerationResultDTO(0, "ALREADY_EXISTS");
  }

  public static GenerationResultDTO noTransactions() {
    return new GenerationResultDTO(0, "NO_TRANSACTIONS");
  }

  public static GenerationResultDTO currentMonthNotAllowed() {
    return new GenerationResultDTO(0, "CURRENT_MONTH_NOT_ALLOWED");
  }

  public boolean isSuccess() {
    return generatedCount > 0;
  }

  public boolean isAlreadyExists() {
    return "ALREADY_EXISTS".equals(reason);
  }

  public boolean isNoTransactions() {
    return "NO_TRANSACTIONS".equals(reason);
  }

  public boolean isCurrentMonthNotAllowed() {
    return "CURRENT_MONTH_NOT_ALLOWED".equals(reason);
  }
}
