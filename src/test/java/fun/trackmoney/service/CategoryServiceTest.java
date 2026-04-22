package fun.trackmoney.service;
import fun.trackmoney.service.CategoryService;


import fun.trackmoney.dto.category.CategoryResponseDTO;
import fun.trackmoney.entity.CategoryEntity;
import fun.trackmoney.exception.CategoryNotFoundException;
import fun.trackmoney.mapper.CategoryMapper;
import fun.trackmoney.mapper.CategoryMapperImpl;
import fun.trackmoney.repository.CategoryRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

  @Mock
  private CategoryRepository categoryRepository;

  @Spy
  private CategoryMapper categoryMapper = new CategoryMapperImpl();

  @InjectMocks
  private CategoryService categoryService;

  @Test
  void findAll_shouldReturnAllCategories() {
    CategoryEntity category1 = new CategoryEntity(1, "Food", "#FF5733");
    CategoryEntity category2 = new CategoryEntity(2, "Transportation", "#33B5E5");

    List<CategoryEntity> entities = Arrays.asList(category1, category2);
    when(categoryRepository.findAll()).thenReturn(entities);

    List<CategoryResponseDTO> result = categoryService.findAll();

    assertEquals(2, result.size());
    assertEquals(1, result.get(0).categoryId());
    assertEquals("Food", result.get(0).name());
    assertEquals(2, result.get(1).categoryId());
    assertEquals("Transportation", result.get(1).name());
  }

  @Test
  void findById_shouldReturnCategory_whenIdExists() {
    Integer categoryId = 1;
    CategoryEntity entity = new CategoryEntity(categoryId, "Food", "#fffff");

    when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(entity));

    CategoryResponseDTO result = categoryService.findById(categoryId);

    assertEquals(categoryId, result.categoryId());
    assertEquals("Food", result.name());
    assertEquals("#fffff", result.color());
  }

  @Test
  void findById_shouldThrowCategoryNotFoundException_whenIdDoesNotExist() {
    Integer categoryId = 99;
    when(categoryRepository.findById(categoryId)).thenReturn(Optional.empty());

    assertThrows(CategoryNotFoundException.class, () -> categoryService.findById(categoryId));
  }
}
