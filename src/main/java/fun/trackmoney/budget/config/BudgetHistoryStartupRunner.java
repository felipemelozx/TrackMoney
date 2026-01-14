package fun.trackmoney.budget.config;

import fun.trackmoney.budget.service.BudgetHistoryService;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
public class BudgetHistoryStartupRunner implements ApplicationRunner {

  private final BudgetHistoryService budgetHistoryService;

  public BudgetHistoryStartupRunner(BudgetHistoryService budgetHistoryService) {
    this.budgetHistoryService = budgetHistoryService;
  }

  @Override
  public void run(ApplicationArguments args) {
    try {
      budgetHistoryService.recoverMissingHistory();
    } catch (Exception e) {
      System.err.println("Error recovering budget history on startup: " + e.getMessage());
    }
  }
}
