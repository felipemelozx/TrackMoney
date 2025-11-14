package fun.trackmoney.transaction.controller;

import fun.trackmoney.enums.TransactionType;
import fun.trackmoney.testutils.CreateTransactionDTOBuilder;
import fun.trackmoney.testutils.TransactionResponseDTOFactory;
import fun.trackmoney.testutils.UserEntityFactory;
import fun.trackmoney.transaction.dto.BillResponseDTO;
import fun.trackmoney.transaction.dto.CreateTransactionDTO;
import fun.trackmoney.transaction.dto.TransactionResponseDTO;
import fun.trackmoney.transaction.dto.TransactionUpdateDTO;
import fun.trackmoney.transaction.enums.DateFilterEnum;
import fun.trackmoney.transaction.enums.TransactionsError;
import fun.trackmoney.transaction.dto.internal.TransactionFailure;
import fun.trackmoney.transaction.dto.internal.TransactionResult;
import fun.trackmoney.transaction.dto.internal.TransactionSuccess;
import fun.trackmoney.transaction.service.TransactionService;
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
import java.time.LocalDate;
import java.util.List;

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

    when(transactionService.createTransaction(dto, user)).thenReturn(transactionResult);

    var response = transactionController.createTransaction(dto, user);

    assertEquals(HttpStatusCode.valueOf(201), response.getStatusCode());
    assertNotNull(response.getBody());
    assertTrue(response.getBody().isSuccess());
    assertEquals("Transfer created", response.getBody().getMessage());
    assertEquals("Salário mensal", response.getBody().getData().description());

    verify(transactionService, times(1)).createTransaction(dto, user);
  }

  @Test
  void shouldReturnBadRequest_whenAccountNotFound() {
    UserEntity user = UserEntityFactory.defaultUser();
    CreateTransactionDTO dto = CreateTransactionDTOBuilder.incomeTransaction();
    TransactionResult transactionResult = new TransactionFailure(TransactionsError.ACCOUNT_NOT_FOUND);

    when(transactionService.createTransaction(dto, user)).thenReturn(transactionResult);

    var response = transactionController.createTransaction(dto, user);

    assertEquals(HttpStatusCode.valueOf(400), response.getStatusCode());
    assertNotNull(response.getBody());
    assertFalse(response.getBody().isSuccess());
    assertEquals("Transaction create error.", response.getBody().getMessage());
    assertEquals("Account", response.getBody().getErrors().get(0).getField());
    assertEquals("Account not found to update balance", response.getBody().getErrors().get(0).getMessage());

    verify(transactionService, times(1)).createTransaction(dto, user);
  }

  @Test
  void shouldReturnBadRequest_whenCategoryNotFound() {
    UserEntity user = UserEntityFactory.defaultUser();
    CreateTransactionDTO dto = CreateTransactionDTOBuilder.incomeTransaction();
    TransactionResult transactionResult = new TransactionFailure(TransactionsError.CATEGORY_NOT_FOUND);

    when(transactionService.createTransaction(dto, user)).thenReturn(transactionResult);

    var response = transactionController.createTransaction(dto, user);

    assertEquals(HttpStatusCode.valueOf(400), response.getStatusCode());
    assertNotNull(response.getBody());
    assertFalse(response.getBody().isSuccess());
    assertEquals("Transaction create error.", response.getBody().getMessage());
    assertEquals("Category", response.getBody().getErrors().get(0).getField());
    assertEquals("Category not found.", response.getBody().getErrors().get(0).getMessage());

    verify(transactionService, times(1)).createTransaction(dto, user);
  }

  @Test
  void findAllTransaction_shouldReturnList() {
    UserEntity currentUser = UserEntityFactory.defaultUser();
    TransactionResponseDTO transaction1 = TransactionResponseDTOFactory.defaultTransactionResponse();
    TransactionResponseDTO transaction2 = TransactionResponseDTOFactory.defaultTransactionResponse();

    when(transactionService.findAllTransaction(currentUser)).thenReturn(List.of(transaction1, transaction2));

    var response = transactionController.findAllTransaction(currentUser);

    assertEquals(HttpStatusCode.valueOf(200), response.getStatusCode());
    assertNotNull(response.getBody());
    assertThat(response.getBody().getData()).hasSize(2);

    verify(transactionService).findAllTransaction(currentUser);
  }

  @Test
  void findTransactionById_shouldReturnTransaction() {
    TransactionResponseDTO transaction = TransactionResponseDTOFactory.defaultTransactionResponse();
    UserEntity currentUser = UserEntityFactory.defaultUser();
    when(transactionService.findById(1, currentUser)).thenReturn(transaction);

    var response = transactionController.findTransactionById(1, currentUser);

    assertEquals(HttpStatusCode.valueOf(200), response.getStatusCode());
    assertNotNull(response.getBody());
    assertEquals("buy bread", response.getBody().getData().description());

    verify(transactionService, times(1)).findById(1, currentUser);
  }

  @Test
  void updateTransaction_shouldReturnUpdatedTransaction() {
    UserEntity currentUser = UserEntityFactory.defaultUser();
    TransactionUpdateDTO dto = new TransactionUpdateDTO("Updated", BigDecimal.valueOf(50), 1, 2, TransactionType.EXPENSE);
    TransactionResponseDTO updated = TransactionResponseDTOFactory.defaultTransactionResponse();

    when(transactionService.update(1, dto, currentUser)).thenReturn(updated);

    var response = transactionController.updateTransaction(1, dto, currentUser);

    assertEquals(HttpStatusCode.valueOf(200), response.getStatusCode());
    assertEquals("buy bread", response.getBody().getData().description());

    verify(transactionService).update(1, dto, currentUser);
  }

  @Test
  void deleteTransaction_shouldReturnSuccessMessage() {
    UserEntity currentUser = UserEntityFactory.defaultUser();
    var response = transactionController.deleteTransaction(1, currentUser);
    assertEquals(HttpStatusCode.valueOf(200), response.getStatusCode());
    verify(transactionService).delete(1, currentUser);
  }

  @Test
  void getIncome_shouldReturnCorrectApiResponse() {
    UserEntity mockUser = UserEntityFactory.defaultUser();
    BigDecimal income = new BigDecimal("1200.00");

    when(transactionService.getIncome(mockUser.getUserId())).thenReturn(income);

    var response = transactionController.getIncome(mockUser);

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
    UserEntity user = UserEntityFactory.defaultUser();
    BigDecimal expense = new BigDecimal("800.00");

    when(transactionService.getExpense(user)).thenReturn(expense);

    var response = transactionController.getExpense(user);

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
    BillResponseDTO billResponseDTO = new BillResponseDTO(
        BigDecimal.valueOf(800), BigDecimal.valueOf(800), BigDecimal.valueOf(800));
    UserEntity currentUser = UserEntityFactory.defaultUser();
    when(transactionService.getBill(currentUser)).thenReturn(billResponseDTO);

    var response = transactionController.getBill(currentUser);

    assertNotNull(response);
    assertEquals(HttpStatus.OK, response.getStatusCode());

    ApiResponse<BillResponseDTO> body = response.getBody();
    assertNotNull(body);
    assertTrue(body.isSuccess());
    assertEquals("Get bill", body.getMessage());
    assertEquals(billResponseDTO, body.getData());
    assertTrue(body.getErrors().isEmpty());
  }

  @Test
  void getTransactionPagination() {
    Pageable pageable = PageRequest.of(0, 10);
    UserEntity user = UserEntityFactory.defaultUser();

    TransactionResponseDTO transaction1 = TransactionResponseDTOFactory.defaultTransactionResponse();
    TransactionResponseDTO transaction2 = TransactionResponseDTOFactory.defaultTransactionResponse();
    Page<TransactionResponseDTO> page = new PageImpl<>(List.of(transaction1, transaction2), pageable, 2);

    when(transactionService.getPaginatedTransactions(pageable, user, "some name", 1l, DateFilterEnum.LAST_MONTH)).thenReturn(page);

    var response = transactionController.getPaginatedTransactions(pageable, "some name", 1l,DateFilterEnum.LAST_MONTH, user);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getBody());
    assertThat(response.getBody().getData()).hasSize(2);
    assertEquals("Paginated transactions", response.getBody().getMessage());
    verify(transactionService, times(1)).getPaginatedTransactions(pageable, user, "some name", 1l, DateFilterEnum.LAST_MONTH);
  }
}
