package fun.trackmoney.testutils;

import fun.trackmoney.category.entity.CategoryEntity;

public class CategoryEntityFactory {

    public static CategoryEntity defaultCategory() {
        return new CategoryEntity(1, "Alimentação", "#FF5733");
    }

    public static CategoryEntity transportCategory() {
        return new CategoryEntity(2, "Transporte", "#33B5E5");
    }

    public static CategoryEntity customCategory(Integer categoryId, String name, String color) {
        return new CategoryEntity(categoryId, name, color);
    }
}
