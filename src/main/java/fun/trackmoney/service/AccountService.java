package fun.trackmoney.service;

import fun.trackmoney.dto.account.AccountRequestDTO;
import fun.trackmoney.dto.account.AccountResponseDTO;
import fun.trackmoney.dto.account.AccountUpdateRequestDTO;
import fun.trackmoney.entity.AccountEntity;
import fun.trackmoney.exception.AccountHasRelatedDataException;
import fun.trackmoney.exception.AccountNotFoundException;
import fun.trackmoney.exception.UserNotFoundException;
import fun.trackmoney.mapper.AccountMapper;
import fun.trackmoney.repository.AccountRepository;
import fun.trackmoney.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
public class AccountService {

  private final AccountRepository accountRepository;
  private final AccountMapper accountMapper;
  private final UserRepository userRepository;

  public AccountService(AccountRepository accountRepository,
                        AccountMapper accountMapper,
                        UserRepository userRepository) {
    this.accountRepository = accountRepository;
    this.accountMapper = accountMapper;
    this.userRepository = userRepository;
  }

  public AccountResponseDTO createAccount(AccountRequestDTO dto, UUID authenticatedUserId) {
    AccountEntity account = accountMapper.accountRequestToAccountEntity(dto);

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
        .orElseThrow(() -> new AccountNotFoundException("Account not found!"));
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
        .orElseThrow(() -> new AccountNotFoundException("Account not found!"));

    account.setName(dto.name());

    return accountMapper.accountEntityToAccountResponse(accountRepository.save(account));
  }

  @Transactional
  public void deleteById(Integer id, UUID authenticatedUserId) {
    AccountEntity account = accountRepository.findByAccountIdAndUserId(id, authenticatedUserId)
        .orElseThrow(() -> new AccountNotFoundException("Account not found!"));

    if (accountRepository.hasRelatedData(id)) {
      throw new AccountHasRelatedDataException(id);
    }

    userRepository.clearAccountReference(authenticatedUserId);
    accountRepository.deleteByAccountIdDirect(account.getAccountId());
  }

  public boolean updateAccountBalance(BigDecimal balance, Integer accountId, Boolean isCredit) {
    AccountEntity account = findById(accountId);

    if(account == null) {
      return false;
    }

    if (isCredit) {
      account.setBalance(account.getBalance().add(balance));
    } else{
      account.setBalance(account.getBalance().subtract(balance));
    }

    accountRepository.save(account);
    return true;
  }
}
