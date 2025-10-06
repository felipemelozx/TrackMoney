package fun.trackmoney.transaction.controller;

import fun.trackmoney.account.dtos.AccountResponseDTO;
import fun.trackmoney.enums.TransactionType;
import fun.trackmoney.testutils.CreateTransactionDTOBuilder;
import fun.trackmoney.testutils.TransactionResponseDTOFactory;
import fun.trackmoney.testutils.UserEntityFactory;
import fun.trackmoney.transaction.dto.BillResponseDTO;
import fun.trackmoney.transaction.dto.CreateTransactionDTO;
import fun.trackmoney.transaction.dto.TransactionResponseDTO;
import fun.trackmoney.transaction.dto.TransactionUpdateDTO;
import fun.trackmoney.transaction.dto.TransactionsError;
import fun.trackmoney.transaction.dto.internal.TransactionFailure;
import fun.trackmoney.transaction.dto.internal.TransactionResult;
import fun.trackmoney.transaction.dto.internal.TransactionSuccess;
import fun.trackmoney.transaction.service.TransactionService;
import fun.trackmoney.user.dtos.UserResponseDTO;
import fun.trackmoney.user.entity.UserEntity;
import fun.trackmoney.utils.response.ApiResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class TransactionControllerTest {

  @Mock
  private TransactionService transactionService;
  @InjectMocks
  private TransactionController transactionController;

  @Test
  void shouldReturnCreatedResponse_whenTransactionIsSuccessful() {
    UserEntity user = UserEntityFactory.defaultUser();
    CreateTransactionDTO dto = CreateTransactionDTOBuilder.incomeTransaction();
    TransactionResponseDTO responseDTO = TransactionResponseDTOFactory.incomeTransactionResponse();
    TransactionResult transactionResult = new TransactionSuccess(responseDTO);

    when(transactionService.createTransaction(dto, user.getUserId())).thenReturn(transactionResult);

    var response = transactionController.createTransaction(dto, user);

    assertEquals(HttpStatusCode.valueOf(201), response.getStatusCode());
    assertNotNull(response.getBody());
    assertTrue(response.getBody().isSuccess());
    assertEquals("Transfer created", response.getBody().getMessage());
    assertEquals("Salário mensal", response.getBody().getData().description());

    verify(transactionService, times(1)).createTransaction(dto, user.getUserId());
  }

  @Test
  void shouldReturnBadRequest_whenAccountNotFound() {
    UserEntity user = UserEntityFactory.defaultUser();
    CreateTransactionDTO dto = CreateTransactionDTOBuilder.incomeTransaction();
    TransactionResult transactionResult = new TransactionFailure(TransactionsError.ACCOUNT_NOT_FOUND);

    when(transactionService.createTransaction(dto, user.getUserId())).thenReturn(transactionResult);

    var response = transactionController.createTransaction(dto, user);

    assertEquals(HttpStatusCode.valueOf(400), response.getStatusCode());
    assertNotNull(response.getBody());
    assertFalse(response.getBody().isSuccess());
    assertEquals("Transaction create error.", response.getBody().getMessage());
    assertEquals("Account", response.getBody().getErrors().get(0).getField());
    assertEquals("Account not found to update balance", response.getBody().getErrors().get(0).getMessage());

    verify(transactionService, times(1)).createTransaction(dto, user.getUserId());
  }

  @Test
  void shouldReturnBadRequest_whenCategoryNotFound() {
    UserEntity user = UserEntityFactory.defaultUser();
    CreateTransactionDTO dto = CreateTransactionDTOBuilder.incomeTransaction();
    TransactionResult transactionResult = new TransactionFailure(TransactionsError.CATEGORY_NOT_FOUND);

    when(transactionService.createTransaction(dto, user.getUserId())).thenReturn(transactionResult);

    var response = transactionController.createTransaction(dto, user);

    assertEquals(HttpStatusCode.valueOf(400), response.getStatusCode());
    assertNotNull(response.getBody());
    assertFalse(response.getBody().isSuccess());
    assertEquals("Transaction create error.", response.getBody().getMessage());
    assertEquals("Category", response.getBody().getErrors().get(0).getField());
    assertEquals("Category not found.", response.getBody().getErrors().get(0).getMessage());

    verify(transactionService, times(1)).createTransaction(dto, user.getUserId());
  }

  @Test
  void findAllTransaction_shouldReturnList() {
    // Arrange
    TransactionResponseDTO transaction1 = new TransactionResponseDTO(1, "T1", BigDecimal.TEN, null);
    TransactionResponseDTO transaction2 = new TransactionResponseDTO(2, "T2", BigDecimal.ONE, null);

    when(transactionService.findAllTransaction()).thenReturn(List.of(transaction1, transaction2));

    // Act
    var response = transactionController.findAllTransaction();

    // Assert
    assertEquals(HttpStatusCode.valueOf(200), response.getStatusCode());
    assertNotNull(response.getBody());
    assertThat(response.getBody().getData()).hasSize(2);

    verify(transactionService).findAllTransaction();
  }

  @Test
  void findTransactionById_shouldReturnTransaction() {
    // Arrange
    TransactionResponseDTO transaction = new TransactionResponseDTO(1, "T1", BigDecimal.TEN, null);
    when(transactionService.findById(1)).thenReturn(transaction);

    // Act
    var response = transactionController.findTransactionById(1);

    // Assert
    assertEquals(HttpStatusCode.valueOf(200), response.getStatusCode());
    assertNotNull(response.getBody());
    assertEquals("T1", response.getBody().getData().description());

    verify(transactionService).findById(1);
  }

  @Test
  void updateTransaction_shouldReturnUpdatedTransaction() {
    // Arrange
    TransactionUpdateDTO dto = new TransactionUpdateDTO("Updated", BigDecimal.valueOf(50), 1, 2, TransactionType.EXPENSE);
    TransactionResponseDTO updated = new TransactionResponseDTO(1, "Updated", BigDecimal.valueOf(50), null);

    when(transactionService.update(1, dto)).thenReturn(updated);

    // Act
    var response = transactionController.updateTransaction(1, dto);

    // Assert
    assertEquals(HttpStatusCode.valueOf(200), response.getStatusCode());
    assertEquals("Updated", response.getBody().getData().description());

    verify(transactionService).update(1, dto);
  }

  @Test
  void deleteTransaction_shouldReturnSuccessMessage() {
    // Act
    var response = transactionController.deleteTransaction(1);

    // Assert
    assertEquals(HttpStatusCode.valueOf(200), response.getStatusCode());

    verify(transactionService).delete(1);
  }

  @Test
  void getIncome_shouldReturnCorrectApiResponse() {
    Integer id = 1;
    BigDecimal income = new BigDecimal("1200.00");

    when(transactionService.getIncome(id)).thenReturn(income);

    var response = transactionController.getIncome(id);

    assertNotNull(response);
    assertEquals(HttpStatus.OK, response.getStatusCode());

    ApiResponse<BigDecimal> body = response.getBody();
    assertNotNull(body);
    assertTrue(body.isSuccess());
    assertEquals("Get income", body.getMessage());
    assertEquals(income, body.getData());
    assertTrue(body.getErrors().isEmpty());
  }

  @Test
  void getExpense_shouldReturnCorrectApiResponse() {
    Integer id = 2;
    BigDecimal expense = new BigDecimal("800.00");

    when(transactionService.getExpense(id)).thenReturn(expense);

    var response = transactionController.getExpense(id);

    assertNotNull(response);
    assertEquals(HttpStatus.OK, response.getStatusCode());

    ApiResponse<BigDecimal> body = response.getBody();
    assertNotNull(body);
    assertTrue(body.isSuccess());
    assertEquals("Get expense", body.getMessage());
    assertEquals(expense, body.getData());
    assertTrue(body.getErrors().isEmpty());
  }

  @Test
  void getBill_shouldReturnBill() {
    Integer id = 2;
    BillResponseDTO billResponseDTO = new BillResponseDTO(
        BigDecimal.valueOf(800), BigDecimal.valueOf(800), BigDecimal.valueOf(800));

    when(transactionService.getBill(id)).thenReturn(billResponseDTO);

    var response = transactionController.getBill(id);

    assertNotNull(response);
    assertEquals(HttpStatus.OK, response.getStatusCode());

    ApiResponse<BillResponseDTO> body = response.getBody();
    assertNotNull(body);
    assertTrue(body.isSuccess());
    assertEquals("Get expense", body.getMessage());
    assertEquals(billResponseDTO, body.getData());
    assertTrue(body.getErrors().isEmpty());
  }

  @Test
  void getTransactionPagination() {
    Pageable pageable = PageRequest.of(0, 10);
    var user = new UserResponseDTO(UUID.randomUUID(), "name", "teste");
    AccountResponseDTO res = new AccountResponseDTO(1, user, "name", BigDecimal.TEN, false);
    TransactionResponseDTO transaction1 = new TransactionResponseDTO(1, "T1", BigDecimal.TEN, res);
    TransactionResponseDTO transaction2 = new TransactionResponseDTO(2, "T2", BigDecimal.ONE, res);
    Page<TransactionResponseDTO> page = new PageImpl<>(List.of(transaction1, transaction2), pageable, 2);

    when(transactionService.getPaginatedTransactions(pageable)).thenReturn(page);

    var response = transactionController.getPaginatedTransactions(pageable);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getBody());
    assertThat(response.getBody().getData()).hasSize(2);
    assertEquals("Paginated transactions", response.getBody().getMessage());

    verify(transactionService).getPaginatedTransactions(pageable);
  }
}
