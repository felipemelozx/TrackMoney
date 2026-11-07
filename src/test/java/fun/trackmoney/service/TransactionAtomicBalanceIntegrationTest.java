package fun.trackmoney.service;

import fun.trackmoney.dto.transaction.CreateTransactionDTO;
import fun.trackmoney.entity.AccountEntity;
import fun.trackmoney.entity.CategoryEntity;
import fun.trackmoney.entity.TransactionEntity;
import fun.trackmoney.entity.UserEntity;
import fun.trackmoney.enums.TransactionType;
import fun.trackmoney.exception.AccountNotFoundException;
import fun.trackmoney.repository.AccountRepository;
import fun.trackmoney.repository.CategoryRepository;
import fun.trackmoney.repository.TransactionRepository;
import fun.trackmoney.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;

@SpringBootTest
class TransactionAtomicBalanceIntegrationTest {

  @Autowired
  private TransactionService transactionService;

  @Autowired
  private TransactionRepository transactionRepository;

  @Autowired
  private AccountRepository accountRepository;

  @Autowired
  private CategoryRepository categoryRepository;

  @Autowired
  private UserRepository userRepository;

  @SpyBean
  private AccountService accountService;

  private UserEntity user;
  private AccountEntity account;
  private CategoryEntity category;

  @BeforeEach
  void setUp() {
    category = categoryRepository.findAll().get(0);
    user = persistUserWithAccount("atomic-user", new BigDecimal("500.00"));
    account = user.getAccount();
  }

  @Test
  void createTransaction_whenBalanceUpdateFails_shouldRollbackAndNotPersistTransaction() {
    doThrow(new AccountNotFoundException("Simulated balance failure"))
        .when(accountService).updateAccountBalance(any(), any(), any());

    CreateTransactionDTO dto = new CreateTransactionDTO(
        "Grocery",
        category.getCategoryId(),
        TransactionType.EXPENSE,
        new BigDecimal("50.00"),
        "Fresh produce",
        LocalDateTime.now()
    );

    long countBefore = transactionRepository.count();

    assertThrows(AccountNotFoundException.class, () ->
        transactionService.createTransaction(dto, user));

    long countAfter = transactionRepository.count();
    assertEquals(countBefore, countAfter, "No transaction should be persisted when balance update fails");
  }

  @Test
  void deleteTransaction_whenBalanceUpdateFails_shouldRollbackAndNotDeleteTransactionFromDatabase() {
    TransactionEntity transaction = new TransactionEntity()
        .setAccount(account)
        .setCategory(category)
        .setTransactionType(TransactionType.EXPENSE)
        .setAmount(new BigDecimal("100.00"))
        .setDescription("Test expense")
        .setTransactionDate(LocalDateTime.now());
    transaction = transactionRepository.saveAndFlush(transaction);

    Integer txId = transaction.getTransactionId();
    assertNotNull(txId);

    doThrow(new AccountNotFoundException("Simulated balance reversal failure"))
        .when(accountService).updateAccountBalance(any(), any(), any());

    assertThrows(AccountNotFoundException.class, () ->
        transactionService.delete(txId, user));

    TransactionEntity afterAttempt = transactionRepository.findById(txId).orElse(null);
    assertNotNull(afterAttempt, "Transaction must still exist when balance reversal fails");
  }

  private UserEntity persistUserWithAccount(String prefix, BigDecimal initialBalance) {
    UserEntity user = new UserEntity();
    user.setName(prefix);
    user.setEmail(prefix + "-" + UUID.randomUUID() + "@example.com");
    user.setPassword("encoded-password");
    user.activate();

    AccountEntity account = new AccountEntity()
        .setName(prefix + " account")
        .setBalance(initialBalance)
        .setUser(user);
    user.setAccount(account);

    return userRepository.saveAndFlush(user);
  }
}
