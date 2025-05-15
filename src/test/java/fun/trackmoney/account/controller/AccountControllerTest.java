package fun.trackmoney.account.controller;

import fun.trackmoney.account.dtos.AccountRequestDTO;
import fun.trackmoney.account.dtos.AccountResponseDTO;
import fun.trackmoney.account.dtos.AccountUpdateRequestDTO;
import fun.trackmoney.account.service.AccountService;
import fun.trackmoney.user.dtos.UserResponseDTO;
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
    AccountRequestDTO requestDTO = new AccountRequestDTO(userId, "Conta Corrente", BigDecimal.valueOf(1000), true);
    AccountResponseDTO responseDTO = new AccountResponseDTO(1, userResponseDTO, "Conta Corrente", BigDecimal.valueOf(1000), true);

    when(accountService.createAccount(requestDTO)).thenReturn(responseDTO);

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
        new AccountResponseDTO(1, userResponseDTO, "Conta Corrente", BigDecimal.valueOf(1000), true),
        new AccountResponseDTO(2, userResponseDTO, "Conta Poupança", BigDecimal.valueOf(500), false)
    );

    when(accountService.findAllAccount()).thenReturn(accounts);

    ResponseEntity<ApiResponse<List<AccountResponseDTO>>> response = accountController.findAllAccounts();

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getBody());
    assertTrue(response.getBody().isSuccess());
    assertEquals("Account list retrieved successfully.", response.getBody().getMessage());
    assertEquals(accounts, response.getBody().getData());
  }

  @Test
  void testFindAccountById() {
    AccountResponseDTO responseDTO = new AccountResponseDTO(1, userResponseDTO, "Conta Corrente", BigDecimal.valueOf(1000), true);

    when(accountService.findAccountById(1)).thenReturn(responseDTO);

    ResponseEntity<ApiResponse<AccountResponseDTO>> response = accountController.findAccountById(1);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getBody());
    assertTrue(response.getBody().isSuccess());
    assertEquals("Account retrieved successfully.", response.getBody().getMessage());
    assertEquals(responseDTO, response.getBody().getData());
  }

  @Test
  void testUpdateAccountById() {
    AccountUpdateRequestDTO updateDTO = new AccountUpdateRequestDTO("Nova Conta", false);
    AccountResponseDTO updatedResponse = new AccountResponseDTO(1, userResponseDTO, "Nova Conta", BigDecimal.valueOf(1000), false);

    when(accountService.updateAccountById(1, updateDTO)).thenReturn(updatedResponse);

    ResponseEntity<ApiResponse<AccountResponseDTO>> response = accountController.updateAccountById(1, updateDTO);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getBody());
    assertTrue(response.getBody().isSuccess());
    assertEquals("Account updated successfully.", response.getBody().getMessage());
    assertEquals(updatedResponse, response.getBody().getData());
  }

  @Test
  void testDeleteAccountById() {
    doNothing().when(accountService).deleteById(1);

    ResponseEntity<Void> response = accountController.deleteAccountById(1);

    assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    verify(accountService, times(1)).deleteById(1);
  }
}
