package fun.trackmoney.mapper;

import fun.trackmoney.dto.budget.BudgetHistoryResponseDTO;
import fun.trackmoney.entity.BudgetHistoryEntity;
import fun.trackmoney.testutils.BudgetHistoryEntityFactory;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class BudgetHistoryMapperTest {

  private final BudgetHistoryMapper mapper = new BudgetHistoryMapper();

  @Test
  void entityToResponseDTO_shouldMapAllFieldsExceptTransactionsAndBudgetId() {
    BudgetHistoryEntity entity = BudgetHistoryEntityFactory.defaultBudgetHistory();

    BudgetHistoryResponseDTO dto = mapper.entityToResponseDTO(entity);

    assertNotNull(dto);
    assertEquals(entity.getHistoryId(), dto.historyId());
    assertNull(dto.budgetId());
    assertEquals(entity.getCategory(), dto.category());
    assertEquals(entity.getReferenceMonth(), dto.referenceMonth());
    assertEquals(entity.getReferenceYear(), dto.referenceYear());
    assertEquals(entity.getPercent(), dto.percent());
    assertEquals(entity.getTargetAmount(), dto.targetAmount());
    assertEquals(entity.getSpentAmount(), dto.spentAmount());
    assertEquals(entity.getRemainingAmount(), dto.remainingAmount());
    assertEquals(entity.getTotalIncome(), dto.totalIncome());
    assertNull(dto.transactions());
    assertEquals(entity.getStatus(), dto.status());
    assertEquals(entity.getCreatedAt(), dto.createdAt());
  }

  @Test
  void entityToResponseDTO_shouldReturnNullWhenInputIsNull() {
    assertNull(mapper.entityToResponseDTO(null));
  }

  @Test
  void entityListToResponseList_shouldMapList() {
    List<BudgetHistoryEntity> entities = List.of(
        BudgetHistoryEntityFactory.defaultBudgetHistory(),
        BudgetHistoryEntityFactory.exceededBudgetHistory()
    );

    List<BudgetHistoryResponseDTO> dtos = mapper.entityListToResponseList(entities);

    assertNotNull(dtos);
    assertEquals(2, dtos.size());
    assertEquals(1, dtos.get(0).historyId());
    assertEquals(2, dtos.get(1).historyId());
  }

  @Test
  void entityListToResponseList_shouldReturnNullWhenInputIsNull() {
    assertNull(mapper.entityListToResponseList(null));
  }
}
