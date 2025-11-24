package fun.trackmoney.pots.mapper;

import fun.trackmoney.pots.dtos.CreatePotsDTO;
import fun.trackmoney.pots.dtos.PotsResponseDTO;
import fun.trackmoney.pots.entity.PotsEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface PotsMapper {

  List<PotsResponseDTO> listToResponse(List<PotsEntity> entityList);

  @Mapping(target = "color", source = "color.hex")
  PotsResponseDTO toResponse(PotsEntity entity);

  PotsEntity toEntity(CreatePotsDTO dto);
}
