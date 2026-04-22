package fun.trackmoney.service;

import fun.trackmoney.dto.category.CategoryResponseDTO;
import fun.trackmoney.entity.CategoryEntity;
import fun.trackmoney.exception.CategoryNotFoundException;
import fun.trackmoney.mapper.CategoryMapper;
import fun.trackmoney.repository.CategoryRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryService {

  private final CategoryRepository categoryRepository;
  private final CategoryMapper categoryMapper;

  public CategoryService(CategoryRepository categoryRepository, CategoryMapper categoryMapper) {
    this.categoryRepository = categoryRepository;
    this.categoryMapper = categoryMapper;
  }

  public List<CategoryResponseDTO> findAll() {
    return categoryMapper.categoryEntityListToResponseList(categoryRepository.findAll());
  }

  public CategoryResponseDTO findById(Integer categoryId) {
    CategoryEntity entity = categoryRepository.findById(categoryId)
        .orElseThrow(() -> new CategoryNotFoundException("Category not found."));
    return categoryMapper.categoryEntityToCategoryResponse(entity);
  }

  public CategoryEntity findEntityById(Integer categoryId) {
    return categoryRepository.findById(categoryId).orElse(null);
  }
}
