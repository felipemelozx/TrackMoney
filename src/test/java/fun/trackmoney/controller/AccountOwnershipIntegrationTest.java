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
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class AccountOwnershipIntegrationTest {

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

  private UserEntity authenticatedUser;
  private UserEntity accountOwner;
  private AccountEntity otherUsersAccount;

  @BeforeEach
  void setUp() {
    authenticatedUser = persistUserWithAccount("caller");
    accountOwner = persistUserWithAccount("owner");
    otherUsersAccount = accountOwner.getAccount();
  }

  @Test
  void getAccountById_shouldNotExposeAnotherUsersAccount() throws Exception {
    mockMvc.perform(get("/accounts/{id}", otherUsersAccount.getAccountId())
            .with(authenticatedRequest()))
        .andExpect(status().isNotFound());
  }

  @Test
  void updateAccount_shouldNotModifyAnotherUsersAccount() throws Exception {
    mockMvc.perform(put("/accounts/{id}", otherUsersAccount.getAccountId())
            .with(authenticatedRequest())
            .contentType(MediaType.APPLICATION_JSON)
            .content("{\"name\":\"Alterada por outro usuário\"}"))
        .andExpect(status().isNotFound());

    assertEquals("Account owner", accountRepository.findById(otherUsersAccount.getAccountId())
        .orElseThrow().getName());
  }

  @Test
  void deleteAccount_shouldNotDeleteAnotherUsersAccount() throws Exception {
    mockMvc.perform(delete("/accounts/{id}", otherUsersAccount.getAccountId())
            .with(authenticatedRequest()))
        .andExpect(status().isNotFound());

    assertNotNull(accountRepository.findById(otherUsersAccount.getAccountId()).orElse(null));
    assertNotNull(userRepository.findById(accountOwner.getUserId()).orElseThrow().getAccount());
  }

  @Test
  void deleteAccountWithTransactions_shouldReturnConflictWithoutDeletingData() throws Exception {
    AccountEntity ownAccount = authenticatedUser.getAccount();
    CategoryEntity category = categoryRepository.findAll().get(0);
    TransactionEntity transaction = new TransactionEntity()
        .setAccount(ownAccount)
        .setCategory(category)
        .setTransactionType(TransactionType.EXPENSE)
        .setAmount(BigDecimal.TEN)
        .setDescription("Keep this financial record")
        .setTransactionDate(LocalDateTime.now());
    transactionRepository.saveAndFlush(transaction);

    mockMvc.perform(delete("/accounts/{id}", ownAccount.getAccountId())
            .with(authenticatedRequest()))
        .andExpect(status().isConflict());

    assertNotNull(accountRepository.findById(ownAccount.getAccountId()).orElse(null));
    assertNotNull(transactionRepository.findById(transaction.getTransactionId()).orElse(null));
  }

  private UserEntity persistUserWithAccount(String prefix) {
    UserEntity user = new UserEntity();
    user.setName(prefix);
    user.setEmail(prefix + "-" + UUID.randomUUID() + "@example.com");
    user.setPassword("encoded-password");
    user.activate();

    AccountEntity account = new AccountEntity()
        .setName(prefix.equals("owner") ? "Account owner" : "Caller account")
        .setBalance(BigDecimal.ZERO)
        .setUser(user);
    user.setAccount(account);

    return userRepository.saveAndFlush(user);
  }

  private org.springframework.test.web.servlet.request.RequestPostProcessor authenticatedRequest() {
    return authentication(new UsernamePasswordAuthenticationToken(authenticatedUser, null,
        List.of(new SimpleGrantedAuthority("USER_ROLES"))));
  }
}
