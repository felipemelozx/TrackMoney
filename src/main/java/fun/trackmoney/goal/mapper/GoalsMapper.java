package fun.trackmoney.goal.mapper;

import fun.trackmoney.goal.dtos.CreateGoalsDTO;
import fun.trackmoney.goal.dtos.GoalsResponseDTO;
import fun.trackmoney.goal.entity.GoalsEntity;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface GoalsMapper {
  GoalsEntity toEntity(CreateGoalsDTO dto);

  GoalsResponseDTO toResponseDTO(GoalsEntity entity);

  List<GoalsResponseDTO> toListResponseDTO(List<GoalsEntity> entity);

}
