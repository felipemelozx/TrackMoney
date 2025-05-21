package fun.trackmoney.budget.mapper;

import fun.trackmoney.budget.dtos.BudgetCreateDTO;
import fun.trackmoney.budget.dtos.BudgetResponseDTO;
import fun.trackmoney.budget.entity.BudgetsEntity;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface BudgetMapper {

  BudgetsEntity createDtoTOEntity(BudgetCreateDTO dto);

  BudgetResponseDTO entityToResponseDTO(BudgetsEntity entity);

  List<BudgetResponseDTO> entityListToResponseList(List<BudgetsEntity> entityList);
}
