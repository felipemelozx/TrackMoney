package fun.trackmoney.testutils;

import fun.trackmoney.budget.dtos.BudgetCreateDTO;

public class BudgetCreateDTOFactory {

    public static BudgetCreateDTO defaultDTO() {
        return new BudgetCreateDTO(
            1,
            (short) 50
        );
    }

    public static BudgetCreateDTO secondaryDTO() {
        return new BudgetCreateDTO(
            2,
            (short) 30
        );
    }

    public static BudgetCreateDTO customDTO(Integer categoryId, short percent) {
        return new BudgetCreateDTO(categoryId, percent);
    }
}
