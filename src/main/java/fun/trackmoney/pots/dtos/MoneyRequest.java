package fun.trackmoney.pots.dtos;

import fun.trackmoney.enums.TransactionType;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record MoneyRequest(@NotNull  TransactionType type, BigDecimal amount) {}