package fun.trackmoney.budget.dtos;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;


public record BudgetCreateDTO(@Min(1)
                              Integer categoryId,
                              @Min(1)
                              short percent) {
}
