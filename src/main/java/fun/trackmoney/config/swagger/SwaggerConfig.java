package fun.trackmoney.config.swagger;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

  @Bean
  public OpenAPI customOpenAPI() {
    return new OpenAPI()
        .info(new Info()
            .title("TrackMoney API Manager Finance")
            .version("v1")
            .description("This is the backend part of a full-stack financial management application, " +
                "designed to handle essential features like user authentication, CRUD operations " +
                "for financial resources (transactions, budgets, recurring accounts), and more. " +
                "The backend is built using Java 17, Spring Boot, Spring Security, and follows " +
                "the Clean Architecture principles. It is integrated with a PostgreSQL database " +
                "and includes a CI/CD pipeline for continuous integration and deployment."))
            .addSecurityItem(new SecurityRequirement().addList("bearerAuth"))
            .components(new Components()
            .addSecuritySchemes("bearerAuth",
                new SecurityScheme()
                    .type(SecurityScheme.Type.HTTP)
                    .scheme("bearer")
                    .bearerFormat("JWT")));
  }
}
