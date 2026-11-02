package fun.trackmoney.mapper;

import fun.trackmoney.dto.account.AccountRequestDTO;
import fun.trackmoney.dto.account.AccountResponseDTO;
import fun.trackmoney.dto.account.AccountUpdateRequestDTO;
import fun.trackmoney.entity.AccountEntity;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class AccountMapper {

  private final UserMapper userMapper;

  public AccountMapper(UserMapper userMapper) {
    this.userMapper = userMapper;
  }

  public AccountEntity accountRequestToAccountEntity(AccountRequestDTO accountRequestDTO) {
    if (accountRequestDTO == null) {
      return null;
    }
    AccountEntity entity = new AccountEntity();
    entity.setName(accountRequestDTO.name());
    entity.setBalance(accountRequestDTO.balance());
    return entity;
  }

  public AccountEntity accountUpdateRequestToAccountEntity(AccountUpdateRequestDTO accountRequestDTO) {
    if (accountRequestDTO == null) {
      return null;
    }
    AccountEntity entity = new AccountEntity();
    entity.setName(accountRequestDTO.name());
    return entity;
  }

  public AccountResponseDTO accountEntityToAccountResponse(AccountEntity accountEntity) {
    if (accountEntity == null) {
      return null;
    }
    return new AccountResponseDTO(
        accountEntity.getAccountId(),
        userMapper.userEntityToUserResponseDto(accountEntity.getUser()),
        accountEntity.getName(),
        accountEntity.getBalance()
    );
  }

  public List<AccountResponseDTO> accountEntityListToAccountResponseList(List<AccountEntity> entities) {
    if (entities == null) {
      return null;
    }
    return entities.stream()
        .map(this::accountEntityToAccountResponse)
        .toList();
  }

  public AccountEntity accountResponseToEntity(AccountResponseDTO accountById) {
    if (accountById == null) {
      return null;
    }
    AccountEntity entity = new AccountEntity();
    entity.setAccountId(accountById.accountId());
    entity.setUser(userMapper.userResponseDtoToEntity(accountById.user()));
    entity.setName(accountById.name());
    entity.setBalance(accountById.balance());
    return entity;
  }

  public AccountUpdateRequestDTO toAccountRequest(AccountEntity account) {
    if (account == null) {
      return null;
    }
    return new AccountUpdateRequestDTO(account.getName());
  }
}
