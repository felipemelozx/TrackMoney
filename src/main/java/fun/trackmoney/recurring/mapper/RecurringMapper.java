package fun.trackmoney.recurring.mapper;

import fun.trackmoney.recurring.dtos.CreateRecurringRequest;
import fun.trackmoney.recurring.dtos.RecurringResponse;
import fun.trackmoney.recurring.entity.RecurringEntity;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface RecurringMapper {

  RecurringEntity toEntity(CreateRecurringRequest request);

  RecurringResponse toResponse(RecurringEntity save);

  List<RecurringResponse> toResponse(List<RecurringEntity> save);
}
