package fun.trackmoney.transaction.controller;

import fun.trackmoney.enums.TransactionType;
import fun.trackmoney.transaction.dto.CreateTransactionDTO;
import fun.trackmoney.transaction.dto.TransactionResponseDTO;
import fun.trackmoney.transaction.dto.TransactionUpdateDTO;
import fun.trackmoney.transaction.service.TransactionService;
import fun.trackmoney.utils.response.ApiResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.*;

class TransactionControllerTest {

  private TransactionService transactionService;
  private TransactionController transactionController;

  @BeforeEach
  void setUp() {
    transactionService = mock(TransactionService.class);
    transactionController = new TransactionController(transactionService);
  }

  @Test
  void createTransaction_shouldReturnCreatedResponse() {
    // Arrange
    CreateTransactionDTO dto = new CreateTransactionDTO(
        1, 2, TransactionType.INCOME, BigDecimal.valueOf(100), "Test",
        new Timestamp(System.currentTimeMillis())
    );
    TransactionResponseDTO responseDTO = new TransactionResponseDTO(1, "Test", BigDecimal.valueOf(100), null);

    when(transactionService.createTransaction(dto)).thenReturn(responseDTO);

    // Act
    var response = transactionController.createTransaction(dto);

    // Assert
    assertEquals(HttpStatusCode.valueOf(201), response.getStatusCode());
    assertNotNull(response.getBody());
    assertTrue(response.getBody().getSuccess());
    assertEquals("Transfere created", response.getBody().getMessage());
    assertEquals("Test", response.getBody().getData().description());

    verify(transactionService).createTransaction(dto);
  }
  /*
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
*/
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
    assertEquals("Transaction deleted.", response.getBody().getData());

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
    assertTrue(body.getSuccess());
    assertEquals("Get income", body.getMessage());
    assertEquals(income, body.getData());
    assertNull(body.getErrors());
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
    assertTrue(body.getSuccess());
    assertEquals("Get expense", body.getMessage());
    assertEquals(expense, body.getData());
    assertNull(body.getErrors());
  }
}
