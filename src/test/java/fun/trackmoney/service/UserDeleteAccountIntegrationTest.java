package fun.trackmoney.service;

import fun.trackmoney.entity.AccountEntity;
import fun.trackmoney.entity.BudgetsEntity;
import fun.trackmoney.entity.CategoryEntity;
import fun.trackmoney.entity.UserEntity;
import fun.trackmoney.repository.AccountRepository;
import fun.trackmoney.repository.BudgetsRepository;
import fun.trackmoney.repository.CategoryRepository;
import fun.trackmoney.repository.UserRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@Transactional
class UserDeleteAccountIntegrationTest {

  @Autowired
  private UserService userService;

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private AccountRepository accountRepository;

  @Autowired
  private BudgetsRepository budgetsRepository;

  @Autowired
  private CategoryRepository categoryRepository;

  @Autowired
  private PasswordEncoder passwordEncoder;

  @Autowired
  private EntityManager entityManager;

  @Test
  void deleteAccount_shouldDeleteBudgetLinkedToAccount() {
    CategoryEntity category = categoryRepository.findAll().stream()
        .findFirst()
        .orElseGet(() -> categoryRepository.save(new CategoryEntity(null, "Teste", "#000000")));

    UserEntity user = new UserEntity();
    user.setName("Integration Test");
    user.setEmail("it-" + UUID.randomUUID() + "@example.com");
    user.setPassword(passwordEncoder.encode("password123"));
    user.activate();

    AccountEntity account = new AccountEntity()
        .setName("Conta Teste")
        .setBalance(BigDecimal.ZERO)
        .setUser(user);
    user.setAccount(account);

    BudgetsEntity budget = new BudgetsEntity(null, category, account, (short) 20);

    userRepository.save(user);
    budgetsRepository.save(budget);

    // Simula o cenário real: dados já persistidos em transação anterior
    // e um persistence context "fresco" ao chamar deleteAccount
    entityManager.flush();
    entityManager.clear();

    boolean result = userService.deleteAccount(user, "password123");

    entityManager.flush();
    entityManager.clear();

    assertTrue(result);
    assertTrue(userRepository.findById(user.getUserId()).isEmpty());
    assertTrue(accountRepository.findById(account.getAccountId()).isEmpty());
    assertTrue(budgetsRepository.findById(budget.getBudgetId()).isEmpty());
  }
}
