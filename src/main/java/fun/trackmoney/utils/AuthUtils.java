package fun.trackmoney.utils;

import fun.trackmoney.entity.UserEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class AuthUtils {

  public UserEntity getCurrentUser() {
    return (UserEntity) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
  }
}
