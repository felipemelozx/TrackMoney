package fun.trackmoney.service;

import fun.trackmoney.entity.AccountEntity;
import fun.trackmoney.entity.UserEntity;
import fun.trackmoney.repository.UserRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@Transactional
class UserChangePasswordIntegrationTest {

  @Autowired
  private UserService userService;

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private PasswordEncoder passwordEncoder;

  @Autowired
  private EntityManager entityManager;

  @Test
  void changePassword_shouldPersistNewEncodedPassword_whenCurrentPasswordIsCorrect() {
    UserEntity user = new UserEntity();
    user.setName("Integration Test");
    user.setEmail("it-" + UUID.randomUUID() + "@example.com");
    user.setPassword(passwordEncoder.encode("OldPassword123#"));
    user.activate();

    AccountEntity account = new AccountEntity()
        .setName("Conta Teste")
        .setBalance(BigDecimal.ZERO)
        .setUser(user);
    user.setAccount(account);

    userRepository.save(user);

    entityManager.flush();
    entityManager.clear();

    boolean result = userService.changePassword(user, "OldPassword123#", "NewStrongPassword123#");

    entityManager.flush();
    entityManager.clear();

    assertTrue(result);

    UserEntity persisted = userRepository.findById(user.getUserId()).orElseThrow();
    assertTrue(passwordEncoder.matches("NewStrongPassword123#", persisted.getPassword()));
    assertFalse(passwordEncoder.matches("OldPassword123#", persisted.getPassword()));
  }

  @Test
  void changePassword_shouldNotChangePassword_whenCurrentPasswordIsIncorrect() {
    UserEntity user = new UserEntity();
    user.setName("Integration Test");
    user.setEmail("it-" + UUID.randomUUID() + "@example.com");
    user.setPassword(passwordEncoder.encode("OldPassword123#"));
    user.activate();

    AccountEntity account = new AccountEntity()
        .setName("Conta Teste")
        .setBalance(BigDecimal.ZERO)
        .setUser(user);
    user.setAccount(account);

    userRepository.save(user);

    entityManager.flush();
    entityManager.clear();

    boolean result = userService.changePassword(user, "WrongPassword123#", "NewStrongPassword123#");

    entityManager.flush();
    entityManager.clear();

    assertFalse(result);

    UserEntity persisted = userRepository.findById(user.getUserId()).orElseThrow();
    assertTrue(passwordEncoder.matches("OldPassword123#", persisted.getPassword()));
    assertFalse(passwordEncoder.matches("NewStrongPassword123#", persisted.getPassword()));
  }
}