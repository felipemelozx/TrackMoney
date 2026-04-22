package fun.trackmoney.mapper;

import fun.trackmoney.dto.category.CategoryResponseDTO;
import fun.trackmoney.entity.CategoryEntity;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CategoryMapper {
  CategoryResponseDTO categoryEntityToCategoryResponse(CategoryEntity entity);

  List<CategoryResponseDTO> categoryEntityListToResponseList(List<CategoryEntity> entities);
}
