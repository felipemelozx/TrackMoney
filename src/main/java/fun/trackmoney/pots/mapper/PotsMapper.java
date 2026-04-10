package fun.trackmoney.pots.mapper;

import fun.trackmoney.dto.pots.CreatePotsDTO;
import fun.trackmoney.dto.pots.PotsResponseDTO;
import fun.trackmoney.entity.PotsEntity;
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
