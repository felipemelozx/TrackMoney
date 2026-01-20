package fun.trackmoney.budget.dtos;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record BudgetHistoryGenerateDTO(
    @NotNull(message = "Month is required")
    @Min(value = 1, message = "Month must be between 1 and 12")
    @Max(value = 12, message = "Month must be between 1 and 12")
    int month,

    @NotNull(message = "Year is required")
    @Min(value = 2020, message = "Year must be 2020 or later")
    int year
) {}
