package fun.trackmoney.mapper;

import fun.trackmoney.dto.pots.CreatePotsDTO;
import fun.trackmoney.dto.pots.PotsResponseDTO;
import fun.trackmoney.entity.PotsEntity;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PotsMapper {

  public List<PotsResponseDTO> listToResponse(List<PotsEntity> entityList) {
    if (entityList == null) {
      return null;
    }
    return entityList.stream()
        .map(this::toResponse)
        .toList();
  }

  public PotsResponseDTO toResponse(PotsEntity entity) {
    if (entity == null) {
      return null;
    }
    return new PotsResponseDTO(
        entity.getPotId(),
        entity.getName(),
        entity.getTargetAmount(),
        entity.getCurrentAmount(),
        entity.getColor() != null ? entity.getColor().getHex() : null
    );
  }

  public PotsEntity toEntity(CreatePotsDTO dto) {
    if (dto == null) {
      return null;
    }
    PotsEntity entity = new PotsEntity();
    entity.setName(dto.name());
    entity.setTargetAmount(dto.targetAmount());
    entity.setColor(dto.color());
    return entity;
  }
}
