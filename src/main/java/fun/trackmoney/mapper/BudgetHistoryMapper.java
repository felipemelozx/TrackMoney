package fun.trackmoney.mapper;

import fun.trackmoney.dto.budget.BudgetHistoryResponseDTO;
import fun.trackmoney.entity.BudgetHistoryEntity;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class BudgetHistoryMapper {

  public BudgetHistoryResponseDTO entityToResponseDTO(BudgetHistoryEntity entity) {
    if (entity == null) {
      return null;
    }
    return new BudgetHistoryResponseDTO(
        entity.getHistoryId(),
        null,
        entity.getCategory(),
        entity.getReferenceMonth(),
        entity.getReferenceYear(),
        entity.getPercent(),
        entity.getTargetAmount(),
        entity.getSpentAmount(),
        entity.getRemainingAmount(),
        entity.getTotalIncome(),
        null,
        entity.getStatus(),
        entity.getCreatedAt()
    );
  }

  public List<BudgetHistoryResponseDTO> entityListToResponseList(List<BudgetHistoryEntity> entityList) {
    if (entityList == null) {
      return null;
    }
    return entityList.stream()
        .map(this::entityToResponseDTO)
        .toList();
  }
}
