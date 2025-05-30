package fun.trackmoney.category.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * Represents a category entity that is mapped to the {@code tb_category} table in the database.
 * Each category can be associated with transactions or other financial entities.
 */
@Entity
@Table(name = "tb_category")
public class CategoryEntity {

  /**
   * The unique ID of the category.
   * This is the primary key and is automatically generated.
   */
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer categoryId;

  /**
   * The name of the category.
   * This could represent the type or classification of a transaction or item.
   */
  private String name;

  private String color;
  /**
   * Default constructor for creating an instance of {@link CategoryEntity}.
   * This constructor is required for JPA persistence.
   */
  public CategoryEntity() {
  }

  /**
   * Full constructor to create an instance of {@link CategoryEntity}.
   *
   * @param categoryId The unique ID of the category.
   * @param name       The name of the category.
   */
  public CategoryEntity(Integer categoryId, String name) {
    this.categoryId = categoryId;
    this.name = name;
  }

  public CategoryEntity(Integer categoryId, String name, String color) {
    this(categoryId,name);
    this.color = color;
  }

  public String getColor() {
    return color;
  }

  public void setColor(String color) {
    this.color = color;
  }

  /**
   * Gets the category ID.
   *
   * @return The unique ID of the category.
   */
  public Integer getCategoryId() {
    return categoryId;
  }

  /**
   * Sets the category ID.
   *
   * @param categoryId The unique ID to set for the category.
   */
  public void setCategoryId(Integer categoryId) {
    this.categoryId = categoryId;
  }

  /**
   * Gets the name of the category.
   *
   * @return The name of the category.
   */
  public String getName() {
    return name;
  }

  /**
   * Sets the name of the category.
   *
   * @param name The name to set for the category.
   */
  public void setName(String name) {
    this.name = name;
  }
}
