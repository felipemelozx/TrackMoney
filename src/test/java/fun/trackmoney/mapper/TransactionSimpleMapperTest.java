package fun.trackmoney.mapper;

import fun.trackmoney.mapper.TransactionSimpleMapper;
import fun.trackmoney.dto.transaction.TransactionSimpleDTO;
import fun.trackmoney.entity.TransactionEntity;
import fun.trackmoney.mapper.TransactionSimpleMapperImpl;
import fun.trackmoney.testutils.TransactionEntityFactory;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

class TransactionSimpleMapperTest {

  private final TransactionSimpleMapper mapper = new TransactionSimpleMapperImpl();

  @Test
  void entityToSimpleDTO_shouldMapAllFields() {
    TransactionEntity entity = new TransactionEntity()
        .setTransactionId(1)
        .setTransactionName("Test Transaction")
        .setAmount(BigDecimal.valueOf(100.50))
        .setTransactionDate(LocalDateTime.of(2025, 1, 15, 10, 30));

    TransactionSimpleDTO dto = mapper.entityToSimpleDTO(entity);

    assertNotNull(dto);
    assertEquals(entity.getTransactionId(), dto.transactionId());
    assertEquals(entity.getTransactionName(), dto.transactionName());
    assertEquals(entity.getAmount(), dto.amount());
    assertEquals(entity.getTransactionDate(), dto.transactionDate());
  }

  @Test
  void entityToSimpleDTO_shouldHandleNullFields() {
    TransactionEntity entity = new TransactionEntity()
        .setTransactionId(null)
        .setTransactionName(null)
        .setAmount(null)
        .setTransactionDate(null);

    TransactionSimpleDTO dto = mapper.entityToSimpleDTO(entity);

    assertNotNull(dto);
    assertNull(dto.transactionId());
    assertNull(dto.transactionName());
    assertNull(dto.amount());
    assertNull(dto.transactionDate());
  }

  @Test
  void entityListToSimpleDTOList_shouldMapEmptyList() {
    List<TransactionSimpleDTO> dtos = mapper.entityListToSimpleDTOList(List.of());

    assertNotNull(dtos);
    assertEquals(0, dtos.size());
  }

  @Test
  void entityListToSimpleDTOList_shouldMapListOfEntities() {
    TransactionEntity entity1 = new TransactionEntity()
        .setTransactionId(1)
        .setTransactionName("Transaction 1")
        .setAmount(BigDecimal.valueOf(100))
        .setTransactionDate(LocalDateTime.of(2025, 1, 15, 10, 0));

    TransactionEntity entity2 = new TransactionEntity()
        .setTransactionId(2)
        .setTransactionName("Transaction 2")
        .setAmount(BigDecimal.valueOf(200))
        .setTransactionDate(LocalDateTime.of(2025, 1, 16, 11, 0));

    List<TransactionSimpleDTO> dtos = mapper.entityListToSimpleDTOList(List.of(entity1, entity2));

    assertNotNull(dtos);
    assertEquals(2, dtos.size());
    assertEquals(1, dtos.get(0).transactionId());
    assertEquals("Transaction 1", dtos.get(0).transactionName());
    assertEquals(2, dtos.get(1).transactionId());
    assertEquals("Transaction 2", dtos.get(1).transactionName());
  }

  @Test
  void entityListToSimpleDTOList_shouldWorkWithFactory() {
    TransactionEntity entity = TransactionEntityFactory.defaultExpenseNow();

    List<TransactionSimpleDTO> dtos = mapper.entityListToSimpleDTOList(List.of(entity));

    assertNotNull(dtos);
    assertEquals(1, dtos.size());
    assertEquals(entity.getTransactionId(), dtos.get(0).transactionId());
  }
}
