package fun.trackmoney.transaction.mapper;

import fun.trackmoney.dto.transaction.CreateTransactionDTO;
import fun.trackmoney.dto.transaction.TransactionResponseDTO;
import fun.trackmoney.entity.TransactionEntity;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface TransactionMapper {

  TransactionEntity createTransactionToEntity(CreateTransactionDTO dto);

  TransactionResponseDTO toResponseDTO(TransactionEntity save);

  List<TransactionResponseDTO> toResponseDTOList(List<TransactionEntity> all);
}
