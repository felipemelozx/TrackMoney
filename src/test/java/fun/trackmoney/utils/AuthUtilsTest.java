package fun.trackmoney.utils;

import fun.trackmoney.user.entity.UserEntity;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class AuthUtilsTest {

  private final AuthUtils authUtils = new AuthUtils();

  @AfterEach
  void clearContext() {
    SecurityContextHolder.clearContext();
  }

  @Test
  void getCurrentUser_shouldReturnUserEntity() {
    UserEntity mockUser = new UserEntity();
    mockUser.setUserId(UUID.randomUUID());
    mockUser.setName("John Doe");
    mockUser.setEmail("john@example.com");

    UsernamePasswordAuthenticationToken authentication =
        new UsernamePasswordAuthenticationToken(mockUser, null, null);

    SecurityContext context = Mockito.mock(SecurityContext.class);
    Mockito.when(context.getAuthentication()).thenReturn(authentication);
    SecurityContextHolder.setContext(context);

    UserEntity result = authUtils.getCurrentUser();

    assertNotNull(result);
    assertEquals(mockUser.getUserId(), result.getUserId());
    assertEquals(mockUser.getEmail(), result.getEmail());
  }
}
