package fun.trackmoney.mapper;

import fun.trackmoney.dto.user.UserRequestDTO;
import fun.trackmoney.dto.user.UserResponseDTO;
import fun.trackmoney.entity.UserEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {
  UserResponseDTO userEntityToUserResponseDto(UserEntity user);

  UserEntity userRequestDTOToEntity(UserRequestDTO dto);
}