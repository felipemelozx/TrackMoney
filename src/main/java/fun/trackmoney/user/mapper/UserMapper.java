package fun.trackmoney.user.mapper;

import fun.trackmoney.user.dtos.UserRequestDTO;
import fun.trackmoney.user.dtos.UserResponseDTO;
import fun.trackmoney.user.entity.UserEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {
  UserResponseDTO userEntityToUserResponseDto(UserEntity user);
  UserEntity userRequestDTOToEntity(UserRequestDTO dto);
}