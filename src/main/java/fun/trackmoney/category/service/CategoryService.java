package fun.trackmoney.category.service;

import fun.trackmoney.category.entity.CategoryEntity;
import fun.trackmoney.category.exception.CategoryNotFoundException;
import fun.trackmoney.category.repository.CategoryRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryService {

  private final CategoryRepository categoryRepository;

  public CategoryService(CategoryRepository categoryRepository) {
    this.categoryRepository = categoryRepository;
  }

  public List<CategoryEntity> findAll() {
    return categoryRepository.findAll();
  }

  public CategoryEntity findById(Integer categoryId) {
    return categoryRepository.findById(categoryId)
        .orElseThrow(() -> {
          throw new CategoryNotFoundException("Category not found.");
        });
  }
}
