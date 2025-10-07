package fun.trackmoney.category.service;

import fun.trackmoney.category.entity.CategoryEntity;
import fun.trackmoney.category.repository.CategoryRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
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

  @InjectMocks
  private CategoryService categoryService;

  @Test
  void findAll_shouldReturnAllCategories() {
    CategoryEntity category1 = new CategoryEntity();
    category1.setCategoryId(1);
    category1.setName("Food");

    CategoryEntity category2 = new CategoryEntity();
    category2.setCategoryId(2);
    category2.setName("Transportation");

    List<CategoryEntity> expectedCategories = Arrays.asList(category1, category2);
    when(categoryRepository.findAll()).thenReturn(expectedCategories);

    List<CategoryEntity> actualCategories = categoryService.findAll();

    assertEquals(expectedCategories.size(), actualCategories.size());
    assertTrue(actualCategories.containsAll(expectedCategories));
  }

  @Test
  void findById_shouldReturnCategory_whenIdExists() {
    Integer categoryId = 1;
    CategoryEntity expectedCategory = new CategoryEntity();
    expectedCategory.setCategoryId(categoryId);
    expectedCategory.setName("Food");
    expectedCategory.setColor("#fffff");

    when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(expectedCategory));

    CategoryEntity actualCategory = categoryService.findById(categoryId);

    assertEquals(expectedCategory.getCategoryId(), actualCategory.getCategoryId());
    assertEquals(expectedCategory.getName(), actualCategory.getName());
    assertEquals(expectedCategory.getColor(), actualCategory.getColor());
  }

  @Test
  void findById_shouldThrowCategoryNotFoundException_whenIdDoesNotExist() {
    Integer categoryId = 99;
    when(categoryRepository.findById(categoryId)).thenReturn(Optional.empty());

    assertNull(categoryService.findById(categoryId));
  }
}