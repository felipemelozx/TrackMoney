package fun.trackmoney.auth.infra.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

  private final SecurityFilterConfig securityFilter;

  public SecurityConfig(SecurityFilterConfig securityFilter) {
    this.securityFilter = securityFilter;
  }

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http
        .csrf(csrf -> csrf.disable())
        .cors(Customizer.withDefaults())
        .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .authorizeHttpRequests(authorize -> authorize
            .requestMatchers(HttpMethod.POST, "/auth/login").permitAll()
            .requestMatchers(HttpMethod.POST, "/auth/register").permitAll()
            .requestMatchers(HttpMethod.POST, "/auth/forgot-password/**").permitAll()
            .requestMatchers(HttpMethod.POST, "/auth/reset-password").hasAuthority("RESET_PASSWORD")
            .requestMatchers(HttpMethod.POST, "/auth/resend-verification-email").hasAuthority("USER_UNVERIFIED")
            .requestMatchers(HttpMethod.POST, "/auth/verify-email/**").hasAuthority("USER_UNVERIFIED")
            .requestMatchers(HttpMethod.GET, "/auth/refresh").hasAuthority("REFRESH")
            .requestMatchers("/actuator/health").permitAll()
            .requestMatchers(HttpMethod.GET, "/auth/verify").hasAnyAuthority("USER_UNVERIFIED",
                "RESET_PASSWORD",
                "USER_ROLES")
            .requestMatchers(
                "/swagger-ui.html",
                "/swagger-ui/**",
                "/v3/api-docs/**"
            ).denyAll()
            .anyRequest().hasAuthority("USER_ROLES")
        )
        .addFilterBefore(securityFilter, UsernamePasswordAuthenticationFilter.class);
    return http.build();
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  public AuthenticationManager authenticationManager(
      AuthenticationConfiguration authenticationConfiguration) throws Exception {
    return authenticationConfiguration.getAuthenticationManager();
  }
}
