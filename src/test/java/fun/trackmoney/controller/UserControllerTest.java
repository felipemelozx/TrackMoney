package fun.trackmoney.controller;
import fun.trackmoney.controller.UserController;

import fun.trackmoney.dto.user.DeleteAccountRequestDTO;
import fun.trackmoney.dto.user.UserResponseDTO;
import fun.trackmoney.entity.UserEntity;
import fun.trackmoney.service.UserService;
import fun.trackmoney.testutils.UserEntityFactory;
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
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

  @InjectMocks
  UserController userController;

  @Mock
  AuthUtils authUtils;

  @Mock
  UserService userService;

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

  @Test
  void deleteUser_shouldReturn204NoContent_whenPasswordIsValid() {
    UserEntity user = UserEntityFactory.defaultUser();
    DeleteAccountRequestDTO request = new DeleteAccountRequestDTO("password123");

    when(userService.deleteAccount(user, request.password())).thenReturn(true);

    ResponseEntity<ApiResponse<Void>> response = userController.deleteUser(request, user);

    assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    assertNull(response.getBody());
    verify(userService, times(1)).deleteAccount(user, request.password());
  }

  @Test
  void deleteUser_shouldReturn400BadRequest_whenPasswordIsInvalid() {
    UserEntity user = UserEntityFactory.defaultUser();
    DeleteAccountRequestDTO request = new DeleteAccountRequestDTO("wrong-password");

    when(userService.deleteAccount(user, request.password())).thenReturn(false);

    ResponseEntity<ApiResponse<Void>> response = userController.deleteUser(request, user);

    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    assertNotNull(response.getBody());
    assertFalse(response.getBody().isSuccess());
    assertEquals("Password", response.getBody().getErrors().get(0).getField());
    verify(userService, times(1)).deleteAccount(user, request.password());
  }
}
