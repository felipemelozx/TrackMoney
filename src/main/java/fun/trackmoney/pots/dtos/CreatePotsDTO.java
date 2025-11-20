package fun.trackmoney.pots.dtos;

import fun.trackmoney.pots.enums.ColorPick;
import jakarta.validation.constraints.Positive;
import org.hibernate.validator.constraints.Length;

public record CreatePotsDTO(@Length(min = 3, max = 30)
                            String name,
                            @Positive
                            Long targetAmount,
                            ColorPick color) {
}
