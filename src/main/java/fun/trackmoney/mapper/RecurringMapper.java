package fun.trackmoney.mapper;

import fun.trackmoney.dto.recurring.CreateRecurringRequest;
import fun.trackmoney.dto.recurring.RecurringResponse;
import fun.trackmoney.entity.RecurringEntity;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface RecurringMapper {

  RecurringEntity toEntity(CreateRecurringRequest request);

  RecurringResponse toResponse(RecurringEntity save);

  List<RecurringResponse> toResponse(List<RecurringEntity> save);
}
