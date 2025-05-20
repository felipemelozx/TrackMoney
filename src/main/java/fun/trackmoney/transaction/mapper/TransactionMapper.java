package fun.trackmoney.transaction.mapper;

import fun.trackmoney.transaction.dto.CreateTransactionDTO;
import fun.trackmoney.transaction.dto.TransactionResponseDTO;
import fun.trackmoney.transaction.entity.TransactionEntity;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface TransactionMapper {

  TransactionEntity createTransactionToEntity(CreateTransactionDTO dto);

  TransactionResponseDTO toResponseDTO(TransactionEntity save);

  List<TransactionResponseDTO> toResponseDTOList(List<TransactionEntity> all);
}
