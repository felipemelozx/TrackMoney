package fun.trackmoney.budget.mapper;

import fun.trackmoney.budget.dtos.BudgetHistoryResponseDTO;
import fun.trackmoney.budget.entity.BudgetHistoryEntity;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface BudgetHistoryMapper {

  BudgetHistoryResponseDTO entityToResponseDTO(BudgetHistoryEntity entity);

  List<BudgetHistoryResponseDTO> entityListToResponseList(List<BudgetHistoryEntity> entityList);
}
