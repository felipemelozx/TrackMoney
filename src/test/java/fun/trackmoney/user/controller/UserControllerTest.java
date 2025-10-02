package fun.trackmoney.user.controller;

import fun.trackmoney.user.dtos.UserResponseDTO;
import fun.trackmoney.user.entity.UserEntity;
import fun.trackmoney.utils.AuthUtils;
import fun.trackmoney.utils.response.ApiResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

  @InjectMocks
  UserController userController;

  @Mock
  AuthUtils authUtils;

  @Test
  void shouldReturnUserInfoWhenAuthUtilsReturnsValidUser(){
    UserEntity userMock = new UserEntity(UUID.randomUUID(), "some name", "some@email.com", "somePassword", true);
    when(authUtils.getCurrentUser()).thenReturn(userMock);

    ResponseEntity<ApiResponse<UserResponseDTO>> response = userController.getUserInfo();

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getBody());
    ApiResponse<UserResponseDTO> body = response.getBody();
    assertEquals("Success to get user info.", body.getMessage());
    assertTrue(body.isSuccess());
    assertEquals(UserResponseDTO.class, body.getData().getClass());
    assertEquals(userMock.getName(), body.getData().name());
    assertEquals(userMock.getEmail(), body.getData().email());
    assertEquals(userMock.getUserId(), body.getData().userId());
  }
}
