package fun.trackmoney.config.startup;

import fun.trackmoney.service.BudgetHistoryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
public class BudgetHistoryStartupRunner implements ApplicationRunner {

  private static final Logger LOG = LoggerFactory.getLogger(BudgetHistoryStartupRunner.class);

  private final BudgetHistoryService budgetHistoryService;

  public BudgetHistoryStartupRunner(BudgetHistoryService budgetHistoryService) {
    this.budgetHistoryService = budgetHistoryService;
  }

  @Override
  public void run(ApplicationArguments args) {
    try {
      budgetHistoryService.recoverMissingHistory();
    } catch (Exception e) {
      LOG.error("Error recovering budget history on startup: {}", e.getMessage(), e);
    }
  }
}
