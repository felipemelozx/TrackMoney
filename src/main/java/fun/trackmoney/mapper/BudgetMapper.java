package fun.trackmoney.mapper;

import fun.trackmoney.dto.budget.BudgetCreateDTO;
import fun.trackmoney.dto.budget.BudgetResponseDTO;
import fun.trackmoney.entity.BudgetsEntity;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class BudgetMapper {

  private final AccountMapper accountMapper;

  public BudgetMapper(AccountMapper accountMapper) {
    this.accountMapper = accountMapper;
  }

  public BudgetsEntity createDtoTOEntity(BudgetCreateDTO dto) {
    if (dto == null) {
      return null;
    }
    BudgetsEntity entity = new BudgetsEntity();
    entity.setPercent(dto.percent());
    return entity;
  }

  public BudgetResponseDTO entityToResponseDTO(BudgetsEntity entity) {
    if (entity == null) {
      return null;
    }
    return new BudgetResponseDTO(
        entity.getBudgetId(),
        entity.getCategory(),
        accountMapper.accountEntityToAccountResponse(entity.getAccount()),
        entity.getPercent(),
        null,
        null,
        null
    );
  }

  public List<BudgetResponseDTO> entityListToResponseList(List<BudgetsEntity> entityList) {
    if (entityList == null) {
      return null;
    }
    return entityList.stream()
        .map(this::entityToResponseDTO)
        .toList();
  }
}
