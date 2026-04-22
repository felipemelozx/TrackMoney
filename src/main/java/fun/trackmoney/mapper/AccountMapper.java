package fun.trackmoney.mapper;

import fun.trackmoney.dto.account.AccountRequestDTO;
import fun.trackmoney.dto.account.AccountResponseDTO;
import fun.trackmoney.dto.account.AccountUpdateRequestDTO;
import fun.trackmoney.entity.AccountEntity;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface AccountMapper {
  AccountEntity accountRequestToAccountEntity(AccountRequestDTO accountRequestDTO);

  AccountEntity accountUpdateRequestToAccountEntity(AccountUpdateRequestDTO accountRequestDTO);

  AccountResponseDTO accountEntityToAccountResponse(AccountEntity accountEntity);

  List<AccountResponseDTO> accountEntityListToAccountResponseList(List<AccountEntity> entities);

  AccountEntity accountResponseToEntity(AccountResponseDTO accountById);

  AccountUpdateRequestDTO toAccountRequest(AccountEntity account);
}
