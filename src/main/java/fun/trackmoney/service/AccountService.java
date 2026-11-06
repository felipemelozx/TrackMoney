package fun.trackmoney.service;

import fun.trackmoney.dto.account.AccountRequestDTO;
import fun.trackmoney.dto.account.AccountResponseDTO;
import fun.trackmoney.dto.account.AccountUpdateRequestDTO;
import fun.trackmoney.entity.AccountEntity;
import fun.trackmoney.exception.AccountHasRelatedDataException;
import fun.trackmoney.exception.AccountNotFoundException;
import fun.trackmoney.exception.InvalidAccountRequestException;
import fun.trackmoney.exception.UserNotFoundException;
import fun.trackmoney.mapper.AccountMapper;
import fun.trackmoney.repository.AccountRepository;
import fun.trackmoney.repository.UserRepository;
import fun.trackmoney.utils.ConcurrencyRetryPolicy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.transaction.support.TransactionTemplate;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
public class AccountService {

  private static final String ACCOUNT_NOT_FOUND_MESSAGE = "Account not found!";

  private final AccountRepository accountRepository;
  private final AccountMapper accountMapper;
  private final UserRepository userRepository;
  private final ConcurrencyRetryPolicy retryPolicy;
  private final TransactionTemplate transactionTemplate;

  public AccountService(AccountRepository accountRepository,
                        AccountMapper accountMapper,
                        UserRepository userRepository) {
    this(accountRepository, accountMapper, userRepository,
        new ConcurrencyRetryPolicy(), (PlatformTransactionManager) null);
  }

  @Autowired
  public AccountService(AccountRepository accountRepository,
                        AccountMapper accountMapper,
                        UserRepository userRepository,
                        ConcurrencyRetryPolicy retryPolicy,
                        PlatformTransactionManager transactionManager) {
    this.accountRepository = accountRepository;
    this.accountMapper = accountMapper;
    this.userRepository = userRepository;
    this.retryPolicy = retryPolicy != null ? retryPolicy : new ConcurrencyRetryPolicy();
    this.transactionTemplate = transactionManager != null ? new TransactionTemplate(transactionManager) : null;
  }

  public AccountResponseDTO createAccount(AccountRequestDTO dto, UUID authenticatedUserId) {
    AccountEntity account = accountMapper.accountRequestToAccountEntity(dto);
    if (account == null) {
      throw new InvalidAccountRequestException("Account request is required.");
    }

    account.setUser(userRepository.findById(authenticatedUserId)
        .orElseThrow(() -> new UserNotFoundException("User not found!")));

    return accountMapper.accountEntityToAccountResponse(accountRepository
           .save(account));
  }

  public List<AccountResponseDTO> findAllAccount(UUID userId) {
    return accountMapper.accountEntityListToAccountResponseList(accountRepository.findAllByUserEmail(userId));
  }

  public AccountResponseDTO findAccountById(Integer id, UUID authenticatedUserId) {
    AccountEntity account = accountRepository.findByAccountIdAndUserId(id, authenticatedUserId)
        .orElseThrow(() -> new AccountNotFoundException(ACCOUNT_NOT_FOUND_MESSAGE));
    return accountMapper.accountEntityToAccountResponse(account);
  }

  public AccountEntity findById(Integer id) {
    return accountRepository.findById(id).orElse(null);
  }

  public AccountEntity findAccountDefaultByUserId(UUID userId) {
    return accountRepository.findDefaultAccountByUserId(userId).orElse(null);
  }

  public AccountResponseDTO updateAccountById(Integer id, AccountUpdateRequestDTO dto, UUID authenticatedUserId) {
    AccountEntity account = accountRepository.findByAccountIdAndUserId(id, authenticatedUserId)
        .orElseThrow(() -> new AccountNotFoundException(ACCOUNT_NOT_FOUND_MESSAGE));

    account.setName(dto.name());

    return accountMapper.accountEntityToAccountResponse(accountRepository.save(account));
  }

  @Transactional
  public void deleteById(Integer id, UUID authenticatedUserId) {
    AccountEntity account = accountRepository.findByAccountIdAndUserId(id, authenticatedUserId)
        .orElseThrow(() -> new AccountNotFoundException(ACCOUNT_NOT_FOUND_MESSAGE));

    if (accountRepository.hasRelatedData(id)) {
      throw new AccountHasRelatedDataException(id);
    }

    userRepository.clearAccountReference(authenticatedUserId);
    accountRepository.deleteByAccountIdDirect(account.getAccountId());
  }

  public boolean updateAccountBalance(BigDecimal balance, Integer accountId, Boolean isCredit) {
    if (balance == null || accountId == null) {
      return false;
    }

    if (TransactionSynchronizationManager.isActualTransactionActive()) {
      return executeUpdateBalance(balance, accountId, isCredit);
    }

    if (transactionTemplate == null) {
      return retryPolicy.execute(() -> executeUpdateBalance(balance, accountId, isCredit));
    }

    return retryPolicy.execute(() ->
        Boolean.TRUE.equals(transactionTemplate.execute(status ->
            executeUpdateBalance(balance, accountId, isCredit)))
    );
  }

  @Transactional(propagation = Propagation.REQUIRED)
  public boolean executeUpdateBalance(BigDecimal balance, Integer accountId, Boolean isCredit) {
    AccountEntity account = accountRepository.findByIdForUpdate(accountId)
        .or(() -> accountRepository.findById(accountId))
        .orElse(null);

    if (account == null) {
      return false;
    }

    BigDecimal currentBalance = account.getBalance() != null ? account.getBalance() : BigDecimal.ZERO;
    if (Boolean.TRUE.equals(isCredit)) {
      account.setBalance(currentBalance.add(balance));
    } else {
      account.setBalance(currentBalance.subtract(balance));
    }

    accountRepository.save(account);
    return true;
  }
}
