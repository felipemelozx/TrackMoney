package fun.trackmoney.category.controller;

import fun.trackmoney.category.entity.CategoryEntity;
import fun.trackmoney.category.exception.CategoryNotFoundException;
import fun.trackmoney.category.service.CategoryService;
import fun.trackmoney.utils.response.ApiResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CategoryControllerTest {

  @Mock
  private CategoryService categoryService;

  @InjectMocks
  private CategoryController categoryController;

  @Test
  void findAll_shouldReturnOkResponseWithCategoryList() {
    // Arrange
    CategoryEntity category1 = new CategoryEntity();
    category1.setCategoryId(1);
    category1.setName("Food");

    CategoryEntity category2 = new CategoryEntity();
    category2.setCategoryId(2);
    category2.setName("Transportation");

    List<CategoryEntity> categories = Arrays.asList(category1, category2);
    when(categoryService.findAll()).thenReturn(categories);

    // Act
    ResponseEntity<ApiResponse<List<CategoryEntity>>> response = categoryController.findAll();

    // Assert
    assertEquals(HttpStatus.OK, response.getStatusCode());
    ApiResponse<List<CategoryEntity>> apiResponse = response.getBody();
    assertTrue(apiResponse.isSuccess());
    assertEquals("Category", apiResponse.getMessage());
    assertEquals(categories.size(), apiResponse.getData().size());
    assertTrue(apiResponse.getData().containsAll(categories));
    assertEquals(null, apiResponse.getErrors());
  }

  @Test
  void findById_shouldReturnOkResponseWithCategory_whenIdExists() {
    // Arrange
    Integer categoryId = 1;
    CategoryEntity category = new CategoryEntity();
    category.setCategoryId(categoryId);
    category.setName("Food");

    when(categoryService.findById(categoryId)).thenReturn(category);

    // Act
    ResponseEntity<ApiResponse<CategoryEntity>> response = categoryController.findById(categoryId);

    // Assert
    assertEquals(HttpStatus.OK, response.getStatusCode());
    ApiResponse<CategoryEntity> apiResponse = response.getBody();
    assertTrue(apiResponse.isSuccess());
    assertEquals("Category", apiResponse.getMessage());
    assertEquals(category.getCategoryId(), apiResponse.getData().getCategoryId());
    assertEquals(category.getName(), apiResponse.getData().getName());
    assertEquals(null, apiResponse.getErrors());
  }

  @Test
  void findById_shouldReturnNotFound_whenCategoryNotFound() {
    // Arrange
    Integer categoryId = 99;
    when(categoryService.findById(categoryId)).thenThrow(new CategoryNotFoundException("Category not found."));

    // Act
    try {
      categoryController.findById(categoryId);
    } catch (CategoryNotFoundException e) {
      // Assert that the exception is thrown.
      assertEquals("Category not found.", e.getMessage());
      return;
    }
    // Should not reach here if the exception is thrown.
    assertTrue(false, "CategoryNotFoundException should have been thrown.");
  }
}