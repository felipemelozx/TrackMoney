package fun.trackmoney.recurring.mapper;

import fun.trackmoney.recurring.dtos.CreateRecurringRequest;
import fun.trackmoney.recurring.dtos.RecurringResponse;
import fun.trackmoney.recurring.entity.RecurringEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface RecurringMapper {

  RecurringEntity toEntity(CreateRecurringRequest request);

  RecurringResponse toResponse(RecurringEntity save);
}
