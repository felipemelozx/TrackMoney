package fun.trackmoney.budget.dtos;

import jakarta.validation.constraints.Min;


public record BudgetCreateDTO(@Min(1)
                              Integer categoryId,
                              @Min(1)
                              short percent) {
}
