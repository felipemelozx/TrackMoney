package fun.trackmoney.account.service;

import fun.trackmoney.account.dtos.AccountRequestDTO;
import fun.trackmoney.account.dtos.AccountResponseDTO;
import fun.trackmoney.account.dtos.AccountUpdateRequestDTO;
import fun.trackmoney.account.entity.AccountEntity;
import fun.trackmoney.account.exception.AccountNotFoundException;
import fun.trackmoney.account.mapper.AccountMapper;
import fun.trackmoney.account.repository.AccountRepository;
import fun.trackmoney.user.entity.UserEntity;
import fun.trackmoney.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AccountServiceTest {

  private AccountRepository accountRepository;
  private AccountMapper accountMapper;
  private UserRepository userRepository;

  private AccountService accountService;

  @BeforeEach
  void setUp() {
    accountRepository = mock(AccountRepository.class);
    accountMapper = mock(AccountMapper.class);
    userRepository = mock(UserRepository.class);
    accountService = new AccountService(accountRepository, accountMapper, userRepository);
  }

  @Test
  void testCreateAccount() {
    UUID userId = UUID.randomUUID();
    AccountRequestDTO requestDTO = new AccountRequestDTO(userId, "Conta Corrente", BigDecimal.valueOf(1000), true);
    AccountEntity accountEntity = new AccountEntity();
    UserEntity user = new UserEntity();
    AccountEntity savedEntity = new AccountEntity();
    AccountResponseDTO responseDTO = new AccountResponseDTO(1, null, "Conta Corrente", BigDecimal.valueOf(1000), true);

    when(accountMapper.accountRequestToAccountEntity(requestDTO)).thenReturn(accountEntity);
    when(userRepository.findById(userId)).thenReturn(Optional.of(user));
    when(accountRepository.save(accountEntity)).thenReturn(savedEntity);
    when(accountMapper.accountEntityToAccountResponse(savedEntity)).thenReturn(responseDTO);

    AccountResponseDTO result = accountService.createAccount(requestDTO);

    assertEquals(responseDTO, result);
    verify(accountRepository).save(accountEntity);
    verify(userRepository).findById(userId);
    assertEquals(user, accountEntity.getUser());
  }

  @Test
  void testFindAllAccounts() {
    UUID uuid = UUID.randomUUID();
    List<AccountEntity> entities = List.of(new AccountEntity(), new AccountEntity());
    List<AccountResponseDTO> responseDTOs = List.of(
        new AccountResponseDTO(1, null, "Conta 1", BigDecimal.valueOf(100), false),
        new AccountResponseDTO(2, null, "Conta 2", BigDecimal.valueOf(200), true)
    );

    when(accountRepository.findAllByUserEmail(uuid)).thenReturn(entities);
    when(accountMapper.accountEntityListToAccountResponseList(entities)).thenReturn(responseDTOs);

    List<AccountResponseDTO> result = accountService.findAllAccount(uuid);

    assertEquals(2, result.size());
    assertEquals(responseDTOs, result);
  }

  @Test
  void testFindAccountById_Success() {
    AccountEntity entity = new AccountEntity();
    AccountResponseDTO responseDTO = new AccountResponseDTO(1, null, "Conta Corrente", BigDecimal.valueOf(1000), false);

    when(accountRepository.findById(1)).thenReturn(Optional.of(entity));
    when(accountMapper.accountEntityToAccountResponse(entity)).thenReturn(responseDTO);

    AccountResponseDTO result = accountService.findAccountById(1);

    assertEquals(responseDTO, result);
  }

  @Test
  void testFindAccountById_NotFound() {
    when(accountRepository.findById(1)).thenReturn(Optional.empty());

    assertThrows(AccountNotFoundException.class, () -> accountService.findAccountById(1));
  }

  @Test
  void testUpdateAccountById_Success() {
    AccountEntity existing = new AccountEntity();
    existing.setName("Old Name");
    existing.setIsAccountDefault(false);

    AccountUpdateRequestDTO dto = new AccountUpdateRequestDTO("New Name", true);
    AccountEntity saved = new AccountEntity();
    AccountResponseDTO responseDTO = new AccountResponseDTO(1, null, "New Name", BigDecimal.ZERO, true);

    when(accountRepository.findById(1)).thenReturn(Optional.of(existing));
    when(accountRepository.save(existing)).thenReturn(saved);
    when(accountMapper.accountEntityToAccountResponse(saved)).thenReturn(responseDTO);

    AccountResponseDTO result = accountService.updateAccountById(1, dto);

    assertEquals(responseDTO, result);
    assertEquals("New Name", existing.getName());
    assertTrue(existing.getIsAccountDefault());
  }

  @Test
  void testUpdateAccountById_NotFound() {
    AccountUpdateRequestDTO dto = new AccountUpdateRequestDTO("New Name", true);

    when(accountRepository.findById(1)).thenReturn(Optional.empty());

    assertThrows(AccountNotFoundException.class, () -> accountService.updateAccountById(1, dto));
    verify(accountRepository, never()).save(any());
  }

  @Test
  void testDeleteAccountById() {
    doNothing().when(accountRepository).deleteById(1);

    accountService.deleteById(1);

    verify(accountRepository, times(1)).deleteById(1);
  }

  @Test
  void testUpdateAccountBalanceSum() {
    AccountEntity account = new AccountEntity(1, null, "Test Account", BigDecimal.valueOf(100), true);
    when(accountRepository.findById(1)).thenReturn(Optional.of(account));
    when(accountRepository.save(account)).thenReturn(account);

    accountService.updateAccountBalance(BigDecimal.valueOf(100), 1, true);
    assertEquals(new BigDecimal("200"), account.getBalance());
    verify(accountRepository, times(1)).save(account);
  }

  @Test
  void testUpdateAccountBalanceSub() {
    AccountEntity account = new AccountEntity(1, null, "Test Account", BigDecimal.valueOf(100), true);
    when(accountRepository.findById(1)).thenReturn(Optional.of(account));
    when(accountRepository.save(account)).thenReturn(any());

    accountService.updateAccountBalance(BigDecimal.valueOf(100), 1, false);
    assertEquals(new BigDecimal("0"), account.getBalance());
    verify(accountRepository, times(1)).save(account);
  }
}
