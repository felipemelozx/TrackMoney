package fun.trackmoney.mapper;

import fun.trackmoney.dto.category.CategoryResponseDTO;
import fun.trackmoney.entity.CategoryEntity;
import fun.trackmoney.testutils.CategoryEntityFactory;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CategoryMapperTest {

  private final CategoryMapper mapper = new CategoryMapper();

  @Test
  void categoryEntityToCategoryResponse_shouldMapAllFields() {
    CategoryEntity entity = CategoryEntityFactory.defaultCategory();

    CategoryResponseDTO dto = mapper.categoryEntityToCategoryResponse(entity);

    assertNotNull(dto);
    assertEquals(entity.getCategoryId(), dto.categoryId());
    assertEquals(entity.getName(), dto.name());
    assertEquals(entity.getColor(), dto.color());
  }

  @Test
  void categoryEntityToCategoryResponse_shouldReturnNullWhenInputIsNull() {
    assertNull(mapper.categoryEntityToCategoryResponse(null));
  }

  @Test
  void categoryEntityListToResponseList_shouldMapList() {
    List<CategoryEntity> entities = List.of(
        CategoryEntityFactory.defaultCategory(),
        CategoryEntityFactory.transportCategory()
    );

    List<CategoryResponseDTO> dtos = mapper.categoryEntityListToResponseList(entities);

    assertNotNull(dtos);
    assertEquals(2, dtos.size());
    assertEquals(1, dtos.get(0).categoryId());
    assertEquals(2, dtos.get(1).categoryId());
  }

  @Test
  void categoryEntityListToResponseList_shouldReturnNullWhenInputIsNull() {
    assertNull(mapper.categoryEntityListToResponseList(null));
  }

  @Test
  void categoryEntityListToResponseList_shouldMapEmptyList() {
    List<CategoryResponseDTO> dtos = mapper.categoryEntityListToResponseList(List.of());

    assertNotNull(dtos);
    assertEquals(0, dtos.size());
  }
}
