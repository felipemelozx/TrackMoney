package fun.trackmoney.service;

import fun.trackmoney.service.AccountService;
import fun.trackmoney.dto.account.AccountRequestDTO;
import fun.trackmoney.dto.account.AccountResponseDTO;
import fun.trackmoney.dto.account.AccountUpdateRequestDTO;
import fun.trackmoney.entity.AccountEntity;
import fun.trackmoney.exception.AccountNotFoundException;
import fun.trackmoney.mapper.AccountMapper;
import fun.trackmoney.repository.AccountRepository;
import fun.trackmoney.entity.UserEntity;
import fun.trackmoney.repository.UserRepository;
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
    AccountRequestDTO requestDTO = new AccountRequestDTO(userId, "Conta Corrente", BigDecimal.valueOf(1000));
    AccountEntity accountEntity = new AccountEntity();
    UserEntity user = new UserEntity();
    AccountEntity savedEntity = new AccountEntity();
    AccountResponseDTO responseDTO = new AccountResponseDTO(1, null, "Conta Corrente", BigDecimal.valueOf(1000));

    when(accountMapper.accountRequestToAccountEntity(requestDTO)).thenReturn(accountEntity);
    when(userRepository.findById(userId)).thenReturn(Optional.of(user));
    when(accountRepository.save(accountEntity)).thenReturn(savedEntity);
    when(accountMapper.accountEntityToAccountResponse(savedEntity)).thenReturn(responseDTO);

    AccountResponseDTO result = accountService.createAccount(requestDTO, userId);

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
        new AccountResponseDTO(1, null, "Conta 1", BigDecimal.valueOf(100)),
        new AccountResponseDTO(2, null, "Conta 2", BigDecimal.valueOf(200))
    );

    when(accountRepository.findAllByUserEmail(uuid)).thenReturn(entities);
    when(accountMapper.accountEntityListToAccountResponseList(entities)).thenReturn(responseDTOs);

    List<AccountResponseDTO> result = accountService.findAllAccount(uuid);

    assertEquals(2, result.size());
    assertEquals(responseDTOs, result);
  }

  @Test
  void testFindAccountById_Success() {
    UUID ownerId = UUID.randomUUID();
    AccountEntity entity = new AccountEntity();
    entity.setUser(new UserEntity().setUserId(ownerId));
    AccountResponseDTO responseDTO = new AccountResponseDTO(1, null, "Conta Corrente", BigDecimal.valueOf(1000));

    when(accountRepository.findByAccountIdAndUserId(1, ownerId)).thenReturn(Optional.of(entity));
    when(accountMapper.accountEntityToAccountResponse(entity)).thenReturn(responseDTO);

    AccountResponseDTO result = accountService.findAccountById(1, ownerId);

    assertEquals(responseDTO, result);
  }

  @Test
  void testFindAccountById_NotFound() {
    UUID ownerId = UUID.randomUUID();
    when(accountRepository.findByAccountIdAndUserId(1, ownerId)).thenReturn(Optional.empty());

    assertThrows(AccountNotFoundException.class, () -> accountService.findAccountById(1, ownerId));
  }

  @Test
  void testUpdateAccountById_Success() {
    UUID ownerId = UUID.randomUUID();
    AccountEntity existing = new AccountEntity();
    existing.setName("Old Name");
    existing.setUser(new UserEntity().setUserId(ownerId));

    AccountUpdateRequestDTO dto = new AccountUpdateRequestDTO("New Name");
    AccountEntity saved = new AccountEntity();
    AccountResponseDTO responseDTO = new AccountResponseDTO(1, null, "New Name", BigDecimal.ZERO);

    when(accountRepository.findByAccountIdAndUserId(1, ownerId)).thenReturn(Optional.of(existing));
    when(accountRepository.save(existing)).thenReturn(saved);
    when(accountMapper.accountEntityToAccountResponse(saved)).thenReturn(responseDTO);

    AccountResponseDTO result = accountService.updateAccountById(1, dto, ownerId);

    assertEquals(responseDTO, result);
    assertEquals("New Name", existing.getName());
  }

  @Test
  void testUpdateAccountById_NotFound() {
    UUID ownerId = UUID.randomUUID();
    AccountUpdateRequestDTO dto = new AccountUpdateRequestDTO("New Name");

    when(accountRepository.findByAccountIdAndUserId(1, ownerId)).thenReturn(Optional.empty());

    assertThrows(AccountNotFoundException.class, () -> accountService.updateAccountById(1, dto, ownerId));
    verify(accountRepository, never()).save(any());
  }

  @Test
  void testDeleteAccountById() {
    UUID ownerId = UUID.randomUUID();
    AccountEntity account = new AccountEntity().setAccountId(1).setUser(new UserEntity().setUserId(ownerId));
    when(accountRepository.findByAccountIdAndUserId(1, ownerId)).thenReturn(Optional.of(account));
    when(accountRepository.hasRelatedData(1)).thenReturn(false);

    accountService.deleteById(1, ownerId);

    verify(userRepository).clearAccountReference(ownerId);
    verify(accountRepository).deleteByAccountIdDirect(1);
  }

  @Test
  void testDeleteAccountById_RejectsAccountOwnedByDifferentUser() {
    UUID callerId = UUID.randomUUID();
    when(accountRepository.findByAccountIdAndUserId(1, callerId)).thenReturn(Optional.empty());

    assertThrows(AccountNotFoundException.class, () -> accountService.deleteById(1, callerId));

    verify(accountRepository, never()).deleteByAccountIdDirect(anyInt());
  }

  @Test
  void testDeleteAccountById_RejectsAccountWithRelatedData() {
    UUID ownerId = UUID.randomUUID();
    AccountEntity account = new AccountEntity().setAccountId(1).setUser(new UserEntity().setUserId(ownerId));
    when(accountRepository.findByAccountIdAndUserId(1, ownerId)).thenReturn(Optional.of(account));
    when(accountRepository.hasRelatedData(1)).thenReturn(true);

    assertThrows(fun.trackmoney.exception.AccountHasRelatedDataException.class,
        () -> accountService.deleteById(1, ownerId));

    verify(accountRepository, never()).deleteByAccountIdDirect(anyInt());
  }

  @Test
  void testUpdateAccountBalanceSum() {
    AccountEntity account = new AccountEntity(1, null, "Test Account", BigDecimal.valueOf(100));
    when(accountRepository.findById(1)).thenReturn(Optional.of(account));
    when(accountRepository.save(account)).thenReturn(account);

    accountService.updateAccountBalance(BigDecimal.valueOf(100), 1, true);
    assertEquals(new BigDecimal("200"), account.getBalance());
    verify(accountRepository, times(1)).save(account);
  }

  @Test
  void testUpdateAccountBalanceSub() {
    AccountEntity account = new AccountEntity(1, null, "Test Account", BigDecimal.valueOf(100));
    when(accountRepository.findById(1)).thenReturn(Optional.of(account));
    when(accountRepository.save(account)).thenReturn(any());

    accountService.updateAccountBalance(BigDecimal.valueOf(100), 1, false);
    assertEquals(new BigDecimal("0"), account.getBalance());
    verify(accountRepository, times(1)).save(account);
  }
}
