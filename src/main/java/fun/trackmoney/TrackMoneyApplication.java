package fun.trackmoney;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * The main entry point for the TrackMoney application.
 * This class uses the Spring Boot framework to
 * initialize and run the application.
 */
@SpringBootApplication
@EnableAsync
@EnableCaching
public class TrackMoneyApplication {
  /**
   * The main method which serves as the entry point of the application.
   * It bootstraps the Spring Boot application by calling
   * {@link SpringApplication#run(Class, String[])}.
   */
  public static void main(String[] args) {
    SpringApplication.run(TrackMoneyApplication.class, args);
  }
}
