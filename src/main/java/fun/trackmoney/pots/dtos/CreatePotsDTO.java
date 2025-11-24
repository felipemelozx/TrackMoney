package fun.trackmoney.pots.dtos;

import fun.trackmoney.pots.enums.ColorPick;
import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;

import java.math.BigDecimal;

public record CreatePotsDTO(@Length(min = 3, max = 30)
                            String name,
                            @NotNull
                            BigDecimal targetAmount,
                            @NotNull
                            ColorPick color) {
}
