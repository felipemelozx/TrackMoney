package fun.trackmoney.account.controller;

import fun.trackmoney.account.dtos.AccountRequestDTO;
import fun.trackmoney.account.dtos.AccountResponseDTO;
import fun.trackmoney.account.dtos.AccountUpdateRequestDTO;
import fun.trackmoney.account.service.AccountService;
import fun.trackmoney.utils.AuthUtils;
import fun.trackmoney.utils.response.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/accounts")
public class AccountController {

  private final AccountService accountService;
  private final AuthUtils authUtils;

  public AccountController(AccountService accountService, AuthUtils authUtils) {
    this.accountService = accountService;
    this.authUtils = authUtils;
  }

  @PostMapping
  public ResponseEntity<ApiResponse<AccountResponseDTO>> createAccount(@RequestBody AccountRequestDTO dto) {
    AccountResponseDTO createdAccount = accountService.createAccount(dto);
    return ResponseEntity.status(HttpStatus.CREATED).body(
        ApiResponse.<AccountResponseDTO>success()
            .message("Account successfully created.")
            .data(createdAccount)
            .build()
        );
  }


  @GetMapping
  public ResponseEntity<ApiResponse<List<AccountResponseDTO>>> findAllAccounts() {
    UUID email = authUtils.getCurrentUser().getUserId();
    List<AccountResponseDTO> accounts = accountService.findAllAccount(email);
    return ResponseEntity.ok(
        ApiResponse.<List<AccountResponseDTO>>success()
            .message("Account list retrieved successfully.")
            .data(accounts)
            .build()
    );
  }


  @GetMapping("/{id}")
  public ResponseEntity<ApiResponse<AccountResponseDTO>> findAccountById(@PathVariable Integer id) {
    AccountResponseDTO account = accountService.findAccountById(id);
    return ResponseEntity.ok(
        ApiResponse.<AccountResponseDTO>success()
            .message("Account retrieved successfully.")
            .data(account)
            .build()
    );
  }

  @PutMapping("/{id}")
  public ResponseEntity<ApiResponse<AccountResponseDTO>> updateAccountById(@PathVariable Integer id,
                                                                           @RequestBody AccountUpdateRequestDTO dto) {
    AccountResponseDTO updatedAccount = accountService.updateAccountById(id, dto);
    return ResponseEntity.ok(
        ApiResponse.<AccountResponseDTO>success()
            .message("Account updated successfully.")
            .data(updatedAccount)
            .build()
    );
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<ApiResponse<Void>> deleteAccountById(@PathVariable Integer id) {
    accountService.deleteById(id);
    return ResponseEntity.ok(
        ApiResponse.<Void>success()
            .message("Account deleted successfully.")
            .build()
    );
  }
}
