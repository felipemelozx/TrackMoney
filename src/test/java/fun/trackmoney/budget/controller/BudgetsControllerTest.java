package fun.trackmoney.budget.controller;

import fun.trackmoney.budget.dtos.BudgetCreateDTO;
import fun.trackmoney.budget.dtos.BudgetResponseDTO;
import fun.trackmoney.budget.service.BudgetsService;
import fun.trackmoney.utils.response.ApiResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

class BudgetsControllerTest {

  @Mock
  private BudgetsService budgetsService;

  @InjectMocks
  private BudgetsController budgetsController;

  private BudgetCreateDTO createDTO;
  private BudgetResponseDTO responseDTO;
  private Integer budgetId;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    UUID userId = UUID.randomUUID();
    budgetId = 1;
    createDTO = new BudgetCreateDTO(10, userId, 20, BigDecimal.valueOf(1000), 5);
    responseDTO = new BudgetResponseDTO(budgetId, null, null, BigDecimal.valueOf(1000), 5);
  }

  @Test
  void create_shouldReturnCreatedAndBudgetResponse() {
    when(budgetsService.create(createDTO)).thenReturn(responseDTO);

    ResponseEntity<ApiResponse<BudgetResponseDTO>> response = budgetsController.create(createDTO);

    assertEquals(HttpStatus.CREATED, response.getStatusCode());
    assertNotNull(response.getBody());
    assertTrue(response.getBody().isSuccess());
    assertEquals("Budget created", response.getBody().getMessage());
    assertEquals(responseDTO, response.getBody().getData());
    assertNull(response.getBody().getErrors());
    verify(budgetsService, times(1)).create(createDTO);
  }

  @Test
  void findAll_shouldReturnOkAndListOfBudgetResponses() {
    List<BudgetResponseDTO> budgetList = Collections.singletonList(responseDTO);
    when(budgetsService.findAll()).thenReturn(budgetList);

    ResponseEntity<ApiResponse<List<BudgetResponseDTO>>> response = budgetsController.findAll();

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getBody());
    assertTrue(response.getBody().isSuccess());
    assertEquals("Get all Budget", response.getBody().getMessage());
    assertEquals(budgetList, response.getBody().getData());
    assertNull(response.getBody().getErrors());
    verify(budgetsService, times(1)).findAll();
  }

  @Test
  void findById_shouldReturnOkAndBudgetResponse() {
    when(budgetsService.findById(budgetId)).thenReturn(responseDTO);

    ResponseEntity<ApiResponse<BudgetResponseDTO>> response = budgetsController.findById(budgetId);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getBody());
    assertTrue(response.getBody().isSuccess());
    assertEquals("Get Budget by Id", response.getBody().getMessage());
    assertEquals(responseDTO, response.getBody().getData());
    assertNull(response.getBody().getErrors());
    verify(budgetsService, times(1)).findById(budgetId);
  }

  @Test
  void deleteById_shouldReturnOkAndSuccessMessage() {
    ResponseEntity<ApiResponse<String>> response = budgetsController.deleteById(budgetId);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getBody());
    assertTrue(response.getBody().isSuccess());
    assertEquals("Delete Budget by id", response.getBody().getMessage());
    assertEquals("Budget deleted", response.getBody().getData());
    assertNull(response.getBody().getErrors());
    verify(budgetsService, times(1)).findById(budgetId); // Para garantir que o findById foi chamado
  }

  @Test
  void updateById_shouldReturnOkAndUpdatedBudgetResponse() {
    int updateId = 1;
    BudgetCreateDTO updateDTO = new BudgetCreateDTO(11, UUID.randomUUID(), 21, BigDecimal.valueOf(1500), 6);
    BudgetResponseDTO updatedResponseDTO = new BudgetResponseDTO(updateId, null, null, BigDecimal.valueOf(1500), 6);
    when(budgetsService.update(updateDTO, updateId)).thenReturn(updatedResponseDTO);

    ResponseEntity<ApiResponse<BudgetResponseDTO>> response = budgetsController.updateById(updateId, updateDTO);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getBody());
    assertTrue(response.getBody().isSuccess());
    assertEquals("Update Budget", response.getBody().getMessage());
    assertEquals(updatedResponseDTO, response.getBody().getData());
    assertNull(response.getBody().getErrors());
    verify(budgetsService, times(1)).update(updateDTO, updateId);
  }
}