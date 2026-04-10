package fun.trackmoney.mapper;

import fun.trackmoney.dto.budget.BudgetHistoryResponseDTO;
import fun.trackmoney.entity.BudgetHistoryEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface BudgetHistoryMapper {

  @Mapping(target = "transactions", ignore = true)
  BudgetHistoryResponseDTO entityToResponseDTO(BudgetHistoryEntity entity);

  @Mapping(target = "transactions", ignore = true)
  List<BudgetHistoryResponseDTO> entityListToResponseList(List<BudgetHistoryEntity> entityList);
}
