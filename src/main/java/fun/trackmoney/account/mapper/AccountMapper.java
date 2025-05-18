package fun.trackmoney.account.mapper;

import fun.trackmoney.account.dtos.AccountRequestDTO;
import fun.trackmoney.account.dtos.AccountResponseDTO;
import fun.trackmoney.account.dtos.AccountUpdateRequestDTO;
import fun.trackmoney.account.entity.AccountEntity;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface AccountMapper {
  AccountEntity accountRequestToAccountEntity(AccountRequestDTO accountRequestDTO);

  AccountEntity accountUpdateRequestToAccountEntity(AccountUpdateRequestDTO accountRequestDTO);

  AccountResponseDTO accountEntityToAccountResponse(AccountEntity accountEntity);

  List<AccountResponseDTO> accountEntityListToAccountResponseList(List<AccountEntity> entities);

  AccountEntity accountResponseToEntity(AccountResponseDTO accountById);
}
