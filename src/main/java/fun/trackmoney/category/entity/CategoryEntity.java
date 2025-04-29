package fun.trackmoney.category.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "tb_category")
public class CategoryEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer categoryId;
  private String name;

  public CategoryEntity() {
  }

  public CategoryEntity(Integer categoryId, String name) {
    this.categoryId = categoryId;
    this.name = name;
  }

  public Integer getCategoryId() {
    return categoryId;
  }

  public void setCategoryId(Integer categoryId) {
    this.categoryId = categoryId;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }
}
