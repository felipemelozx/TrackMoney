package fun.trackmoney.mapper;

import fun.trackmoney.dto.recurring.CreateRecurringRequest;
import fun.trackmoney.dto.recurring.RecurringResponse;
import fun.trackmoney.entity.RecurringEntity;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class RecurringMapper {

  public RecurringEntity toEntity(CreateRecurringRequest request) {
    if (request == null) {
      return null;
    }
    RecurringEntity entity = new RecurringEntity();
    entity.setFrequency(request.frequency());
    entity.setTransactionType(request.transactionType());
    entity.setAmount(request.amount());
    entity.setDescription(request.description());
    entity.setTransactionName(request.transactionName());
    return entity;
  }

  public RecurringResponse toResponse(RecurringEntity save) {
    if (save == null) {
      return null;
    }
    return new RecurringResponse(
        save.getId(),
        save.getFrequency(),
        save.getCategory(),
        save.getTransactionType(),
        save.getNextDate(),
        save.getLastDate(),
        save.getAmount(),
        save.getDescription(),
        save.getTransactionName()
    );
  }

  public List<RecurringResponse> toResponse(List<RecurringEntity> save) {
    if (save == null) {
      return null;
    }
    return save.stream()
        .map(this::toResponse)
        .toList();
  }
}
