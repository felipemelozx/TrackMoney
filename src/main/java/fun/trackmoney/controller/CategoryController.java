package fun.trackmoney.controller;

import fun.trackmoney.controller.docs.CategoryControllerDocs;
import fun.trackmoney.dto.category.CategoryResponseDTO;
import fun.trackmoney.service.CategoryService;
import fun.trackmoney.utils.response.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("category")
public class CategoryController implements CategoryControllerDocs {

  private final CategoryService categoryService;

  public CategoryController(CategoryService categoryService) {
    this.categoryService = categoryService;
  }

  @Override
  @GetMapping("/findAll")
  public ResponseEntity<ApiResponse<List<CategoryResponseDTO>>> findAll() {
    return ResponseEntity.ok().body(
      ApiResponse.<List<CategoryResponseDTO>>success()
          .message("Categories")
          .data(categoryService.findAll())
          .build()
    );
  }

  @Override
  @GetMapping("/{id}")
  public ResponseEntity<ApiResponse<CategoryResponseDTO>> findById(@PathVariable Integer id) {
    return ResponseEntity.ok().body(
      ApiResponse.<CategoryResponseDTO>success()
          .message("Category found")
          .data(categoryService.findById(id))
          .build()
    );
  }
}
