package fun.trackmoney.service;
import fun.trackmoney.service.RecurringService;

import fun.trackmoney.service.CategoryService;
import fun.trackmoney.entity.CategoryEntity;
import fun.trackmoney.enums.Frequency;
import fun.trackmoney.enums.TransactionType;
import fun.trackmoney.dto.recurring.CreateRecurringRequest;
import fun.trackmoney.dto.recurring.RecurringResponse;
import fun.trackmoney.entity.RecurringEntity;
import fun.trackmoney.mapper.RecurringMapper;
import fun.trackmoney.repository.RecurringRepository;
import fun.trackmoney.testutils.CategoryEntityFactory;
import fun.trackmoney.testutils.CreateRecurringRequestFactory;
import fun.trackmoney.testutils.RecurringEntityFactory;
import fun.trackmoney.testutils.RecurringResponseFactory;
import fun.trackmoney.testutils.TransactionResponseDTOFactory;
import fun.trackmoney.testutils.UserEntityFactory;
import fun.trackmoney.dto.transaction.CreateTransactionDTO;
import fun.trackmoney.dto.transaction.TransactionResponseDTO;
import fun.trackmoney.dto.transaction.internal.TransactionFailure;
import fun.trackmoney.dto.transaction.internal.TransactionResult;
import fun.trackmoney.dto.transaction.internal.TransactionSuccess;
import fun.trackmoney.enums.TransactionsError;
import fun.trackmoney.service.TransactionService;
import fun.trackmoney.entity.UserEntity;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class RecurringServiceTest {

  @Mock
  private RecurringRepository recurringRepository;

  @Mock
  private RecurringMapper recurringMapper;

  @Mock
  private CategoryService categoryService;

  @Mock
  private TransactionService transactionService;

  @InjectMocks
  private RecurringService recurringService;

  private final Long RECURRING_ID = 1L;

  @Test
  void create_shouldCreateAnewRecurring() {
    CreateRecurringRequest request = CreateRecurringRequestFactory.defaultRequest();
    RecurringEntity recurring = RecurringEntityFactory.defaultEntity();
    RecurringResponse response = RecurringResponseFactory.defaultResponse();
    UserEntity currentUser = UserEntityFactory.defaultUser();
    CategoryEntity category = CategoryEntityFactory.defaultCategory();

    LocalDateTime expectedNextDate = LocalDateTime.now().plusMonths(1);

    when(recurringMapper.toEntity(request)).thenReturn(recurring);
    when(categoryService.findEntityById(request.categoryId())).thenReturn(category);

    // Mock the repository to set the expected next date when saving
    when(recurringRepository.save(any(RecurringEntity.class))).thenAnswer(invocation -> {
      RecurringEntity entity = invocation.getArgument(0);
      entity.setNextDate(expectedNextDate);
      return entity;
    });

    when(recurringMapper.toResponse(recurring)).thenReturn(response);

    RecurringResponse result = recurringService.create(request, currentUser);

    assertNotNull(result);
    assertEquals(response, result);
    assertEquals(currentUser.getAccount(), recurring.getAccount());

    verify(categoryService, times(1)).findEntityById(request.categoryId());
    verify(recurringRepository, times(1)).save(any(RecurringEntity.class));
  }

  @Test
  void create_shouldFailureCreateAnewRecurring() {
    CreateRecurringRequest request = CreateRecurringRequestFactory.defaultRequest();
    RecurringEntity recurring = RecurringEntityFactory.defaultEntity();
    UserEntity currentUser = UserEntityFactory.defaultUser();


    when(recurringMapper.toEntity(request)).thenReturn(recurring);
    when(categoryService.findEntityById(request.categoryId())).thenReturn(null);

    RecurringResponse result = recurringService.create(request, currentUser);

    assertNull(result);
    verify(categoryService, times(1)).findEntityById(request.categoryId());
    verify(recurringRepository, times(0)).save(recurring);
  }

  @Test
  void calculateNextRun_shouldReturnNextDay() {
    LocalDateTime now = LocalDateTime.now();
    LocalDateTime expected = now.plusDays(1);

    LocalDateTime result = ReflectionTestUtils.invokeMethod(recurringService, "calculateNextRun", Frequency.DAILY, now);

    assertEquals(expected.getYear(), result.getYear());
    assertEquals(expected.getMonth(), result.getMonth());
    assertEquals(expected.getDayOfMonth(), result.getDayOfMonth());
    assertEquals(expected.getHour(), result.getHour());
    assertEquals(expected.getMinute(), result.getMinute());
    assertEquals(expected.getSecond(), result.getSecond());
  }

  @Test
  void calculateNextRun_shouldReturnNextMondayWhenWeekly() {
    LocalDateTime now = LocalDateTime.now();

    LocalDateTime expected = now.with(TemporalAdjusters.next(DayOfWeek.MONDAY));

    LocalDateTime result = ReflectionTestUtils.invokeMethod(recurringService, "calculateNextRun", Frequency.WEEKLY, now);

    assertEquals(expected.getYear(), result.getYear());
    assertEquals(expected.getMonth(), result.getMonth());
    assertEquals(expected.getDayOfMonth(), result.getDayOfMonth());
    assertEquals(expected.getHour(), result.getHour());
    assertEquals(expected.getMinute(), result.getMinute());
    assertEquals(expected.getSecond(), result.getSecond());
  }

  @Test
  void calculateNextRun_shouldReturnNextMonth() {
    LocalDateTime now = LocalDateTime.now();
    LocalDateTime expected = now.plusMonths(1);

    LocalDateTime result = ReflectionTestUtils.invokeMethod(recurringService, "calculateNextRun", Frequency.MONTHLY, now);

    assertEquals(expected.getYear(), result.getYear());
    assertEquals(expected.getMonth(), result.getMonth());
    assertEquals(expected.getDayOfMonth(), result.getDayOfMonth());
    assertEquals(expected.getHour(), result.getHour());
    assertEquals(expected.getMinute(), result.getMinute());
    assertEquals(expected.getSecond(), result.getSecond());
  }

  @Test
  void calculateNextRun_shouldReturnNextYear() {
    LocalDateTime now = LocalDateTime.now();
    LocalDateTime expected = now.plusYears(1);

    LocalDateTime result = ReflectionTestUtils.invokeMethod(recurringService, "calculateNextRun", Frequency.YEARLY, now);

    assertEquals(expected.getYear(), result.getYear());
    assertEquals(expected.getMonth(), result.getMonth());
    assertEquals(expected.getDayOfMonth(), result.getDayOfMonth());
    assertEquals(expected.getHour(), result.getHour());
    assertEquals(expected.getMinute(), result.getMinute());
    assertEquals(expected.getSecond(), result.getSecond());
  }

  @Test
  void processRecurring_shouldProcessSuccessfully() {
    RecurringService spyService = Mockito.spy(recurringService);
    RecurringEntity recurring = RecurringEntityFactory.defaultEntity();
    LocalDateTime expectedNextDate = LocalDateTime.now().plusMonths(1);
    TransactionResponseDTO responseDTO = TransactionResponseDTOFactory.defaultTransactionResponse();
    TransactionResult successResult = new TransactionSuccess(responseDTO);

    when(transactionService.createTransaction(
        any(CreateTransactionDTO.class),
        eq(recurring.getAccount().getUser())
    )).thenReturn(successResult);

    // Mock the repository to set the expected next date when saving
    when(recurringRepository.save(any(RecurringEntity.class))).thenAnswer(invocation -> {
      RecurringEntity entity = invocation.getArgument(0);
      entity.setNextDate(expectedNextDate);
      return entity;
    });

    ReflectionTestUtils.invokeMethod(spyService, "processRecurring", recurring);

    ArgumentCaptor<CreateTransactionDTO> dtoCaptor = ArgumentCaptor.forClass(CreateTransactionDTO.class);

    verify(transactionService).createTransaction(
        dtoCaptor.capture(),
        eq(recurring.getAccount().getUser())
    );

    CreateTransactionDTO capturedDto = dtoCaptor.getValue();

    assertEquals(recurring.getAmount(), capturedDto.amount());
    assertEquals(recurring.getDescription(), capturedDto.description());
    assertEquals(recurring.getCategory().getCategoryId(), capturedDto.categoryId());
    // ---------------------------------------------

    verify(recurringRepository).save(any(RecurringEntity.class));
  }

  @Test
  void processRecurring_shouldThrowWhenTransactionFails() {
    RecurringService spyService = Mockito.spy(recurringService);
    RecurringEntity recurring = RecurringEntityFactory.defaultEntity();
    TransactionFailure failure = new TransactionFailure(TransactionsError.ACCOUNT_NOT_FOUND);

    when(transactionService.createTransaction(
        Mockito.<CreateTransactionDTO>any(),
        eq(recurring.getAccount().getUser())
    )).thenReturn(failure);

    RuntimeException ex = assertThrows(
        RuntimeException.class,
        () -> ReflectionTestUtils.invokeMethod(spyService, "processRecurring", recurring)
    );

    assertTrue(ex.getMessage().contains("Failed to create transaction"));
    verify(recurringRepository, never()).save(any(RecurringEntity.class));
  }

  @Test
  void recurringTransaction_shouldProcessTheRecurring() {
    RecurringService spyService = Mockito.spy(recurringService);

    RecurringEntity recurring1 = RecurringEntityFactory.defaultEntity();
    RecurringEntity recurring2 = RecurringEntityFactory.defaultEntity();
    List<RecurringEntity> recurrences = List.of(recurring1, recurring2);

    when(recurringRepository.findByNextDateBefore(any(LocalDateTime.class)))
        .thenReturn(recurrences);

    spyService.recurringTransactions();

    verify(recurringRepository, times(1)).findByNextDateBefore(any(LocalDateTime.class));
  }

  @Test
  void update_shouldUpdateRecurringSuccessfully() {
    CreateRecurringRequest request = CreateRecurringRequestFactory.defaultRequest();
    UserEntity currentUser = UserEntityFactory.defaultUser();

    RecurringEntity existingRecurring = RecurringEntityFactory.defaultEntity();
    CategoryEntity newCategory = CategoryEntityFactory.defaultCategory();
    RecurringResponse expectedResponse = RecurringResponseFactory.defaultResponse();

    when(categoryService.findEntityById(request.categoryId())).thenReturn(newCategory);
    when(recurringRepository.findByIdAndAccount(RECURRING_ID, currentUser.getAccount().getAccountId()))
        .thenReturn(Optional.of(existingRecurring));

    when(recurringRepository.save(any(RecurringEntity.class))).thenAnswer(invocation -> {
      RecurringEntity entity = invocation.getArgument(0);
      entity.setNextDate(LocalDateTime.now().plusMonths(2));
      return entity;
    });
    when(recurringMapper.toResponse(existingRecurring)).thenReturn(expectedResponse);

    RecurringResponse result = recurringService.update(RECURRING_ID, request, currentUser);

    assertNotNull(result);
    assertEquals(expectedResponse, result);

    assertEquals(newCategory, existingRecurring.getCategory());
    assertEquals(request.transactionName(), existingRecurring.getTransactionName());
    assertEquals(request.description(), existingRecurring.getDescription());
    assertEquals(request.transactionType(), existingRecurring.getTransactionType());
    assertEquals(request.amount(), existingRecurring.getAmount());
    assertEquals(request.frequency(), existingRecurring.getFrequency());

    verify(categoryService, times(1)).findEntityById(request.categoryId());
    verify(recurringRepository, times(1)).findByIdAndAccount(RECURRING_ID, currentUser.getAccount().getAccountId());
    verify(recurringRepository, times(1)).save(any(RecurringEntity.class));
  }

  @Test
  void update_shouldReturnNullWhenCategoryNotFound() {
    CreateRecurringRequest request = CreateRecurringRequestFactory.defaultRequest();
    UserEntity currentUser = UserEntityFactory.defaultUser();

    when(categoryService.findEntityById(request.categoryId())).thenReturn(null);

    RecurringResponse result = recurringService.update(RECURRING_ID, request, currentUser);

    assertNull(result);

    verify(categoryService, times(1)).findEntityById(request.categoryId());
    verify(recurringRepository, never()).findByIdAndAccount(any(), any());
    verify(recurringRepository, never()).save(any());
  }

  @Test
  void update_shouldReturnNullWhenRecurringNotFoundForUser() {
    CreateRecurringRequest request = CreateRecurringRequestFactory.defaultRequest();
    UserEntity currentUser = UserEntityFactory.defaultUser();
    CategoryEntity category = CategoryEntityFactory.defaultCategory();

    when(categoryService.findEntityById(request.categoryId())).thenReturn(category);
    when(recurringRepository.findByIdAndAccount(RECURRING_ID, currentUser.getAccount().getAccountId()))
        .thenReturn(Optional.empty());

    RecurringResponse result = recurringService.update(RECURRING_ID, request, currentUser);

    assertNull(result);

    verify(categoryService, times(1)).findEntityById(request.categoryId());
    verify(recurringRepository, times(1)).findByIdAndAccount(RECURRING_ID,currentUser.getAccount().getAccountId());
    verify(recurringRepository, never()).save(any());
  }

  @Test
  void findAll_shouldReturnListOfRecurringResponses() {
    UserEntity currentUser = UserEntityFactory.defaultUser();
    Integer accountId = currentUser.getAccount().getAccountId();

    List<RecurringEntity> entities = List.of(RecurringEntityFactory.defaultEntity(), RecurringEntityFactory.defaultEntity());
    List<RecurringResponse> expectedResponses = List.of(RecurringResponseFactory.defaultResponse(), RecurringResponseFactory.defaultResponse());

    when(recurringRepository.findAllByAccountId(accountId)).thenReturn(entities);
    when(recurringMapper.toResponse(entities)).thenReturn(expectedResponses);

    List<RecurringResponse> result = recurringService.findAll(currentUser);

    assertNotNull(result);
    assertEquals(2, result.size());
    assertEquals(expectedResponses, result);

    verify(recurringRepository, times(1)).findAllByAccountId(accountId);
    verify(recurringMapper, times(1)).toResponse(entities);
  }

  @Test
  void findAll_shouldReturnEmptyList_whenNoRecurrencesFound() {
    UserEntity currentUser = UserEntityFactory.defaultUser();
    Integer accountId = currentUser.getAccount().getAccountId();

    List<RecurringEntity> emptyEntities = Collections.emptyList();
    List<RecurringResponse> emptyResponses = Collections.emptyList();

    when(recurringRepository.findAllByAccountId(accountId)).thenReturn(emptyEntities);
    when(recurringMapper.toResponse(emptyEntities)).thenReturn(emptyResponses);

    List<RecurringResponse> result = recurringService.findAll(currentUser);

    assertNotNull(result);
    assertTrue(result.isEmpty());

    verify(recurringRepository, times(1)).findAllByAccountId(accountId);
    verify(recurringMapper, times(1)).toResponse(emptyEntities);
  }

  @Test
  void delete_shouldCallRepositoryDelete_whenSuccessful() {
    UserEntity currentUser = UserEntityFactory.defaultUser();
    Integer accountId = currentUser.getAccount().getAccountId();

    doNothing().when(recurringRepository).deleteByIdAndAccountId(RECURRING_ID, accountId);

    recurringService.delete(RECURRING_ID, currentUser);

    verify(recurringRepository, times(1)).deleteByIdAndAccountId(RECURRING_ID, accountId);
  }

  @Test
  void delete_shouldHandleException_whenRecurringNotFoundOrNotOwned() {
    UserEntity currentUser = UserEntityFactory.defaultUser();
    Integer accountId = currentUser.getAccount().getAccountId();

    doNothing().when(recurringRepository).deleteByIdAndAccountId(RECURRING_ID, accountId);

    recurringService.delete(RECURRING_ID, currentUser);

    verify(recurringRepository, times(1)).deleteByIdAndAccountId(RECURRING_ID, accountId);
  }

  @Test
  void getBill_shouldReturnBillResponseDTO() {
    var currentUser = UserEntityFactory.defaultUser();
    var accountId = currentUser.getAccount().getAccountId();

    var recurring1 = new RecurringEntity();
    var recurring2 = new RecurringEntity();
    var recurring3 = new RecurringEntity();

    recurring1.setAmount(BigDecimal.valueOf(100));
    recurring2.setAmount(BigDecimal.valueOf(100));
    recurring3.setAmount(BigDecimal.valueOf(100));

    recurring1.setTransactionType(TransactionType.EXPENSE);
    recurring2.setTransactionType(TransactionType.EXPENSE);
    recurring3.setTransactionType(TransactionType.EXPENSE);

    LocalDateTime baseDate = LocalDate.now().atStartOfDay();

    recurring1.setNextDate(baseDate.minusDays(8));

    recurring2.setNextDate(baseDate.plusDays(2));

    recurring3.setNextDate(baseDate.plusDays(5));

    when(recurringRepository.findAllByAccountId(accountId))
        .thenReturn(List.of(recurring1, recurring2, recurring3));

    var result = recurringService.getBill(currentUser);

    assertNotNull(result);
    assertEquals(BigDecimal.valueOf(200), result.totalUpcoming());
    assertEquals(BigDecimal.valueOf(100), result.bill());
    assertEquals(BigDecimal.valueOf(200), result.bueSoon());
  }
}