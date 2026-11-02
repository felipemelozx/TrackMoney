package fun.trackmoney.mapper;

import fun.trackmoney.dto.transaction.TransactionSimpleDTO;
import fun.trackmoney.entity.TransactionEntity;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class TransactionSimpleMapper {

  public TransactionSimpleDTO entityToSimpleDTO(TransactionEntity entity) {
    if (entity == null) {
      return null;
    }
    return new TransactionSimpleDTO(
        entity.getTransactionId(),
        entity.getTransactionName(),
        entity.getAmount(),
        entity.getTransactionDate()
    );
  }

  public List<TransactionSimpleDTO> entityListToSimpleDTOList(List<TransactionEntity> entityList) {
    if (entityList == null) {
      return null;
    }
    return entityList.stream()
        .map(this::entityToSimpleDTO)
        .toList();
  }
}
