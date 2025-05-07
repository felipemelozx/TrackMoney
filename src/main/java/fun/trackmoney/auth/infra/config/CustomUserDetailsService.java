package fun.trackmoney.auth.infra.config;

import fun.trackmoney.user.entity.UserEntity;
import fun.trackmoney.user.repository.UserRepository;
import java.util.Collections;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

@Component
public class CustomUserDetailsService implements UserDetailsService {

  private final UserRepository repository;

  public CustomUserDetailsService(UserRepository repository) {
    this.repository = repository;
  }

  @Override
  public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
    UserEntity user = repository.findByEmail(email)
        .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));
    return new User(user.getEmail(), user.getPassword(), Collections.emptyList());
  }
}