package fun.trackmoney.mapper;

import fun.trackmoney.dto.pots.CreatePotsDTO;
import fun.trackmoney.dto.pots.PotsResponseDTO;
import fun.trackmoney.entity.PotsEntity;
import fun.trackmoney.enums.ColorPick;
import fun.trackmoney.testutils.CreatePotsDTOFactory;
import fun.trackmoney.testutils.PotsEntityFactory;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PotsMapperTest {

  private final PotsMapper mapper = new PotsMapper();

  @Test
  void toResponse_shouldMapAllFieldsAndExtractColorHex() {
    PotsEntity entity = PotsEntityFactory.defaultPot();

    PotsResponseDTO dto = mapper.toResponse(entity);

    assertNotNull(dto);
    assertEquals(entity.getPotId(), dto.potId());
    assertEquals(entity.getName(), dto.name());
    assertEquals(entity.getTargetAmount(), dto.targetAmount());
    assertEquals(entity.getCurrentAmount(), dto.currentAmount());
    assertEquals(ColorPick.DARK_BLUE.getHex(), dto.color());
  }

  @Test
  void toResponse_shouldReturnNullWhenInputIsNull() {
    assertNull(mapper.toResponse(null));
  }

  @Test
  void toResponse_shouldHandleNullColor() {
    PotsEntity entity = new PotsEntity();
    entity.setPotId(1L);
    entity.setName("Test");
    entity.setTargetAmount(BigDecimal.TEN);
    entity.setCurrentAmount(BigDecimal.ZERO);
    entity.setColor(null);

    PotsResponseDTO dto = mapper.toResponse(entity);

    assertNotNull(dto);
    assertNull(dto.color());
  }

  @Test
  void toEntity_shouldMapAllFields() {
    CreatePotsDTO dto = CreatePotsDTOFactory.defaultCreatePot();

    PotsEntity entity = mapper.toEntity(dto);

    assertNotNull(entity);
    assertEquals(dto.name(), entity.getName());
    assertEquals(dto.targetAmount(), entity.getTargetAmount());
    assertEquals(dto.color(), entity.getColor());
  }

  @Test
  void toEntity_shouldReturnNullWhenInputIsNull() {
    assertNull(mapper.toEntity(null));
  }

  @Test
  void listToResponse_shouldMapList() {
    List<PotsEntity> entities = List.of(
        PotsEntityFactory.defaultPot(),
        PotsEntityFactory.vacationPot()
    );

    List<PotsResponseDTO> dtos = mapper.listToResponse(entities);

    assertNotNull(dtos);
    assertEquals(2, dtos.size());
    assertEquals(1L, dtos.get(0).potId());
    assertEquals(2L, dtos.get(1).potId());
  }

  @Test
  void listToResponse_shouldReturnNullWhenInputIsNull() {
    assertNull(mapper.listToResponse(null));
  }
}
