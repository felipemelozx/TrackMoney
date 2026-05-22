package fun.trackmoney.mapper;

import fun.trackmoney.dto.transaction.CreateTransactionDTO;
import fun.trackmoney.dto.transaction.TransactionResponseDTO;
import fun.trackmoney.entity.TransactionEntity;
import fun.trackmoney.testutils.CreateTransactionDTOBuilder;
import fun.trackmoney.testutils.TransactionEntityFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TransactionMapperTest {

  private TransactionMapper mapper;

  @BeforeEach
  void setUp() {
    mapper = new TransactionMapper(new AccountMapper(new UserMapper()));
  }

  @Test
  void createTransactionToEntity_shouldMapAllFields() {
    CreateTransactionDTO dto = CreateTransactionDTOBuilder.defaultTransaction();

    TransactionEntity entity = mapper.createTransactionToEntity(dto);

    assertNotNull(entity);
    assertEquals(dto.transactionType(), entity.getTransactionType());
    assertEquals(dto.amount(), entity.getAmount());
    assertEquals(dto.description(), entity.getDescription());
    assertEquals(dto.transactionDate(), entity.getTransactionDate());
    assertEquals(dto.transactionName(), entity.getTransactionName());
  }

  @Test
  void createTransactionToEntity_shouldReturnNullWhenInputIsNull() {
    assertNull(mapper.createTransactionToEntity(null));
  }

  @Test
  void toResponseDTO_shouldMapAllFieldsIncludingAccount() {
    TransactionEntity entity = TransactionEntityFactory.defaultExpenseNow();

    TransactionResponseDTO dto = mapper.toResponseDTO(entity);

    assertNotNull(dto);
    assertEquals(entity.getTransactionId(), dto.transactionId());
    assertEquals(entity.getTransactionName(), dto.transactionName());
    assertEquals(entity.getDescription(), dto.description());
    assertEquals(entity.getAmount(), dto.amount());
    assertEquals(entity.getTransactionType(), dto.transactionType());
    assertEquals(entity.getCategory(), dto.category());
    assertEquals(entity.getTransactionDate(), dto.transactionDate());
    assertNotNull(dto.account());
    assertEquals(entity.getAccount().getAccountId(), dto.account().accountId());
  }

  @Test
  void toResponseDTO_shouldReturnNullWhenInputIsNull() {
    assertNull(mapper.toResponseDTO(null));
  }

  @Test
  void toResponseDTOList_shouldMapList() {
    List<TransactionEntity> entities = List.of(
        TransactionEntityFactory.defaultExpenseNow(),
        TransactionEntityFactory.defaultIncomeNow()
    );

    List<TransactionResponseDTO> dtos = mapper.toResponseDTOList(entities);

    assertNotNull(dtos);
    assertEquals(2, dtos.size());
    assertEquals(1, dtos.get(0).transactionId());
    assertEquals(2, dtos.get(1).transactionId());
  }

  @Test
  void toResponseDTOList_shouldReturnNullWhenInputIsNull() {
    assertNull(mapper.toResponseDTOList(null));
  }
}
