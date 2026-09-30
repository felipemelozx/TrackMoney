package fun.trackmoney.controller;
import fun.trackmoney.controller.AccountController;

import fun.trackmoney.dto.account.AccountRequestDTO;
import fun.trackmoney.dto.account.AccountResponseDTO;
import fun.trackmoney.dto.account.AccountUpdateRequestDTO;
import fun.trackmoney.service.AccountService;
import fun.trackmoney.dto.user.UserResponseDTO;
import fun.trackmoney.entity.UserEntity;
import fun.trackmoney.utils.AuthUtils;
import fun.trackmoney.utils.response.ApiResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AccountControllerTest {

  @Mock
  private AccountService accountService;

  @Mock
  private AuthUtils authUtils;

  @InjectMocks
  private AccountController accountController;

  private UUID userId;
  private UserResponseDTO userResponseDTO;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    userId = UUID.randomUUID();
    userResponseDTO = new UserResponseDTO(userId, "user@example.com", "User Test");
  }

  @Test
  void testCreateAccount() {
    UserEntity user = new UserEntity(userId, "User Test", "user@example.com", "password");
    when(authUtils.getCurrentUser()).thenReturn(user);
    AccountRequestDTO requestDTO = new AccountRequestDTO(userId, "Conta Corrente", BigDecimal.valueOf(1000));
    AccountResponseDTO responseDTO = new AccountResponseDTO(1, userResponseDTO, "Conta Corrente", BigDecimal.valueOf(1000));

    when(accountService.createAccount(requestDTO, userId)).thenReturn(responseDTO);

    ResponseEntity<ApiResponse<AccountResponseDTO>> response = accountController.createAccount(requestDTO);

    assertEquals(HttpStatus.CREATED, response.getStatusCode());
    assertNotNull(response.getBody());
    assertTrue(response.getBody().isSuccess());
    assertEquals("Account successfully created.", response.getBody().getMessage());
    assertEquals(responseDTO, response.getBody().getData());
  }

  @Test
  void testFindAllAccounts() {
    List<AccountResponseDTO> accounts = List.of(
        new AccountResponseDTO(1, userResponseDTO, "Conta Corrente", BigDecimal.valueOf(1000)),
        new AccountResponseDTO(2, userResponseDTO, "Conta Poupança", BigDecimal.valueOf(500))
    );
    UUID uuid = UUID.randomUUID();
    UserEntity user = new UserEntity(uuid, "", "", "");
    when(authUtils.getCurrentUser()).thenReturn(user);
    when(accountService.findAllAccount(uuid)).thenReturn(accounts);

    ResponseEntity<ApiResponse<List<AccountResponseDTO>>> response = accountController.findAllAccounts();

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getBody());
    assertTrue(response.getBody().isSuccess());
    assertEquals("Account list retrieved successfully.", response.getBody().getMessage());
    assertEquals(accounts, response.getBody().getData());
  }

  @Test
  void testFindAccountById() {
    UserEntity user = new UserEntity(userId, "User Test", "user@example.com", "password");
    when(authUtils.getCurrentUser()).thenReturn(user);
    AccountResponseDTO responseDTO = new AccountResponseDTO(1, userResponseDTO, "Conta Corrente", BigDecimal.valueOf(1000));

    when(accountService.findAccountById(1, userId)).thenReturn(responseDTO);

    ResponseEntity<ApiResponse<AccountResponseDTO>> response = accountController.findAccountById(1);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getBody());
    assertTrue(response.getBody().isSuccess());
    assertEquals("Account retrieved successfully.", response.getBody().getMessage());
    assertEquals(responseDTO, response.getBody().getData());
  }

  @Test
  void testUpdateAccountById() {
    UserEntity user = new UserEntity(userId, "User Test", "user@example.com", "password");
    when(authUtils.getCurrentUser()).thenReturn(user);
    AccountUpdateRequestDTO updateDTO = new AccountUpdateRequestDTO("Nova Conta");
    AccountResponseDTO updatedResponse = new AccountResponseDTO(1, userResponseDTO, "Nova Conta", BigDecimal.valueOf(1000));

    when(accountService.updateAccountById(1, updateDTO, userId)).thenReturn(updatedResponse);

    ResponseEntity<ApiResponse<AccountResponseDTO>> response = accountController.updateAccountById(1, updateDTO);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getBody());
    assertTrue(response.getBody().isSuccess());
    assertEquals("Account updated successfully.", response.getBody().getMessage());
    assertEquals(updatedResponse, response.getBody().getData());
  }

  @Test
  void testDeleteAccountById() {
    UserEntity user = new UserEntity(userId, "User Test", "user@example.com", "password");
    when(authUtils.getCurrentUser()).thenReturn(user);
    doNothing().when(accountService).deleteById(1, userId);

    ResponseEntity<ApiResponse<Void>> response = accountController.deleteAccountById(1);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    verify(accountService, times(1)).deleteById(1, userId);
  }
}
