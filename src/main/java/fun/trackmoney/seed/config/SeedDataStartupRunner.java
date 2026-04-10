package fun.trackmoney.seed.config;

import fun.trackmoney.service.SeedDataService;
import fun.trackmoney.seed.service.model.SeedDataSummary;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Profile("dev")
@Order(1)
public class SeedDataStartupRunner implements ApplicationRunner {

  private static final Logger LOG = LoggerFactory.getLogger(SeedDataStartupRunner.class);

  private final SeedDataService seedDataService;

  public SeedDataStartupRunner(SeedDataService seedDataService) {
    this.seedDataService = seedDataService;
  }

  @Override
  public void run(ApplicationArguments args) {
    if (seedDataService.seedDataAlreadyExists()) {
      LOG.info("Seed data already exists. Skipping generation.");
      return;
    }

    try {
      SeedDataSummary summary = seedDataService.generateSeedData();
      LOG.info("Seed data generated successfully: {}", summary);
    } catch (Exception e) {
      LOG.error("Failed to generate seed data: {}", e.getMessage(), e);
    }
  }
}
