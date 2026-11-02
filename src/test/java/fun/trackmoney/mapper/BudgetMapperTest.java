package fun.trackmoney.mapper;

import fun.trackmoney.dto.budget.BudgetCreateDTO;
import fun.trackmoney.dto.budget.BudgetResponseDTO;
import fun.trackmoney.entity.BudgetsEntity;
import fun.trackmoney.testutils.BudgetCreateDTOFactory;
import fun.trackmoney.testutils.BudgetsEntityFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class BudgetMapperTest {

  private BudgetMapper mapper;

  @BeforeEach
  void setUp() {
    mapper = new BudgetMapper(new AccountMapper(new UserMapper()));
  }

  @Test
  void createDtoTOEntity_shouldMapPercentOnly() {
    BudgetCreateDTO dto = BudgetCreateDTOFactory.defaultDTO();

    BudgetsEntity entity = mapper.createDtoTOEntity(dto);

    assertNotNull(entity);
    assertEquals(dto.percent(), entity.getPercent());
  }

  @Test
  void createDtoTOEntity_shouldReturnNullWhenInputIsNull() {
    assertNull(mapper.createDtoTOEntity(null));
  }

  @Test
  void entityToResponseDTO_shouldMapAllFieldsAndLeaveComputedFieldsNull() {
    BudgetsEntity entity = BudgetsEntityFactory.defaultBudget();

    BudgetResponseDTO dto = mapper.entityToResponseDTO(entity);

    assertNotNull(dto);
    assertEquals(entity.getBudgetId(), dto.budgetId());
    assertEquals(entity.getCategory(), dto.category());
    assertEquals(entity.getPercent(), dto.percent());
    assertNotNull(dto.account());
    assertEquals(entity.getAccount().getAccountId(), dto.account().accountId());
    assertNull(dto.targetAmount());
    assertNull(dto.currentAmount());
    assertNull(dto.lastTransactions());
  }

  @Test
  void entityToResponseDTO_shouldReturnNullWhenInputIsNull() {
    assertNull(mapper.entityToResponseDTO(null));
  }

  @Test
  void entityListToResponseList_shouldMapList() {
    List<BudgetsEntity> entities = List.of(
        BudgetsEntityFactory.defaultBudget(),
        BudgetsEntityFactory.secondaryBudget()
    );

    List<BudgetResponseDTO> dtos = mapper.entityListToResponseList(entities);

    assertNotNull(dtos);
    assertEquals(2, dtos.size());
    assertEquals(1, dtos.get(0).budgetId());
    assertEquals(2, dtos.get(1).budgetId());
  }

  @Test
  void entityListToResponseList_shouldReturnNullWhenInputIsNull() {
    assertNull(mapper.entityListToResponseList(null));
  }
}
