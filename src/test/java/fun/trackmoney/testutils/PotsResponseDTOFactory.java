package fun.trackmoney.testutils;

import fun.trackmoney.pots.dtos.PotsResponseDTO;
import fun.trackmoney.pots.enums.ColorPick;

import java.math.BigDecimal;

public class PotsResponseDTOFactory {

    public static PotsResponseDTO defaultPotResponse() {
        return new PotsResponseDTO(
            1L,
            "Meu Cofrinho",
            BigDecimal.valueOf(1000),
            BigDecimal.valueOf(150),
            ColorPick.DARK_BLUE.getHex()
        );
    }

    public static PotsResponseDTO vacationPotResponse() {
        return new PotsResponseDTO(
            2L,
            "Viagem para o nordeste",
            BigDecimal.valueOf(5000),
            BigDecimal.valueOf(500),
            ColorPick.DARK_BLUE.getHex()
        );
    }

    public static PotsResponseDTO customPotResponse(
            Long potId,
            String name,
            BigDecimal targetAmount,
            BigDecimal currentAmount,
            String color
    ) {
        return new PotsResponseDTO(
                potId,
                name,
                targetAmount,
                currentAmount,
                color
        );
    }
}
