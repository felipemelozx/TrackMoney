package fun.trackmoney.service;

import fun.trackmoney.dto.account.AccountRequestDTO;
import fun.trackmoney.dto.account.AccountResponseDTO;
import fun.trackmoney.dto.account.AccountUpdateRequestDTO;
import fun.trackmoney.entity.AccountEntity;
import fun.trackmoney.exception.AccountNotFoundException;
import fun.trackmoney.mapper.AccountMapper;
import fun.trackmoney.repository.AccountRepository;
import fun.trackmoney.exception.UserNotFoundException;
import fun.trackmoney.repository.UserRepository;
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

  public AccountResponseDTO createAccount(AccountRequestDTO dto) {
    AccountEntity account = accountMapper.accountRequestToAccountEntity(dto);

    account.setUser(userRepository.findById(dto.userId())
        .orElseThrow(() -> new UserNotFoundException("User not found!")));

    return accountMapper.accountEntityToAccountResponse(accountRepository
           .save(account));
  }

  public List<AccountResponseDTO> findAllAccount(UUID userId) {
    return accountMapper.accountEntityListToAccountResponseList(accountRepository.findAllByUserEmail(userId));
  }

  // TODO: remove this method
  @Deprecated
  public AccountResponseDTO findAccountById(Integer id) {
    return accountMapper.accountEntityToAccountResponse(accountRepository.findById(id)
        .orElseThrow(() -> new AccountNotFoundException("Account not found!")));
  }

  public AccountEntity findById(Integer id) {
    return accountRepository.findById(id).orElse(null);
  }

  public AccountEntity findAccountDefaultByUserId(UUID userId) {
    return accountRepository.findDefaultAccountByUserId(userId).orElse(null);
  }

  public AccountResponseDTO updateAccountById(Integer id, AccountUpdateRequestDTO dto) {
    AccountEntity account = accountRepository.findById(id)
        .orElseThrow(() -> new AccountNotFoundException("Account not found!"));

    account.setName(dto.name());

    return accountMapper.accountEntityToAccountResponse(accountRepository.save(account));
  }

  public void deleteById(Integer id) {
      accountRepository.deleteById(id);
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
