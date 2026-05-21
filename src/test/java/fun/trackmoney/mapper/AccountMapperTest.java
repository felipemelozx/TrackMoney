package fun.trackmoney.mapper;

import fun.trackmoney.dto.account.AccountRequestDTO;
import fun.trackmoney.dto.account.AccountResponseDTO;
import fun.trackmoney.dto.account.AccountUpdateRequestDTO;
import fun.trackmoney.entity.AccountEntity;
import fun.trackmoney.testutils.AccountEntityFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class AccountMapperTest {

  private AccountMapper mapper;

  @BeforeEach
  void setUp() {
    mapper = new AccountMapper(new UserMapper());
  }

  @Test
  void accountRequestToAccountEntity_shouldMapNameAndBalance() {
    AccountRequestDTO dto = new AccountRequestDTO(UUID.randomUUID(), "Conta", BigDecimal.valueOf(500));

    AccountEntity entity = mapper.accountRequestToAccountEntity(dto);

    assertNotNull(entity);
    assertEquals("Conta", entity.getName());
    assertEquals(BigDecimal.valueOf(500), entity.getBalance());
  }

  @Test
  void accountRequestToAccountEntity_shouldReturnNullWhenInputIsNull() {
    assertNull(mapper.accountRequestToAccountEntity(null));
  }

  @Test
  void accountUpdateRequestToAccountEntity_shouldMapNameOnly() {
    AccountUpdateRequestDTO dto = new AccountUpdateRequestDTO("Novo Nome");

    AccountEntity entity = mapper.accountUpdateRequestToAccountEntity(dto);

    assertNotNull(entity);
    assertEquals("Novo Nome", entity.getName());
  }

  @Test
  void accountUpdateRequestToAccountEntity_shouldReturnNullWhenInputIsNull() {
    assertNull(mapper.accountUpdateRequestToAccountEntity(null));
  }

  @Test
  void accountEntityToAccountResponse_shouldMapAllFieldsIncludingUser() {
    AccountEntity entity = AccountEntityFactory.defaultAccount();

    AccountResponseDTO dto = mapper.accountEntityToAccountResponse(entity);

    assertNotNull(dto);
    assertEquals(entity.getAccountId(), dto.accountId());
    assertEquals(entity.getName(), dto.name());
    assertEquals(entity.getBalance(), dto.balance());
    assertNotNull(dto.user());
    assertEquals(entity.getUser().getUserId(), dto.user().userId());
    assertEquals(entity.getUser().getName(), dto.user().name());
    assertEquals(entity.getUser().getEmail(), dto.user().email());
  }

  @Test
  void accountEntityToAccountResponse_shouldReturnNullWhenInputIsNull() {
    assertNull(mapper.accountEntityToAccountResponse(null));
  }

  @Test
  void accountEntityListToAccountResponseList_shouldMapList() {
    List<AccountEntity> entities = List.of(
        AccountEntityFactory.defaultAccount(),
        AccountEntityFactory.savingsAccount()
    );

    List<AccountResponseDTO> dtos = mapper.accountEntityListToAccountResponseList(entities);

    assertNotNull(dtos);
    assertEquals(2, dtos.size());
    assertEquals(1, dtos.get(0).accountId());
    assertEquals(2, dtos.get(1).accountId());
  }

  @Test
  void accountEntityListToAccountResponseList_shouldReturnNullWhenInputIsNull() {
    assertNull(mapper.accountEntityListToAccountResponseList(null));
  }

  @Test
  void accountResponseToEntity_shouldMapAllFields() {
    AccountEntity original = AccountEntityFactory.defaultAccount();
    AccountResponseDTO dto = mapper.accountEntityToAccountResponse(original);

    AccountEntity entity = mapper.accountResponseToEntity(dto);

    assertNotNull(entity);
    assertEquals(dto.accountId(), entity.getAccountId());
    assertEquals(dto.name(), entity.getName());
    assertEquals(dto.balance(), entity.getBalance());
    assertNotNull(entity.getUser());
    assertEquals(dto.user().userId(), entity.getUser().getUserId());
  }

  @Test
  void accountResponseToEntity_shouldReturnNullWhenInputIsNull() {
    assertNull(mapper.accountResponseToEntity(null));
  }

  @Test
  void toAccountRequest_shouldMapNameOnly() {
    AccountEntity entity = AccountEntityFactory.defaultAccount();

    AccountUpdateRequestDTO dto = mapper.toAccountRequest(entity);

    assertNotNull(dto);
    assertEquals(entity.getName(), dto.name());
  }

  @Test
  void toAccountRequest_shouldReturnNullWhenInputIsNull() {
    assertNull(mapper.toAccountRequest(null));
  }
}
