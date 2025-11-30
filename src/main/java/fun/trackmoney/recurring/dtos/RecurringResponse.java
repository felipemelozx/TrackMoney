package fun.trackmoney.recurring.dtos;

import fun.trackmoney.category.entity.CategoryEntity;
import fun.trackmoney.enums.Frequency;
import fun.trackmoney.enums.TransactionType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record RecurringResponse(Long id,
                                Frequency frequency,
                                CategoryEntity category,
                                TransactionType transactionType,
                                LocalDateTime nextDate,
                                LocalDateTime lastDate,
                                BigDecimal amount,
                                String description,
                                String transactionName) {
}
