package fun.trackmoney.transaction.mapper;

import fun.trackmoney.transaction.dto.TransactionSimpleDTO;
import fun.trackmoney.transaction.entity.TransactionEntity;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface TransactionSimpleMapper {

  TransactionSimpleDTO entityToSimpleDTO(TransactionEntity entity);

  List<TransactionSimpleDTO> entityListToSimpleDTOList(List<TransactionEntity> entityList);
}
