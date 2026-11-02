package fun.trackmoney.mapper;

import fun.trackmoney.dto.recurring.CreateRecurringRequest;
import fun.trackmoney.dto.recurring.RecurringResponse;
import fun.trackmoney.entity.RecurringEntity;
import fun.trackmoney.testutils.CreateRecurringRequestFactory;
import fun.trackmoney.testutils.RecurringEntityFactory;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class RecurringMapperTest {

  private final RecurringMapper mapper = new RecurringMapper();

  @Test
  void toEntity_shouldMapAllFields() {
    CreateRecurringRequest request = CreateRecurringRequestFactory.defaultRequest();

    RecurringEntity entity = mapper.toEntity(request);

    assertNotNull(entity);
    assertEquals(request.frequency(), entity.getFrequency());
    assertEquals(request.transactionType(), entity.getTransactionType());
    assertEquals(request.amount(), entity.getAmount());
    assertEquals(request.description(), entity.getDescription());
    assertEquals(request.transactionName(), entity.getTransactionName());
  }

  @Test
  void toEntity_shouldReturnNullWhenInputIsNull() {
    assertNull(mapper.toEntity(null));
  }

  @Test
  void toResponse_shouldMapAllFields() {
    RecurringEntity entity = RecurringEntityFactory.defaultEntity();

    RecurringResponse response = mapper.toResponse(entity);

    assertNotNull(response);
    assertEquals(entity.getId(), response.id());
    assertEquals(entity.getFrequency(), response.frequency());
    assertEquals(entity.getCategory(), response.category());
    assertEquals(entity.getTransactionType(), response.transactionType());
    assertEquals(entity.getNextDate(), response.nextDate());
    assertEquals(entity.getLastDate(), response.lastDate());
    assertEquals(entity.getAmount(), response.amount());
    assertEquals(entity.getDescription(), response.description());
    assertEquals(entity.getTransactionName(), response.transactionName());
  }

  @Test
  void toResponse_shouldReturnNullWhenInputIsNull() {
    assertNull(mapper.toResponse((RecurringEntity) null));
  }

  @Test
  void toResponseList_shouldMapList() {
    List<RecurringEntity> entities = List.of(RecurringEntityFactory.defaultEntity());

    List<RecurringResponse> responses = mapper.toResponse(entities);

    assertNotNull(responses);
    assertEquals(1, responses.size());
    assertEquals(1L, responses.get(0).id());
  }

  @Test
  void toResponseList_shouldReturnNullWhenInputIsNull() {
    assertNull(mapper.toResponse((List<RecurringEntity>) null));
  }
}
