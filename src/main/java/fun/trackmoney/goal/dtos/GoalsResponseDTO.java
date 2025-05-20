package fun.trackmoney.goal.dtos;

import fun.trackmoney.account.dtos.AccountResponseDTO;

import java.math.BigDecimal;

public record GoalsResponseDTO(Integer goalsId,
                               String goal,
                               AccountResponseDTO account,
                               BigDecimal targetAmount,
                               BigDecimal currentAmount,
                               Integer progress) {
}
