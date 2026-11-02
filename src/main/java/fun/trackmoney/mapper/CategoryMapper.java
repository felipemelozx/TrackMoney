package fun.trackmoney.mapper;

import fun.trackmoney.dto.category.CategoryResponseDTO;
import fun.trackmoney.entity.CategoryEntity;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CategoryMapper {

  public CategoryResponseDTO categoryEntityToCategoryResponse(CategoryEntity entity) {
    if (entity == null) {
      return null;
    }
    return new CategoryResponseDTO(
        entity.getCategoryId(),
        entity.getName(),
        entity.getColor()
    );
  }

  public List<CategoryResponseDTO> categoryEntityListToResponseList(List<CategoryEntity> entities) {
    if (entities == null) {
      return null;
    }
    return entities.stream()
        .map(this::categoryEntityToCategoryResponse)
        .toList();
  }
}
