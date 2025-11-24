package fun.trackmoney.testutils;

import fun.trackmoney.pots.dtos.CreatePotsDTO;
import fun.trackmoney.pots.enums.ColorPick;

import java.math.BigDecimal;

public class CreatePotsDTOFactory {

    public static CreatePotsDTO defaultCreatePot() {
        return new CreatePotsDTO(
            "Meu Cofrinho",
            BigDecimal.valueOf(1000),
            ColorPick.DARK_BLUE
        );
    }

    public static CreatePotsDTO vacationPot() {
        return new CreatePotsDTO(
            "Férias",
            BigDecimal.valueOf(5000),
            ColorPick.DARK_BLUE
        );
    }

    public static CreatePotsDTO customCreatePot(
            String name,
            BigDecimal targetAmount,
            ColorPick color
    ) {
        return new CreatePotsDTO(
            name,
            targetAmount,
            color
        );
    }
}
