package fun.trackmoney.dto.user;

import java.util.UUID;

public record UserResponseDTO(UUID userId,
                              String name,
                              String email) {
}
