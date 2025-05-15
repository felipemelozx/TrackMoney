package fun.trackmoney.account.service;

import fun.trackmoney.account.dtos.AccountRequestDTO;
import fun.trackmoney.account.dtos.AccountResponseDTO;
import fun.trackmoney.account.dtos.AccountUpdateRequestDTO;
import fun.trackmoney.account.entity.AccountEntity;
import fun.trackmoney.account.mapper.AccountMapper;
import fun.trackmoney.account.repository.AccountRepository;
import fun.trackmoney.user.service.UserService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AccountService {

  private final AccountRepository accountRepository;
  private final AccountMapper accountMapper;
  private final UserService userService;

  public AccountService(AccountRepository accountRepository, AccountMapper accountMapper, UserService userService) {
    this.accountRepository = accountRepository;
    this.accountMapper = accountMapper;
    this.userService = userService;
  }

  public AccountResponseDTO createAccount(AccountRequestDTO dto) {
    AccountEntity account = accountMapper.accountRequestToAccountEntity(dto);

    account.setUser(userService.findUserById(dto.userId()));

    return accountMapper.accountEntityToAccountResponse(accountRepository
           .save(account));
  }

  public List<AccountResponseDTO> findAllAccount() {
    return accountMapper.accountEntityListToAccountResponseList(accountRepository.findAll());
  }

  public AccountResponseDTO findAccountById(Integer id) {
    return accountMapper.accountEntityToAccountResponse(accountRepository.findById(id)
        .orElseThrow(() -> {
          throw new RuntimeException("Erro");
        }));
  }

  public AccountResponseDTO updateAccountById(Integer id, AccountUpdateRequestDTO dto) {
    AccountEntity account = accountRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("Account not found!"));

    account.setName(dto.name());
    account.setIsAccountDefault(dto.isAccountDefault());

    return accountMapper.accountEntityToAccountResponse(accountRepository.save(account));
  }

  public void deleteById(Integer id) {
      accountRepository.deleteById(id);
  }
}
