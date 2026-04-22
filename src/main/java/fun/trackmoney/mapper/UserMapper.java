package fun.trackmoney.mapper;

import fun.trackmoney.dto.user.UserRequestDTO;
import fun.trackmoney.dto.user.UserResponseDTO;
import fun.trackmoney.entity.UserEntity;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

  public UserResponseDTO userEntityToUserResponseDto(UserEntity user) {
    if (user == null) {
      return null;
    }
    return new UserResponseDTO(
        user.getUserId(),
        user.getName(),
        user.getEmail()
    );
  }

  public UserEntity userRequestDTOToEntity(UserRequestDTO dto) {
    if (dto == null) {
      return null;
    }
    UserEntity entity = new UserEntity();
    entity.setName(dto.name());
    entity.setEmail(dto.email());
    entity.setPassword(dto.password());
    return entity;
  }

  public UserEntity userResponseDtoToEntity(UserResponseDTO dto) {
    if (dto == null) {
      return null;
    }
    UserEntity entity = new UserEntity();
    entity.setUserId(dto.userId());
    entity.setName(dto.name());
    entity.setEmail(dto.email());
    return entity;
  }
}
