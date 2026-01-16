package fun.trackmoney.budget.controller;

import fun.trackmoney.budget.dtos.BudgetHistoryGenerateDTO;
import fun.trackmoney.budget.dtos.BudgetHistoryGenerationResponse;
import fun.trackmoney.budget.dtos.BudgetHistoryResponseDTO;
import fun.trackmoney.budget.dtos.GenerationResultDTO;
import fun.trackmoney.budget.entity.BudgetHistoryEntity;
import fun.trackmoney.budget.enums.BudgetStatus;
import fun.trackmoney.budget.service.BudgetHistoryService;
import fun.trackmoney.category.entity.CategoryEntity;
import fun.trackmoney.testutils.CategoryEntityFactory;
import fun.trackmoney.testutils.UserEntityFactory;
import fun.trackmoney.user.entity.UserEntity;
import fun.trackmoney.utils.response.ApiResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BudgetHistoryControllerTest {

  @Mock
  private BudgetHistoryService budgetHistoryService;

  @InjectMocks
  private BudgetHistoryController budgetHistoryController;

  private UserEntity mockUser;
  private BudgetHistoryEntity mockHistoryEntity;
  private BudgetHistoryResponseDTO mockResponseDTO;

  @BeforeEach
  void setUp() {
    mockUser = UserEntityFactory.defaultUser();
    CategoryEntity category = CategoryEntityFactory.defaultCategory();

    mockHistoryEntity = new BudgetHistoryEntity()
        .setHistoryId(1)
        .setReferenceMonth((short) 1)
        .setReferenceYear(2025)
        .setPercent((short) 20)
        .setTargetAmount(BigDecimal.valueOf(1000))
        .setSpentAmount(BigDecimal.valueOf(800))
        .setRemainingAmount(BigDecimal.valueOf(200))
        .setTotalIncome(BigDecimal.valueOf(5000))
        .setStatus(BudgetStatus.WITHIN_LIMIT)
        .setCategory(category);

    mockResponseDTO = new BudgetHistoryResponseDTO(
        1,
        1,
        category,
        (short) 1,
        2025,
        (short) 20,
        BigDecimal.valueOf(1000),
        BigDecimal.valueOf(800),
        BigDecimal.valueOf(200),
        BigDecimal.valueOf(5000),
        List.of(),
        BudgetStatus.WITHIN_LIMIT,
        null
    );
  }

  @Test
  void generateHistory_shouldReturnCreated_whenHistoryGenerated() {
    BudgetHistoryGenerateDTO dto = new BudgetHistoryGenerateDTO(1, 2025);
    GenerationResultDTO resultDTO = GenerationResultDTO.success(5);
    when(budgetHistoryService.generateHistoryForMonth(mockUser, dto.month(), dto.year()))
        .thenReturn(resultDTO);

    ResponseEntity<ApiResponse<BudgetHistoryGenerationResponse>> response =
        budgetHistoryController.generateHistory(dto, mockUser);

    assertEquals(HttpStatus.CREATED, response.getStatusCode());
    assertNotNull(response.getBody());
    assertTrue(response.getBody().isSuccess());
    assertEquals("Generated 5 budget history entries", response.getBody().getMessage());
    assertEquals(5, response.getBody().getData().generatedCount());
    verify(budgetHistoryService, times(1)).generateHistoryForMonth(mockUser, dto.month(), dto.year());
  }

  @Test
  void generateHistory_shouldReturnOk_whenHistoryAlreadyExists() {
    BudgetHistoryGenerateDTO dto = new BudgetHistoryGenerateDTO(1, 2025);
    GenerationResultDTO resultDTO = GenerationResultDTO.alreadyExists();
    when(budgetHistoryService.generateHistoryForMonth(mockUser, dto.month(), dto.year()))
        .thenReturn(resultDTO);

    ResponseEntity<ApiResponse<BudgetHistoryGenerationResponse>> response =
        budgetHistoryController.generateHistory(dto, mockUser);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getBody());
    assertTrue(response.getBody().isSuccess());
    assertEquals("History already exists for this month", response.getBody().getMessage());
    assertEquals(0, response.getBody().getData().generatedCount());
    verify(budgetHistoryService, times(1)).generateHistoryForMonth(mockUser, dto.month(), dto.year());
  }

  @Test
  void getHistory_shouldReturnAllHistory_whenNoFilters() {
    List<BudgetHistoryEntity> historyList = Collections.singletonList(mockHistoryEntity);
    List<BudgetHistoryResponseDTO> dtoList = Collections.singletonList(mockResponseDTO);

    when(budgetHistoryService.getAllHistory(mockUser, null)).thenReturn(historyList);
    when(budgetHistoryService.enrichWithTransactions(historyList)).thenReturn(dtoList);

    ResponseEntity<ApiResponse<List<BudgetHistoryResponseDTO>>> response =
        budgetHistoryController.getHistory(mockUser, null, null, null, null, null);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getBody());
    assertTrue(response.getBody().isSuccess());
    assertEquals(dtoList, response.getBody().getData());
    verify(budgetHistoryService, times(1)).getAllHistory(mockUser, null);
    verify(budgetHistoryService, times(1)).enrichWithTransactions(historyList);
  }

  @Test
  void getHistory_shouldReturnFilteredHistory_whenDateRangeProvided() {
    List<BudgetHistoryEntity> historyList = Collections.singletonList(mockHistoryEntity);
    List<BudgetHistoryResponseDTO> dtoList = Collections.singletonList(mockResponseDTO);

    when(budgetHistoryService.getHistoryByDateRange(mockUser, (short) 1, 2025, (short) 3, 2025, null))
        .thenReturn(historyList);
    when(budgetHistoryService.enrichWithTransactions(historyList)).thenReturn(dtoList);

    ResponseEntity<ApiResponse<List<BudgetHistoryResponseDTO>>> response =
        budgetHistoryController.getHistory(mockUser, (short) 1, 2025, (short) 3, 2025, null);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getBody());
    assertTrue(response.getBody().isSuccess());
    assertEquals(dtoList, response.getBody().getData());
    verify(budgetHistoryService, times(1)).getHistoryByDateRange(mockUser, (short) 1, 2025, (short) 3, 2025, null);
    verify(budgetHistoryService, times(1)).enrichWithTransactions(historyList);
  }

  @Test
  void getHistoryByMonth_shouldReturnHistoryForSpecificMonth() {
    List<BudgetHistoryEntity> historyList = Collections.singletonList(mockHistoryEntity);
    List<BudgetHistoryResponseDTO> dtoList = Collections.singletonList(mockResponseDTO);

    when(budgetHistoryService.getHistoryByDateRange(mockUser, (short) 1, 2025, (short) 1, 2025, null))
        .thenReturn(historyList);
    when(budgetHistoryService.enrichWithTransactions(historyList)).thenReturn(dtoList);

    ResponseEntity<ApiResponse<List<BudgetHistoryResponseDTO>>> response =
        budgetHistoryController.getHistoryByMonth((short) 1, 2025, null, mockUser);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getBody());
    assertTrue(response.getBody().isSuccess());
    assertEquals("Retrieved budget history for 1/2025", response.getBody().getMessage());
    assertEquals(dtoList, response.getBody().getData());
    verify(budgetHistoryService, times(1)).getHistoryByDateRange(mockUser, (short) 1, 2025, (short) 1, 2025, null);
    verify(budgetHistoryService, times(1)).enrichWithTransactions(historyList);
  }

  @Test
  void getHistoryByMonth_shouldReturnEmptyList_whenNoHistoryFound() {
    when(budgetHistoryService.getHistoryByDateRange(mockUser, (short) 1, 2025, (short) 1, 2025, null))
        .thenReturn(Collections.emptyList());
    when(budgetHistoryService.enrichWithTransactions(Collections.emptyList()))
        .thenReturn(Collections.emptyList());

    ResponseEntity<ApiResponse<List<BudgetHistoryResponseDTO>>> response =
        budgetHistoryController.getHistoryByMonth((short) 1, 2025, null, mockUser);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getBody());
    assertTrue(response.getBody().isSuccess());
    assertTrue(response.getBody().getData().isEmpty());
    verify(budgetHistoryService, times(1)).getHistoryByDateRange(mockUser, (short) 1, 2025, (short) 1, 2025, null);
  }
}
