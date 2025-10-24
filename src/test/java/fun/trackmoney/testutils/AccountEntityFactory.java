package fun.trackmoney.testutils;

import fun.trackmoney.account.entity.AccountEntity;
import fun.trackmoney.user.entity.UserEntity;

import java.math.BigDecimal;
import java.util.UUID;

public class AccountEntityFactory {

    public static AccountEntity defaultAccount() {
        return new AccountEntity(
            1,
            defaultUser(),
            "Conta Corrente",
            BigDecimal.valueOf(1000.00)
        );
    }

    public static AccountEntity savingsAccount() {
        return new AccountEntity(
            2,
            defaultUser(),
            "Poupança",
            BigDecimal.valueOf(5000.00)
        );
    }

    public static AccountEntity customAccount(
            Integer accountId,
            UserEntity user,
            String name,
            BigDecimal balance
    ) {
        return new AccountEntity(accountId, user, name, balance);
    }

    private static UserEntity defaultUser() {
        UserEntity user = new UserEntity();
        user.setUserId(UUID.randomUUID());
        user.setName("Usuário Teste");
        user.setEmail("teste@example.com");
        user.setPassword("senha123");
        return user;
    }
}
