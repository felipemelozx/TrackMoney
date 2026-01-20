package fun.trackmoney.seed.service;

import fun.trackmoney.account.entity.AccountEntity;
import fun.trackmoney.budget.entity.BudgetsEntity;
import fun.trackmoney.budget.repository.BudgetsRepository;
import fun.trackmoney.category.entity.CategoryEntity;
import fun.trackmoney.category.repository.CategoryRepository;
import fun.trackmoney.enums.TransactionType;
import fun.trackmoney.pots.entity.PotsEntity;
import fun.trackmoney.pots.repository.PotsRepository;
import fun.trackmoney.recurring.entity.RecurringEntity;
import fun.trackmoney.recurring.repository.RecurringRepository;
import fun.trackmoney.seed.service.generator.BudgetGenerator;
import fun.trackmoney.seed.service.generator.PotGenerator;
import fun.trackmoney.seed.service.generator.RecurringGenerator;
import fun.trackmoney.seed.service.generator.TransactionGenerator;
import fun.trackmoney.seed.service.model.SeedDataSummary;
import fun.trackmoney.transaction.entity.TransactionEntity;
import fun.trackmoney.transaction.repository.TransactionRepository;
import fun.trackmoney.user.entity.UserEntity;
import fun.trackmoney.user.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class SeedDataService {

  private static final Logger LOG = LoggerFactory.getLogger(SeedDataService.class);
  private static final String SEED_USER_EMAIL = "test@example.com";
  private static final String SEED_USER_PASSWORD = "Test@123";

  private final PasswordEncoder passwordEncoder;
  private final UserRepository userRepository;
  private final CategoryRepository categoryRepository;
  private final BudgetsRepository budgetRepository;
  private final PotsRepository potsRepository;
  private final RecurringRepository recurringRepository;
  private final TransactionRepository transactionRepository;

  private final TransactionGenerator transactionGenerator;
  private final BudgetGenerator budgetGenerator;
  private final PotGenerator potGenerator;
  private final RecurringGenerator recurringGenerator;

  public SeedDataService(PasswordEncoder passwordEncoder,
                         UserRepository userRepository,
                         CategoryRepository categoryRepository,
                         BudgetsRepository budgetRepository,
                         PotsRepository potsRepository,
                         RecurringRepository recurringRepository,
                         TransactionRepository transactionRepository,
                         TransactionGenerator transactionGenerator,
                         BudgetGenerator budgetGenerator,
                         PotGenerator potGenerator,
                         RecurringGenerator recurringGenerator) {
    this.passwordEncoder = passwordEncoder;
    this.userRepository = userRepository;
    this.categoryRepository = categoryRepository;
    this.budgetRepository = budgetRepository;
    this.potsRepository = potsRepository;
    this.recurringRepository = recurringRepository;
    this.transactionRepository = transactionRepository;
    this.transactionGenerator = transactionGenerator;
    this.budgetGenerator = budgetGenerator;
    this.potGenerator = potGenerator;
    this.recurringGenerator = recurringGenerator;
  }

  public boolean seedDataAlreadyExists() {
    return userRepository.findByEmail(SEED_USER_EMAIL).isPresent();
  }

  @Transactional
  public SeedDataSummary generateSeedData() {
    LOG.info("=== Starting Seed Data Generation ===");
    LOG.info("Target user: {}", SEED_USER_EMAIL);

    var userAndAccount = createSeedUserWithAccount();
    var categories = loadCategories();

    var budgets = generateAndSaveBudgets(userAndAccount.account(), categories);
    var pots = generateAndSavePots(userAndAccount.account());
    var recurring = generateAndSaveRecurring(userAndAccount.account(), categories);
    var transactions = generateAndSaveTransactions(userAndAccount.account(), categories);

    updateAccountBalance(userAndAccount.account(), transactions);

    var summary = createSummary(budgets, pots, recurring, transactions);
    LOG.info("=== Seed Data Generation Complete ===");
    LOG.info("Summary: {}", summary);

    return summary;
  }

  private UserAndAccount createSeedUserWithAccount() {
    LOG.info("Step 1: Creating user...");
    UserEntity user = createSeedUser();
    AccountEntity account = user.getAccount();
    LOG.debug("User created with ID: {}", user.getUserId());
    return new UserAndAccount(user, account);
  }

  private List<CategoryEntity> loadCategories() {
    LOG.info("Step 2: Loading categories...");
    List<CategoryEntity> categories = categoryRepository.findAll();
    LOG.debug("Loaded {} categories", categories.size());
    return categories;
  }

  private List<BudgetsEntity> generateAndSaveBudgets(AccountEntity account,
                                                     List<CategoryEntity> categories) {
    LOG.info("Step 3: Generating budgets...");
    List<BudgetsEntity> budgets = budgetGenerator.generate(account, categories);
    budgetRepository.saveAll(budgets);
    LOG.info("Created {} budgets", budgets.size());
    return budgets;
  }

  private List<PotsEntity> generateAndSavePots(AccountEntity account) {
    LOG.info("Step 4: Generating pots...");
    List<PotsEntity> pots = potGenerator.generate(account);
    potsRepository.saveAll(pots);
    LOG.info("Created {} pots", pots.size());
    return pots;
  }

  private List<RecurringEntity> generateAndSaveRecurring(AccountEntity account,
                                                         List<CategoryEntity> categories) {
    LOG.info("Step 5: Generating recurring transactions...");
    List<RecurringEntity> recurring = recurringGenerator.generate(account, categories);
    recurringRepository.saveAll(recurring);
    LOG.info("Created {} recurring transactions", recurring.size());
    return recurring;
  }

  private List<TransactionEntity> generateAndSaveTransactions(AccountEntity account,
                                                             List<CategoryEntity> categories) {
    LOG.info("Step 6: Generating transactions (this may take a while)...");
    List<TransactionEntity> transactions = transactionGenerator.generate(
        account, categories,
        LocalDateTime.now().minusYears(2),
        LocalDateTime.now()
    );
    saveTransactionsInBatches(transactions, 100);
    LOG.info("Created {} transactions", transactions.size());
    return transactions;
  }

  private SeedDataSummary createSummary(List<BudgetsEntity> budgets,
                                       List<PotsEntity> pots,
                                       List<RecurringEntity> recurring,
                                       List<TransactionEntity> transactions) {
    return new SeedDataSummary(
        1,
        1,
        budgets.size(),
        pots.size(),
        recurring.size(),
        transactions.size()
    );
  }

  private record UserAndAccount(UserEntity user, AccountEntity account) {}

  private UserEntity createSeedUser() {
    String encodedPassword = passwordEncoder.encode(SEED_USER_PASSWORD);

    UserEntity user = new UserEntity();
    user.setName("Usuário de Desenvolvimento");
    user.setEmail(SEED_USER_EMAIL);
    user.setPassword(encodedPassword);
    user.activate();

    AccountEntity account = new AccountEntity();
    account.setName("Conta Principal");
    account.setBalance(BigDecimal.ZERO);
    account.setUser(user);

    user.setAccount(account);

    return userRepository.save(user);
  }

  private void saveTransactionsInBatches(List<TransactionEntity> transactions, int batchSize) {
    for (int i = 0; i < transactions.size(); i += batchSize) {
      int end = Math.min(i + batchSize, transactions.size());
      List<TransactionEntity> batch = transactions.subList(i, end);
      transactionRepository.saveAll(batch);
      transactionRepository.flush();
    }
  }

  private void updateAccountBalance(AccountEntity account, List<TransactionEntity> transactions) {
    BigDecimal balance = transactions.stream()
        .map(tx -> tx.getTransactionType() == TransactionType.INCOME
            ? tx.getAmount()
            : tx.getAmount().negate())
        .reduce(BigDecimal.ZERO, BigDecimal::add);

    account.setBalance(balance);
    userRepository.save(account.getUser());
  }
}
