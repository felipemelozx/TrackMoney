package fun.trackmoney.testutils;

import fun.trackmoney.account.entity.AccountEntity;
import fun.trackmoney.pots.entity.PotsEntity;
import fun.trackmoney.pots.enums.ColorPick;

import java.math.BigDecimal;

public class PotsEntityFactory {

    public static PotsEntity defaultPot() {
        return new PotsEntity(
            1L,
            "Meu Cofrinho",
            BigDecimal.valueOf(1000.00),
            BigDecimal.valueOf(150.00),
            AccountEntityFactory.defaultAccount(),
            ColorPick.DARK_BLUE
        );
    }

    public static PotsEntity vacationPot() {
        return new PotsEntity(
            2L,
            "Férias",
            BigDecimal.valueOf(5000.00),
            BigDecimal.valueOf(500.00),
            AccountEntityFactory.defaultAccount(),
            ColorPick.DARK_BLUE
        );
    }

  public static PotsEntity withoutCurrentAmount() {
    return new PotsEntity(
        2L,
        "Férias",
        BigDecimal.valueOf(5000.00),
        BigDecimal.ZERO,
        AccountEntityFactory.defaultAccount(),
        ColorPick.DARK_BLUE
    );
  }

    public static PotsEntity customPot(
            Long potId,
            String name,
            BigDecimal targetAmount,
            BigDecimal currentAmount,
            AccountEntity account,
            ColorPick color
    ) {
        return new PotsEntity(
                potId,
                name,
                targetAmount,
                currentAmount,
                account,
                color
        );
    }
}
