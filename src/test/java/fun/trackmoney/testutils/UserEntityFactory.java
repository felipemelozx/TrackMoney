package fun.trackmoney.testutils;

import fun.trackmoney.account.entity.AccountEntity;
import fun.trackmoney.user.entity.UserEntity;

import java.util.UUID;

public class UserEntityFactory {

  public static UserEntity defaultUser() {
    return new UserEntity(
        UUID.randomUUID(),
        "John Doe",
        "johndoe@example.com",
        "password123",
        AccountEntityFactory.defaultAccount(),
        true
    );
  }

  public static UserEntity inactiveUser() {
    return new UserEntity(
        UUID.randomUUID(),
        "Jane Doe",
        "janedoe@example.com",
        "password123",
        AccountEntityFactory.defaultAccount(),
        false
    );
  }

  public static UserEntity customUser(UUID userId, String name, String email, String password, boolean isActive, AccountEntity account) {
    return new UserEntity(userId, name, email, password, account, isActive);
  }
}
