package fun.trackmoney.category.controller;

import fun.trackmoney.category.entity.CategoryEntity;
import fun.trackmoney.category.service.CategoryService;
import fun.trackmoney.utils.response.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("category")
public class CategoryController {

  private final CategoryService categoryService;

  public CategoryController(CategoryService categoryService) {
    this.categoryService = categoryService;
  }

  @GetMapping("/findAll")
  public ResponseEntity<ApiResponse<List<CategoryEntity>>> findAll() {
    return ResponseEntity.ok().body(new ApiResponse<>(true, "Category", categoryService.findAll(), null));
  }

  @GetMapping("/{id}")
  public ResponseEntity<ApiResponse<CategoryEntity>> findById(@PathVariable Integer id) {
    return ResponseEntity.ok().body(new ApiResponse<>(true, "Category", categoryService.findById(id), null));
  }
}
