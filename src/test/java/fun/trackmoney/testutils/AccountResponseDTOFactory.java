package fun.trackmoney.testutils;

import fun.trackmoney.account.dtos.AccountResponseDTO;
import fun.trackmoney.user.dtos.UserResponseDTO;

import java.math.BigDecimal;

public class AccountResponseDTOFactory {

    public static AccountResponseDTO defaultAccountResponse() {
        return new AccountResponseDTO(
            1,
            UserResponseDTOFactory.defaultUserResponse(),
            "Conta Corrente",
            BigDecimal.valueOf(5000.00),
            true
        );
    }

    public static AccountResponseDTO savingsAccountResponse() {
        return new AccountResponseDTO(
            2,
            UserResponseDTOFactory.defaultUserResponse(),
            "Poupança",
            BigDecimal.valueOf(2000.00),
            false
        );
    }

    public static AccountResponseDTO customAccountResponse(
            Integer accountId,
            UserResponseDTO user,
            String name,
            BigDecimal balance,
            Boolean isAccountDefault
    ) {
        return new AccountResponseDTO(accountId, user, name, balance, isAccountDefault);
    }
}
