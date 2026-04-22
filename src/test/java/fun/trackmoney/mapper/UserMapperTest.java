package fun.trackmoney.mapper;

import fun.trackmoney.dto.user.UserRequestDTO;
import fun.trackmoney.dto.user.UserResponseDTO;
import fun.trackmoney.entity.UserEntity;
import fun.trackmoney.testutils.UserEntityFactory;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class UserMapperTest {

  private final UserMapper mapper = new UserMapper();

  @Test
  void userEntityToUserResponseDto_shouldMapAllFields() {
    UserEntity entity = UserEntityFactory.defaultUser();

    UserResponseDTO dto = mapper.userEntityToUserResponseDto(entity);

    assertNotNull(dto);
    assertEquals(entity.getUserId(), dto.userId());
    assertEquals(entity.getName(), dto.name());
    assertEquals(entity.getEmail(), dto.email());
  }

  @Test
  void userEntityToUserResponseDto_shouldReturnNullWhenInputIsNull() {
    assertNull(mapper.userEntityToUserResponseDto(null));
  }

  @Test
  void userRequestDTOToEntity_shouldMapAllFields() {
    UserRequestDTO dto = new UserRequestDTO("John", "john@email.com", "pass123");

    UserEntity entity = mapper.userRequestDTOToEntity(dto);

    assertNotNull(entity);
    assertEquals("John", entity.getName());
    assertEquals("john@email.com", entity.getEmail());
    assertEquals("pass123", entity.getPassword());
  }

  @Test
  void userRequestDTOToEntity_shouldReturnNullWhenInputIsNull() {
    assertNull(mapper.userRequestDTOToEntity(null));
  }

  @Test
  void userResponseDtoToEntity_shouldMapAllFields() {
    UUID userId = UUID.randomUUID();
    UserResponseDTO dto = new UserResponseDTO(userId, "John", "john@email.com");

    UserEntity entity = mapper.userResponseDtoToEntity(dto);

    assertNotNull(entity);
    assertEquals(userId, entity.getUserId());
    assertEquals("John", entity.getName());
    assertEquals("john@email.com", entity.getEmail());
  }

  @Test
  void userResponseDtoToEntity_shouldReturnNullWhenInputIsNull() {
    assertNull(mapper.userResponseDtoToEntity(null));
  }
}
