package fun.trackmoney.service;

import fun.trackmoney.entity.CategoryEntity;
import fun.trackmoney.repository.CategoryRepository;
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
    return categoryRepository.findById(categoryId).orElse(null);
  }
}
