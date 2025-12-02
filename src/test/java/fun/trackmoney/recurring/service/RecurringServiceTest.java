package fun.trackmoney.recurring.service;

import fun.trackmoney.category.entity.CategoryEntity;
import fun.trackmoney.category.service.CategoryService;
import fun.trackmoney.enums.Frequency;
import fun.trackmoney.recurring.dtos.CreateRecurringRequest;
import fun.trackmoney.recurring.dtos.RecurringResponse;
import fun.trackmoney.recurring.entity.RecurringEntity;
import fun.trackmoney.recurring.mapper.RecurringMapper;
import fun.trackmoney.recurring.repository.RecurringRepository;
import fun.trackmoney.testutils.CategoryEntityFactory;
import fun.trackmoney.testutils.CreateRecurringRequestFactory;
import fun.trackmoney.testutils.RecurringEntityFactory;
import fun.trackmoney.testutils.RecurringResponseFactory;
import fun.trackmoney.testutils.TransactionResponseDTOFactory;
import fun.trackmoney.testutils.UserEntityFactory;
import fun.trackmoney.transaction.dto.CreateTransactionDTO;
import fun.trackmoney.transaction.dto.TransactionResponseDTO;
import fun.trackmoney.transaction.dto.internal.TransactionFailure;
import fun.trackmoney.transaction.dto.internal.TransactionResult;
import fun.trackmoney.transaction.dto.internal.TransactionSuccess;
import fun.trackmoney.transaction.enums.TransactionsError;
import fun.trackmoney.transaction.service.TransactionService;
import fun.trackmoney.user.entity.UserEntity;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.DayOfWeek;
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
import static org.mockito.Mockito.doReturn;
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

  @Test
  void create_shouldCreateAnewRecurring() {
    RecurringService recurringServiceSpy = Mockito.spy(recurringService);

    CreateRecurringRequest request = CreateRecurringRequestFactory.defaultRequest();
    RecurringEntity recurring = RecurringEntityFactory.defaultEntity();
    RecurringResponse response = RecurringResponseFactory.defaultResponse();
    UserEntity currentUser = UserEntityFactory.defaultUser();
    CategoryEntity category = CategoryEntityFactory.defaultCategory();

    LocalDateTime expectedNextDate = LocalDateTime.now().plusMonths(1);

    when(recurringMapper.toEntity(request)).thenReturn(recurring);
    when(categoryService.findById(request.categoryId())).thenReturn(category);
    when(recurringRepository.save(recurring)).thenReturn(recurring);
    when(recurringMapper.toResponse(recurring)).thenReturn(response);

    doReturn(expectedNextDate)
        .when(recurringServiceSpy)
        .calculateNextRun(request.frequency(), request.recurrenceDay());

    RecurringResponse result = recurringServiceSpy.create(request, currentUser);

    assertNotNull(result);
    assertEquals(response, result);
    assertEquals(currentUser.getAccount(), recurring.getAccount());
    assertEquals(expectedNextDate, recurring.getNextDate());

    verify(categoryService, times(1)).findById(request.categoryId());
    verify(recurringRepository, times(1)).save(recurring);
  }

  @Test
  void create_shouldFailureCreateAnewRecurring() {
    RecurringService recurringServiceSpy = Mockito.spy(recurringService);
    CreateRecurringRequest request = CreateRecurringRequestFactory.defaultRequest();
    RecurringEntity recurring = RecurringEntityFactory.defaultEntity();
    UserEntity currentUser = UserEntityFactory.defaultUser();


    when(recurringMapper.toEntity(request)).thenReturn(recurring);
    when(categoryService.findById(request.categoryId())).thenReturn(null);

    RecurringResponse result = recurringServiceSpy.create(request, currentUser);

    assertNull(result);
    verify(categoryService, times(1)).findById(request.categoryId());
    verify(recurringRepository, times(0)).save(recurring);
  }

  @Test
  void calculateNextRun_shouldReturnNextDay() {
    LocalDateTime now = LocalDateTime.now();
    LocalDateTime expected = now.plusDays(1);

    LocalDateTime result = recurringService.calculateNextRun(Frequency.DAILY, now);

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

    LocalDateTime result = recurringService.calculateNextRun(Frequency.WEEKLY, now);

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

    LocalDateTime result = recurringService.calculateNextRun(Frequency.MONTHLY, now);

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

    LocalDateTime result = recurringService.calculateNextRun(Frequency.YEARLY, now);

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

    doReturn(expectedNextDate)
        .when(spyService)
        .calculateNextRun(recurring.getFrequency(), recurring.getNextDate());

    when(transactionService.createTransaction(
        any(CreateTransactionDTO.class),
        eq(recurring.getAccount().getUser())
    )).thenReturn(successResult);

    spyService.processRecurring(recurring);

    ArgumentCaptor<CreateTransactionDTO> dtoCaptor = ArgumentCaptor.forClass(CreateTransactionDTO.class);

    verify(transactionService).createTransaction(
        dtoCaptor.capture(),
        eq(recurring.getAccount().getUser())
    );

    CreateTransactionDTO capturedDto = dtoCaptor.getValue();

    // Agora validamos se o DTO foi montado com os dados do RecurringEntity
    assertEquals(recurring.getAmount(), capturedDto.amount());
    assertEquals(recurring.getDescription(), capturedDto.description());
    assertEquals(recurring.getCategory().getCategoryId(), capturedDto.categoryId());
    // ---------------------------------------------

    verify(recurringRepository).save(recurring);
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
        () -> spyService.processRecurring(recurring)
    );

    assertTrue(ex.getMessage().contains("Failed to create transaction"));
    verify(recurringRepository, never()).save(Mockito.<RecurringEntity>any());
  }

  @Test
  void recurringTransaction_shouldProcessTheRecurring() {
    RecurringService spyService = Mockito.spy(recurringService);

    RecurringEntity recurring1 = RecurringEntityFactory.defaultEntity();
    RecurringEntity recurring2 = RecurringEntityFactory.defaultEntity();
    List<RecurringEntity> recurrences = List.of(recurring1, recurring2);

    when(recurringRepository.findByNextDateBefore())
        .thenReturn(recurrences)
        .thenReturn(Collections.emptyList());

    doNothing().when(spyService).processRecurring(any(RecurringEntity.class));

    spyService.recurringTransactions();

    verify(recurringRepository, times(2)).findByNextDateBefore();

    verify(spyService, times(1)).processRecurring(recurring1);
    verify(spyService, times(1)).processRecurring(recurring2);

    verify(spyService, times(recurrences.size())).processRecurring(any());
  }

  @Test
  void update_shouldUpdateRecurringSuccessfully() {
    RecurringService recurringServiceSpy = Mockito.spy(recurringService);

    CreateRecurringRequest request = CreateRecurringRequestFactory.defaultRequest();
    UserEntity currentUser = UserEntityFactory.defaultUser();

    RecurringEntity existingRecurring = RecurringEntityFactory.defaultEntity();
    CategoryEntity newCategory = CategoryEntityFactory.defaultCategory();
    RecurringResponse expectedResponse = RecurringResponseFactory.defaultResponse();

    LocalDateTime newNextDate = LocalDateTime.now().plusMonths(2);

    when(categoryService.findById(request.categoryId())).thenReturn(newCategory);
    when(recurringRepository.findByIdAndAccount(1l, currentUser.getAccount().getAccountId()))
        .thenReturn(Optional.of(existingRecurring));

    doReturn(newNextDate)
        .when(recurringServiceSpy)
        .calculateNextRun(request.frequency(), request.recurrenceDay());

    when(recurringRepository.save(existingRecurring)).thenReturn(existingRecurring);
    when(recurringMapper.toResponse(existingRecurring)).thenReturn(expectedResponse);

    RecurringResponse result = recurringServiceSpy.update(1l, request, currentUser);

    assertNotNull(result);
    assertEquals(expectedResponse, result);

    assertEquals(newCategory, existingRecurring.getCategory());
    assertEquals(request.transactionName(), existingRecurring.getTransactionName());
    assertEquals(newNextDate, existingRecurring.getNextDate());
    assertEquals(request.description(), existingRecurring.getDescription());
    assertEquals(request.transactionType(), existingRecurring.getTransactionType());
    assertEquals(request.amount(), existingRecurring.getAmount());
    assertEquals(request.frequency(), existingRecurring.getFrequency());

    verify(categoryService, times(1)).findById(request.categoryId());
    verify(recurringRepository, times(1)).findByIdAndAccount(1l, currentUser.getAccount().getAccountId());
    verify(recurringRepository, times(1)).save(existingRecurring);
  }

  @Test
  void update_shouldReturnNullWhenCategoryNotFound() {
    RecurringService recurringServiceSpy = Mockito.spy(recurringService);

    CreateRecurringRequest request = CreateRecurringRequestFactory.defaultRequest();
    UserEntity currentUser = UserEntityFactory.defaultUser();

    when(categoryService.findById(request.categoryId())).thenReturn(null);

    RecurringResponse result = recurringServiceSpy.update(1l, request, currentUser);

    assertNull(result);

    verify(categoryService, times(1)).findById(request.categoryId());
    verify(recurringRepository, never()).findByIdAndAccount(any(), any());
    verify(recurringRepository, never()).save(any());
  }

  @Test
  void update_shouldReturnNullWhenRecurringNotFoundForUser() {
    RecurringService recurringServiceSpy = Mockito.spy(recurringService);

    CreateRecurringRequest request = CreateRecurringRequestFactory.defaultRequest();
    UserEntity currentUser = UserEntityFactory.defaultUser();
    CategoryEntity category = CategoryEntityFactory.defaultCategory();

    when(categoryService.findById(request.categoryId())).thenReturn(category);
    when(recurringRepository.findByIdAndAccount(1l, currentUser.getAccount().getAccountId()))
        .thenReturn(Optional.empty());

    RecurringResponse result = recurringServiceSpy.update(1l, request, currentUser);

    assertNull(result);

    verify(categoryService, times(1)).findById(request.categoryId());
    verify(recurringRepository, times(1)).findByIdAndAccount(1l,currentUser.getAccount().getAccountId());
    verify(recurringRepository, never()).save(any());
  }
}

