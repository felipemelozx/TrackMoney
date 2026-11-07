package fun.trackmoney.service;

import fun.trackmoney.dto.transaction.CreateTransactionDTO;
import fun.trackmoney.entity.AccountEntity;
import fun.trackmoney.entity.CategoryEntity;
import fun.trackmoney.entity.UserEntity;
import fun.trackmoney.enums.TransactionType;
import fun.trackmoney.repository.AccountRepository;
import fun.trackmoney.repository.CategoryRepository;
import fun.trackmoney.repository.TransactionRepository;
import fun.trackmoney.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.orm.ObjectOptimisticLockingFailureException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class AccountBalanceConcurrencyIntegrationTest {

  @Autowired
  private AccountService accountService;

  @Autowired
  private TransactionService transactionService;

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private AccountRepository accountRepository;

  @Autowired
  private CategoryRepository categoryRepository;

  @Autowired
  private TransactionRepository transactionRepository;

  @Autowired
  private org.springframework.transaction.PlatformTransactionManager transactionManager;

  private UserEntity testUser;
  private AccountEntity testAccount;
  private CategoryEntity testCategory;

  @BeforeEach
  void setUp() {
    testUser = new UserEntity();
    testUser.setName("Concurrency User");
    testUser.setEmail("concurrency-" + UUID.randomUUID() + "@example.com");
    testUser.setPassword("encoded-password");
    testUser.activate();

    testAccount = new AccountEntity()
        .setName("Concurrency Account")
        .setBalance(new BigDecimal("1000.0000"))
        .setUser(testUser);
    testUser.setAccount(testAccount);

    testUser = userRepository.saveAndFlush(testUser);
    testAccount = testUser.getAccount();

    List<CategoryEntity> categories = categoryRepository.findAll();
    if (!categories.isEmpty()) {
      testCategory = categories.get(0);
    }
  }

  @AfterEach
  void tearDown() {
    if (testAccount != null && testAccount.getAccountId() != null) {
      new org.springframework.transaction.support.TransactionTemplate(transactionManager).execute(status -> {
        transactionRepository.deleteAllByAccount(testAccount);
        userRepository.clearAccountReference(testUser.getUserId());
        accountRepository.deleteByAccountIdDirect(testAccount.getAccountId());
        userRepository.deleteById(testUser.getUserId());
        return null;
      });
    }
  }

  @Test
  void updateAccountBalance_shouldNotLoseUpdates_underConcurrentExpenses() throws Exception {
    Integer accountId = testAccount.getAccountId();
    int threadCount = 2;
    ExecutorService executor = Executors.newFixedThreadPool(threadCount);
    CountDownLatch readyLatch = new CountDownLatch(threadCount);
    CountDownLatch startLatch = new CountDownLatch(1);

    Future<Boolean> task1 = executor.submit(() -> {
      readyLatch.countDown();
      readyLatch.await();
      startLatch.await();
      return accountService.updateAccountBalance(new BigDecimal("100.0000"), accountId, false);
    });

    Future<Boolean> task2 = executor.submit(() -> {
      readyLatch.countDown();
      readyLatch.await();
      startLatch.await();
      return accountService.updateAccountBalance(new BigDecimal("200.0000"), accountId, false);
    });

    readyLatch.await(5, TimeUnit.SECONDS);
    startLatch.countDown();

    assertTrue(task1.get(10, TimeUnit.SECONDS));
    assertTrue(task2.get(10, TimeUnit.SECONDS));
    executor.shutdown();
    assertTrue(executor.awaitTermination(5, TimeUnit.SECONDS));

    AccountEntity updatedAccount = accountRepository.findById(accountId).orElseThrow();
    assertEquals(0, new BigDecimal("700.0000").compareTo(updatedAccount.getBalance()));
    assertTrue(updatedAccount.getVersion() >= 2);
  }

  @Test
  void updateAccountBalance_shouldHandleMultipleConcurrentUpdatesExactly() throws Exception {
    Integer accountId = testAccount.getAccountId();
    int threadCount = 10;
    ExecutorService executor = Executors.newFixedThreadPool(threadCount);
    CountDownLatch readyLatch = new CountDownLatch(threadCount);
    CountDownLatch startLatch = new CountDownLatch(1);
    List<Future<Boolean>> futures = new ArrayList<>();

    for (int i = 0; i < threadCount; i++) {
      final boolean isCredit = (i % 2 == 0);
      final BigDecimal amount = new BigDecimal(isCredit ? "50.0000" : "100.0000");

      futures.add(executor.submit(() -> {
        readyLatch.countDown();
        readyLatch.await();
        startLatch.await();
        return accountService.updateAccountBalance(amount, accountId, isCredit);
      }));
    }

    readyLatch.await(5, TimeUnit.SECONDS);
    startLatch.countDown();

    for (Future<Boolean> future : futures) {
      assertTrue(future.get(10, TimeUnit.SECONDS));
    }
    executor.shutdown();
    assertTrue(executor.awaitTermination(5, TimeUnit.SECONDS));

    // 5 credits of 50 (+250) and 5 debits of 100 (-500) from initial 1000 => 750
    AccountEntity updatedAccount = accountRepository.findById(accountId).orElseThrow();
    assertEquals(0, new BigDecimal("750.0000").compareTo(updatedAccount.getBalance()));
  }

  @Test
  void createTransaction_shouldMaintainExactBalance_underConcurrentTransactions() throws Exception {
    if (testCategory == null) {
      return;
    }

    Integer accountId = testAccount.getAccountId();
    int threadCount = 2;
    ExecutorService executor = Executors.newFixedThreadPool(threadCount);
    CountDownLatch readyLatch = new CountDownLatch(threadCount);
    CountDownLatch startLatch = new CountDownLatch(1);

    CreateTransactionDTO dto1 = new CreateTransactionDTO(
        "Expense 1",
        testCategory.getCategoryId(),
        TransactionType.EXPENSE,
        new BigDecimal("100.0000"),
        "Concurrent expense 1",
        LocalDateTime.now()
    );

    CreateTransactionDTO dto2 = new CreateTransactionDTO(
        "Expense 2",
        testCategory.getCategoryId(),
        TransactionType.EXPENSE,
        new BigDecimal("200.0000"),
        "Concurrent expense 2",
        LocalDateTime.now()
    );

    Future<Boolean> task1 = executor.submit(() -> {
      readyLatch.countDown();
      readyLatch.await();
      startLatch.await();
      return transactionService.createTransaction(dto1, testUser).isOk();
    });

    Future<Boolean> task2 = executor.submit(() -> {
      readyLatch.countDown();
      readyLatch.await();
      startLatch.await();
      return transactionService.createTransaction(dto2, testUser).isOk();
    });

    readyLatch.await(5, TimeUnit.SECONDS);
    startLatch.countDown();

    assertTrue(task1.get(10, TimeUnit.SECONDS));
    assertTrue(task2.get(10, TimeUnit.SECONDS));
    executor.shutdown();
    assertTrue(executor.awaitTermination(5, TimeUnit.SECONDS));

    AccountEntity updatedAccount = accountRepository.findById(accountId).orElseThrow();
    assertEquals(0, new BigDecimal("700.0000").compareTo(updatedAccount.getBalance()));
  }

  @Test
  void accountEntity_shouldDetectStaleVersionConflict() {
    AccountEntity accountA = accountRepository.findById(testAccount.getAccountId()).orElseThrow();
    AccountEntity accountB = accountRepository.findById(testAccount.getAccountId()).orElseThrow();

    accountA.setBalance(new BigDecimal("900.0000"));
    accountRepository.saveAndFlush(accountA);

    accountB.setBalance(new BigDecimal("800.0000"));
    assertThrows(ObjectOptimisticLockingFailureException.class, () -> {
      accountRepository.saveAndFlush(accountB);
    });
  }
}
