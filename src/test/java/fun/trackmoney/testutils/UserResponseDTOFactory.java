package fun.trackmoney.testutils;

import fun.trackmoney.user.dtos.UserResponseDTO;

import java.util.UUID;

public class UserResponseDTOFactory {

    public static UserResponseDTO defaultUserResponse() {
        return new UserResponseDTO(
            UUID.randomUUID(),
            "Usuário Teste",
            "teste@example.com"
        );
    }

    public static UserResponseDTO customUserResponse(UUID userId, String name, String email) {
        return new UserResponseDTO(userId, name, email);
    }
}
