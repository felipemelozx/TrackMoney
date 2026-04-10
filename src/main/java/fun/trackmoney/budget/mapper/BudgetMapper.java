package fun.trackmoney.budget.mapper;

import fun.trackmoney.dto.budget.BudgetCreateDTO;
import fun.trackmoney.dto.budget.BudgetResponseDTO;
import fun.trackmoney.entity.BudgetsEntity;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface BudgetMapper {

  BudgetsEntity createDtoTOEntity(BudgetCreateDTO dto);

  BudgetResponseDTO entityToResponseDTO(BudgetsEntity entity);

  List<BudgetResponseDTO> entityListToResponseList(List<BudgetsEntity> entityList);
}
