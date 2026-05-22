package fun.trackmoney.controller;
import fun.trackmoney.controller.CategoryController;

import fun.trackmoney.dto.category.CategoryResponseDTO;
import fun.trackmoney.exception.CategoryNotFoundException;
import fun.trackmoney.service.CategoryService;
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
    CategoryResponseDTO category1 = new CategoryResponseDTO(1, "Food", "#FF5733");
    CategoryResponseDTO category2 = new CategoryResponseDTO(2, "Transportation", "#33B5E5");

    List<CategoryResponseDTO> categories = Arrays.asList(category1, category2);
    when(categoryService.findAll()).thenReturn(categories);

    ResponseEntity<ApiResponse<List<CategoryResponseDTO>>> response = categoryController.findAll();

    assertEquals(HttpStatus.OK, response.getStatusCode());
    ApiResponse<List<CategoryResponseDTO>> apiResponse = response.getBody();
    assertTrue(apiResponse.isSuccess());
    assertEquals("Categories", apiResponse.getMessage());
    assertEquals(categories.size(), apiResponse.getData().size());
    assertTrue(apiResponse.getData().containsAll(categories));
    assertTrue(apiResponse.getErrors().isEmpty());
  }

  @Test
  void findById_shouldReturnOkResponseWithCategory_whenIdExists() {
    Integer categoryId = 1;
    CategoryResponseDTO category = new CategoryResponseDTO(categoryId, "Food", "#FF5733");

    when(categoryService.findById(categoryId)).thenReturn(category);

    ResponseEntity<ApiResponse<CategoryResponseDTO>> response = categoryController.findById(categoryId);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    ApiResponse<CategoryResponseDTO> apiResponse = response.getBody();
    assertTrue(apiResponse.isSuccess());
    assertEquals("Category found", apiResponse.getMessage());
    assertEquals(category.categoryId(), apiResponse.getData().categoryId());
    assertEquals(category.name(), apiResponse.getData().name());
    assertTrue(apiResponse.getErrors().isEmpty());
  }

  @Test
  void findById_shouldReturnNotFound_whenCategoryNotFound() {
    Integer categoryId = 99;
    when(categoryService.findById(categoryId)).thenThrow(new CategoryNotFoundException("Category not found."));

    try {
      categoryController.findById(categoryId);
    } catch (CategoryNotFoundException e) {
      assertEquals("Category not found.", e.getMessage());
      return;
    }
    assertTrue(false, "CategoryNotFoundException should have been thrown.");
  }
}
