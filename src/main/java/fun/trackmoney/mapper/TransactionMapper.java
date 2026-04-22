package fun.trackmoney.mapper;

import fun.trackmoney.dto.transaction.CreateTransactionDTO;
import fun.trackmoney.dto.transaction.TransactionResponseDTO;
import fun.trackmoney.entity.TransactionEntity;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class TransactionMapper {

  private final AccountMapper accountMapper;

  public TransactionMapper(AccountMapper accountMapper) {
    this.accountMapper = accountMapper;
  }

  public TransactionEntity createTransactionToEntity(CreateTransactionDTO dto) {
    if (dto == null) {
      return null;
    }
    TransactionEntity entity = new TransactionEntity();
    entity.setTransactionType(dto.transactionType());
    entity.setAmount(dto.amount());
    entity.setDescription(dto.description());
    entity.setTransactionDate(dto.transactionDate());
    entity.setTransactionName(dto.transactionName());
    return entity;
  }

  public TransactionResponseDTO toResponseDTO(TransactionEntity save) {
    if (save == null) {
      return null;
    }
    return new TransactionResponseDTO(
        save.getTransactionId(),
        save.getTransactionName(),
        save.getDescription(),
        save.getAmount(),
        accountMapper.accountEntityToAccountResponse(save.getAccount()),
        save.getTransactionType(),
        save.getCategory(),
        save.getTransactionDate()
    );
  }

  public List<TransactionResponseDTO> toResponseDTOList(List<TransactionEntity> all) {
    if (all == null) {
      return null;
    }
    return all.stream()
        .map(this::toResponseDTO)
        .toList();
  }
}
