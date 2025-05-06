package fun.trackmoney.user.dtos;

import java.util.UUID;

public record UserResponseDTO(UUID userId,
                              String name,
                              String email) {
}
