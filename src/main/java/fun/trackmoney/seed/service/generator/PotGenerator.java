package fun.trackmoney.seed.service.generator;

import fun.trackmoney.entity.AccountEntity;
import fun.trackmoney.entity.PotsEntity;
import fun.trackmoney.pots.enums.ColorPick;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

@Component
public class PotGenerator {

  public List<PotsEntity> generate(AccountEntity account) {
    List<PotsEntity> pots = new ArrayList<>();

    pots.add(createPot(account, "Férias 2024", 5000.0, 0.90, ColorPick.DARK_BLUE));
    pots.add(createPot(account, "Carro Novo", 30000.0, 0.10, ColorPick.GRAPHITE_BLUE));
    pots.add(createPot(account, "Reserva de Emergência", 20000.0, 0.50, ColorPick.GRAYISH_BLUE));
    pots.add(createPot(account, "PS5", 4000.0, 0.02, ColorPick.SOFT_BLACK));

    return pots;
  }

  private PotsEntity createPot(AccountEntity account, String name, double target,
                              double completion, ColorPick color) {
    BigDecimal targetAmount = BigDecimal.valueOf(target);
    BigDecimal currentAmount = targetAmount.multiply(BigDecimal.valueOf(completion))
        .setScale(2, RoundingMode.HALF_UP);

    PotsEntity pot = new PotsEntity();
    pot.setAccount(account);
    pot.setName(name);
    pot.setTargetAmount(targetAmount);
    pot.setCurrentAmount(currentAmount);
    pot.setColor(color);

    return pot;
  }
}
