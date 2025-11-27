package fun.trackmoney.recurring.dtos;

import fun.trackmoney.enums.Frequency;
import fun.trackmoney.enums.TransactionType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record CreateRecurringRequest(

    @NotNull(message = "Frequency é obrigatória.")
    Frequency frequency,

    @NotNull(message = "CategoryId é obrigatório.")
    @Positive(message = "CategoryId deve ser um número positivo.")
    Integer categoryId,

    @NotNull(message = "TransactionType é obrigatório.")
    TransactionType transactionType,

    @NotNull(message = "O dia de recorrência não pode ser nulo.")
    LocalDateTime recurrenceDay,

    @NotNull(message = "Amount é obrigatório.")
    @DecimalMin(value = "0.00", inclusive = false, message = "Amount deve ser maior que 0.")
    BigDecimal amount,

    @Size(max = 255, message = "Description deve ter no máximo 255 caracteres.")
    String description,

    @Size(max = 50, message = "TransactionName deve ter no máximo 50 caracteres.")
    String transactionName

) {}
