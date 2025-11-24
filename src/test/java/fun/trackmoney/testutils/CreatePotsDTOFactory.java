package fun.trackmoney.testutils;

import fun.trackmoney.pots.dtos.CreatePotsDTO;
import fun.trackmoney.pots.enums.ColorPick;

public class CreatePotsDTOFactory {

    public static CreatePotsDTO defaultCreatePot() {
        return new CreatePotsDTO(
            "Meu Cofrinho",
            1000L,
            ColorPick.DARK_BLUE
        );
    }

    public static CreatePotsDTO vacationPot() {
        return new CreatePotsDTO(
            "Férias",
            5000L,
            ColorPick.DARK_BLUE
        );
    }

    public static CreatePotsDTO customCreatePot(
            String name,
            Long targetAmount,
            ColorPick color
    ) {
        return new CreatePotsDTO(
            name,
            targetAmount,
            color
        );
    }
}
