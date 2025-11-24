package fun.trackmoney.testutils;

import fun.trackmoney.pots.dtos.PotsResponseDTO;
import fun.trackmoney.pots.enums.ColorPick;

public class PotsResponseDTOFactory {

    public static PotsResponseDTO defaultPotResponse() {
        return new PotsResponseDTO(
            1L,
            "Meu Cofrinho",
            "Guardando dinheiro para emergências",
            1000L,
            150L,
            ColorPick.DARK_BLUE.getHex()
        );
    }

    public static PotsResponseDTO vacationPotResponse() {
        return new PotsResponseDTO(
            2L,
            "Férias",
            "Viagem para o nordeste",
            5000L,
            500L,
            ColorPick.DARK_BLUE.getHex()
        );
    }

    public static PotsResponseDTO customPotResponse(
            Long potId,
            String name,
            String description,
            Long targetAmount,
            Long currentAmount,
            String color
    ) {
        return new PotsResponseDTO(
                potId,
                name,
                description,
                targetAmount,
                currentAmount,
                color
        );
    }
}
