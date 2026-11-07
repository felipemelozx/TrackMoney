package fun.trackmoney.controller;

import fun.trackmoney.entity.AccountEntity;
import fun.trackmoney.entity.CategoryEntity;
import fun.trackmoney.entity.TransactionEntity;
import fun.trackmoney.entity.UserEntity;
import fun.trackmoney.enums.TransactionType;
import fun.trackmoney.repository.AccountRepository;
import fun.trackmoney.repository.CategoryRepository;
import fun.trackmoney.repository.TransactionRepository;
import fun.trackmoney.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class TransactionOwnershipIntegrationTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private AccountRepository accountRepository;

  @Autowired
  private CategoryRepository categoryRepository;

  @Autowired
  private TransactionRepository transactionRepository;

  private UserEntity callerUser;
  private UserEntity ownerUser;
  private AccountEntity callerAccount;
  private AccountEntity ownerAccount;

  @BeforeEach
  void setUp() {
    callerUser = persistUserWithAccount("caller", new BigDecimal("1000.00"));
    ownerUser = persistUserWithAccount("owner", new BigDecimal("2000.00"));
    callerAccount = callerUser.getAccount();
    ownerAccount = ownerUser.getAccount();
  }

  @Test
  void deleteTransaction_whenCallerDoesNotOwnTransaction_shouldNotModifyBalancesAndNotDeleteTransaction()
      throws Exception {
    CategoryEntity category = categoryRepository.findAll().get(0);

    TransactionEntity ownerTransaction = new TransactionEntity()
        .setAccount(ownerAccount)
        .setCategory(category)
        .setTransactionType(TransactionType.EXPENSE)
        .setAmount(new BigDecimal("150.00"))
        .setDescription("Owner expense transaction")
        .setTransactionDate(LocalDateTime.now());
    ownerTransaction = transactionRepository.saveAndFlush(ownerTransaction);

    BigDecimal initialCallerBalance = accountRepository.findById(callerAccount.getAccountId())
        .orElseThrow().getBalance();
    BigDecimal initialOwnerBalance = accountRepository.findById(ownerAccount.getAccountId())
        .orElseThrow().getBalance();

    mockMvc.perform(delete("/transaction/{id}", ownerTransaction.getTransactionId())
            .with(authentication(new UsernamePasswordAuthenticationToken(callerUser, null,
                List.of(new SimpleGrantedAuthority("USER_ROLES"))))))
        .andExpect(status().isOk());

    TransactionEntity persistedTransaction = transactionRepository.findById(ownerTransaction.getTransactionId())
        .orElse(null);
    assertNotNull(persistedTransaction, "Owner transaction must still exist in the database");
    assertEquals(ownerAccount.getAccountId(), persistedTransaction.getAccount().getAccountId());

    BigDecimal finalCallerBalance = accountRepository.findById(callerAccount.getAccountId())
        .orElseThrow().getBalance();
    BigDecimal finalOwnerBalance = accountRepository.findById(ownerAccount.getAccountId())
        .orElseThrow().getBalance();

    assertEquals(0, initialCallerBalance.compareTo(finalCallerBalance),
        "Caller balance must not change when attempting to delete another user's transaction");
    assertEquals(0, initialOwnerBalance.compareTo(finalOwnerBalance),
        "Owner balance must remain unchanged");
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
