package fun.trackmoney.budget.controller;

import fun.trackmoney.budget.dtos.BudgetCreateDTO;
import fun.trackmoney.budget.dtos.BudgetResponseDTO;
import fun.trackmoney.budget.dtos.internal.BudgetFailure;
import fun.trackmoney.budget.dtos.internal.BudgetResult;
import fun.trackmoney.budget.dtos.internal.BudgetSuccess;
import fun.trackmoney.budget.enums.BudgetError;
import fun.trackmoney.budget.service.BudgetsService;
import fun.trackmoney.user.entity.UserEntity;
import fun.trackmoney.utils.response.ApiResponse;
import fun.trackmoney.testutils.BudgetCreateDTOFactory;
import fun.trackmoney.testutils.BudgetResponseDTOFactory;
import fun.trackmoney.testutils.UserEntityFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BudgetsControllerTest {

  @Mock
  private BudgetsService budgetsService;

  @InjectMocks
  private BudgetsController budgetsController;

  private BudgetCreateDTO createDTO;
  private BudgetResponseDTO responseDTO;
  private UserEntity mockUser;
  private final Integer budgetId = 1;

  @BeforeEach
  void setUp() {
    mockUser = UserEntityFactory.defaultUser();
    createDTO = BudgetCreateDTOFactory.defaultDTO();
    responseDTO = BudgetResponseDTOFactory.defaultResponse();
  }


  @Test
  void create_shouldReturnCreatedAndBudgetResponse_onSuccess() {
    BudgetResult successResult = new BudgetSuccess(responseDTO);
    when(budgetsService.create(createDTO, mockUser)).thenReturn(successResult);

    ResponseEntity<ApiResponse<BudgetResponseDTO>> response = budgetsController.create(createDTO, mockUser);

    assertEquals(HttpStatus.CREATED, response.getStatusCode());
    assertNotNull(response.getBody());
    assertTrue(response.getBody().isSuccess());
    assertEquals("Budget created", response.getBody().getMessage());
    assertEquals(responseDTO, response.getBody().getData());
    verify(budgetsService, times(1)).create(createDTO, mockUser);
  }

  @Test
  void create_shouldReturnBadRequest_onCategoryNotFoundFailure() {
    BudgetResult failureResult = new BudgetFailure(BudgetError.CATEGORY_NOT_FOUND, "Category not found");
    when(budgetsService.create(createDTO, mockUser)).thenReturn(failureResult);

    ResponseEntity<ApiResponse<BudgetResponseDTO>> response = budgetsController.create(createDTO, mockUser);

    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    assertTrue(!response.getBody().isSuccess());
    assertTrue(response.getBody().getErrors().stream().anyMatch(e -> "categoryId".equals(e.getField())));
    verify(budgetsService, times(1)).create(createDTO, mockUser);
  }

  @Test
  void findAllByAccountId_shouldReturnOkAndListOfBudgetResponses() {
    List<BudgetResponseDTO> budgetList = Collections.singletonList(responseDTO);
    when(budgetsService.findAllBudgets(mockUser)).thenReturn(budgetList);

    ResponseEntity<ApiResponse<List<BudgetResponseDTO>>> response = budgetsController.findAllByAccountId(mockUser);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertTrue(response.getBody().isSuccess());
    assertEquals(budgetList, response.getBody().getData());
    verify(budgetsService, times(1)).findAllBudgets(mockUser);
  }

  @Test
  void findById_shouldReturnOkAndBudgetResponse_whenFound() {
    when(budgetsService.findById(budgetId, mockUser)).thenReturn(responseDTO);

    ResponseEntity<ApiResponse<BudgetResponseDTO>> response = budgetsController.findById(budgetId, mockUser);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertTrue(response.getBody().isSuccess());
    assertEquals(responseDTO, response.getBody().getData());
    verify(budgetsService, times(1)).findById(budgetId, mockUser);
  }

  @Test
  void findById_shouldReturnNotFound_whenNotFound() {
    when(budgetsService.findById(budgetId, mockUser)).thenReturn(null);

    ResponseEntity<ApiResponse<BudgetResponseDTO>> response = budgetsController.findById(budgetId, mockUser);

    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    assertTrue(!response.getBody().isSuccess());
    assertEquals("Budget not found", response.getBody().getMessage());
    verify(budgetsService, times(1)).findById(budgetId, mockUser);
  }


  @Test
  void deleteById_shouldReturnOkAndSuccessMessage() {
    // deleteById é um método void no serviço
    doNothing().when(budgetsService).deleteById(budgetId, mockUser);

    ResponseEntity<ApiResponse<Void>> response = budgetsController.deleteById(budgetId, mockUser);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertTrue(response.getBody().isSuccess());
    assertEquals("Delete Budget by id", response.getBody().getMessage());
    verify(budgetsService, times(1)).deleteById(budgetId, mockUser);
  }


  @Test
  void updateById_shouldReturnOkAndUpdatedBudgetResponse_onSuccess() {
    Integer updateId = 1;

    BudgetCreateDTO updateDTO = BudgetCreateDTOFactory.secondaryDTO();

    BudgetResponseDTO updatedResponseDTO = BudgetResponseDTOFactory.defaultResponse();
    BudgetResult successResult = new BudgetSuccess(updatedResponseDTO);

    when(budgetsService.update(updateDTO, updateId, mockUser)).thenReturn(successResult);

    ResponseEntity<ApiResponse<BudgetResponseDTO>> response = budgetsController.updateById(updateId, updateDTO, mockUser);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertTrue(response.getBody().isSuccess());
    assertEquals("Budget updated successfully", response.getBody().getMessage());
    assertEquals(updatedResponseDTO, response.getBody().getData());
    verify(budgetsService, times(1)).update(updateDTO, updateId, mockUser);
  }

  @Test
  void updateById_shouldReturnNotFound_onBudgetNotFoundFailure() {
    Integer updateId = 99;

    BudgetCreateDTO updateDTO = BudgetCreateDTOFactory.defaultDTO();

    BudgetResult failureResult = new BudgetFailure(BudgetError.BUDGET_NOT_FOUND, "Budget not found");

    when(budgetsService.update(updateDTO, updateId, mockUser)).thenReturn(failureResult);

    ResponseEntity<ApiResponse<BudgetResponseDTO>> response = budgetsController.updateById(updateId, updateDTO, mockUser);

    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    assertTrue(!response.getBody().isSuccess());
    assertTrue(response.getBody().getErrors().stream().anyMatch(e -> "id".equals(e.getField())));
    verify(budgetsService, times(1)).update(updateDTO, updateId, mockUser);
  }

  @Test
  void updateById_shouldReturnNotFound_onCategoryNotFoundFailure() {
    Integer updateId = 1;
    BudgetCreateDTO updateDTO = BudgetCreateDTOFactory.defaultDTO();
    BudgetResult failureResult = new BudgetFailure(BudgetError.CATEGORY_NOT_FOUND, "Category not found");

    when(budgetsService.update(updateDTO, updateId, mockUser)).thenReturn(failureResult);

    ResponseEntity<ApiResponse<BudgetResponseDTO>> response = budgetsController.updateById(updateId, updateDTO, mockUser);

    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    assertTrue(!response.getBody().isSuccess());

    assertTrue(response.getBody().getErrors().stream().anyMatch(e -> "categoryId".equals(e.getField())));
    verify(budgetsService, times(1)).update(updateDTO, updateId, mockUser);
  }

  @Test
  void updateById_shouldReturnBadRequest_onPercentLimitExceededFailure() {
    Integer updateId = 1;
    BudgetCreateDTO updateDTO = BudgetCreateDTOFactory.defaultDTO();
    BudgetResult failureResult = new BudgetFailure(BudgetError.PERCENT_LIMIT_EXCEEDED, "Percent limit exceeded");

    when(budgetsService.update(updateDTO, updateId, mockUser)).thenReturn(failureResult);

    ResponseEntity<ApiResponse<BudgetResponseDTO>> response = budgetsController.updateById(updateId, updateDTO, mockUser);

    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    assertTrue(!response.getBody().isSuccess());

    assertTrue(response.getBody().getErrors().stream().anyMatch(e -> "percent".equals(e.getField())));
    verify(budgetsService, times(1)).update(updateDTO, updateId, mockUser);
  }

  @Test
  void updateById_shouldReturnBadRequest_onDefaultFailureCase() {
    Integer updateId = 1;
    BudgetCreateDTO updateDTO = BudgetCreateDTOFactory.defaultDTO();
    BudgetResult failureResult = new BudgetFailure(BudgetError.EXIST_BUDGET, "Budget already exists");

    when(budgetsService.update(updateDTO, updateId, mockUser)).thenReturn(failureResult);

    ResponseEntity<ApiResponse<BudgetResponseDTO>> response = budgetsController.updateById(updateId, updateDTO, mockUser);

    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    assertTrue(!response.getBody().isSuccess());

    assertTrue(response.getBody().getErrors().stream().anyMatch(e -> "category".equals(e.getField())));
    verify(budgetsService, times(1)).update(updateDTO, updateId, mockUser);
  }

  @Test
  void create_shouldReturnBadRequest_onPercentLimitExceededFailure() {
    BudgetCreateDTO highPercentDTO = BudgetCreateDTOFactory.defaultDTO();

    BudgetResult failureResult = new BudgetFailure(BudgetError.PERCENT_LIMIT_EXCEEDED, "Percent limit exceeded");
    when(budgetsService.create(highPercentDTO, mockUser)).thenReturn(failureResult);

    ResponseEntity<ApiResponse<BudgetResponseDTO>> response = budgetsController.create(highPercentDTO, mockUser);

    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    assertTrue(!response.getBody().isSuccess());

    assertTrue(response.getBody().getErrors().stream().anyMatch(e -> "percent".equals(e.getField())));
    verify(budgetsService, times(1)).create(highPercentDTO, mockUser);
  }

  @Test
  void create_shouldReturnBadRequest_onDefaultFailureCase() {
    BudgetResult failureResult = new BudgetFailure(BudgetError.EXIST_BUDGET, "Budget already exists for this category");
    when(budgetsService.create(createDTO, mockUser)).thenReturn(failureResult);

    ResponseEntity<ApiResponse<BudgetResponseDTO>> response = budgetsController.create(createDTO, mockUser);

    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    assertTrue(!response.getBody().isSuccess());

    assertTrue(response.getBody().getErrors().stream().anyMatch(e -> "category".equals(e.getField())));
    verify(budgetsService, times(1)).create(createDTO, mockUser);
  }
}