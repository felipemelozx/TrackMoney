package fun.trackmoney.testutils;

import fun.trackmoney.dto.budget.BudgetHistoryResponseDTO;
import fun.trackmoney.enums.BudgetStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class BudgetHistoryResponseDTOFactory {

    public static BudgetHistoryResponseDTO defaultBudgetHistoryResponse() {
        return new BudgetHistoryResponseDTO(
            1,
            1,
            CategoryEntityFactory.defaultCategory(),
            (short) 1,
            2025,
            (short) 50,
            new BigDecimal("5000.0000"),
            new BigDecimal("2500.0000"),
            new BigDecimal("2500.0000"),
            new BigDecimal("10000.0000"),
            List.of(),
            BudgetStatus.WITHIN_LIMIT,
            LocalDateTime.of(2025, 1, 15, 10, 0)
        );
    }

    public static BudgetHistoryResponseDTO exceededBudgetHistoryResponse() {
        return new BudgetHistoryResponseDTO(
            2,
            1,
            CategoryEntityFactory.defaultCategory(),
            (short) 2,
            2025,
            (short) 50,
            new BigDecimal("5000.0000"),
            new BigDecimal("6000.0000"),
            new BigDecimal("-1000.0000"),
            new BigDecimal("10000.0000"),
            List.of(),
            BudgetStatus.EXCEEDED,
            LocalDateTime.of(2025, 2, 15, 10, 0)
        );
    }
}
