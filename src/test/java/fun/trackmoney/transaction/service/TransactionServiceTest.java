package fun.trackmoney.transaction.service;

import fun.trackmoney.account.entity.AccountEntity;
import fun.trackmoney.account.service.AccountService;
import fun.trackmoney.category.entity.CategoryEntity;
import fun.trackmoney.category.service.CategoryService;
import fun.trackmoney.testutils.AccountEntityFactory;
import fun.trackmoney.testutils.CategoryEntityFactory;
import fun.trackmoney.testutils.CreateTransactionDTOBuilder;
import fun.trackmoney.testutils.TransactionEntityFactory;
import fun.trackmoney.testutils.TransactionResponseDTOFactory;
import fun.trackmoney.testutils.TransactionUpdateDTOFactory;
import fun.trackmoney.testutils.UserEntityFactory;
import fun.trackmoney.transaction.dto.CreateTransactionDTO;
import fun.trackmoney.transaction.dto.TransactionResponseDTO;
import fun.trackmoney.transaction.dto.TransactionUpdateDTO;
import fun.trackmoney.transaction.enums.DateFilterEnum;
import fun.trackmoney.transaction.enums.TransactionsError;
import fun.trackmoney.transaction.dto.internal.TransactionFailure;
import fun.trackmoney.transaction.dto.internal.TransactionResult;
import fun.trackmoney.transaction.dto.internal.TransactionSuccess;
import fun.trackmoney.transaction.entity.TransactionEntity;
import fun.trackmoney.transaction.exception.TransactionNotFoundException;
import fun.trackmoney.transaction.mapper.TransactionMapper;
import fun.trackmoney.transaction.repository.TransactionRepository;
import fun.trackmoney.user.entity.UserEntity;
import fun.trackmoney.utils.DateFilterUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {

  @InjectMocks
  @Spy
  private TransactionService transactionService;

  @Mock
  private TransactionRepository transactionRepository;

  @Mock
  private TransactionMapper transactionMapper;

  @Mock
  private AccountService accountService;

  @Mock
  private CategoryService categoryService;


  @Test
  void shouldReturnTransactionSuccessWhenTransactionIsCreatedSuccessfully() {
    UserEntity currentUser = UserEntityFactory.defaultUser();
    CreateTransactionDTO dto = CreateTransactionDTOBuilder.incomeTransaction();
    AccountEntity account = AccountEntityFactory.defaultAccount();
    CategoryEntity category = CategoryEntityFactory.defaultCategory();
    TransactionEntity transaction = TransactionEntityFactory.defaultExpenseNow();
    TransactionResponseDTO responseDTO = TransactionResponseDTOFactory.defaultTransactionResponse();

    when(categoryService.findById(dto.categoryId())).thenReturn(category);
    when(transactionMapper.createTransactionToEntity(dto)).thenReturn(transaction);
    when(transactionRepository.save(transaction)).thenReturn(transaction);
    when(accountService.updateAccountBalance(dto.amount(), account.getAccountId(), true)).thenReturn(true);
    when(transactionMapper.toResponseDTO(transaction)).thenReturn(responseDTO);

    TransactionResult result = transactionService.createTransaction(dto, currentUser);

    assertInstanceOf(TransactionSuccess.class, result);
    TransactionSuccess transactionSuccess = (TransactionSuccess) result;
    assertEquals("buy bread", transactionSuccess.response().description());
    verify(transactionRepository, times(1)).save(transaction);
    verify(categoryService, times(1)).findById(dto.categoryId());
  }

  @Test
  void shouldReturnTransactionFailureWhenAccountIsNotFound() {
    UserEntity currentUser = UserEntityFactory.customUser(UUID.randomUUID(), "some name", "some@gmail", "somepass",true, null);
    CreateTransactionDTO dto = CreateTransactionDTOBuilder.defaultTransaction();

    TransactionResult result = transactionService.createTransaction(dto, currentUser);

    assertInstanceOf(TransactionFailure.class, result);
    TransactionFailure failure = (TransactionFailure) result;
    assertEquals(TransactionsError.ACCOUNT_NOT_FOUND, failure.error());

    verifyNoInteractions(categoryService, transactionRepository, transactionMapper);
  }

  @Test
  void shouldReturnTransactionFailureWhenCategoryIsNotFound() {
    UserEntity currentUser = UserEntityFactory.defaultUser();
    CreateTransactionDTO dto = CreateTransactionDTOBuilder.defaultTransaction();

    when(categoryService.findById(dto.categoryId())).thenReturn(null);

    TransactionResult result = transactionService.createTransaction(dto, currentUser);

    assertInstanceOf(TransactionFailure.class, result);
    TransactionFailure failure = (TransactionFailure) result;
    assertEquals(TransactionsError.CATEGORY_NOT_FOUND, failure.error());

    verify(categoryService, times(1)).findById(dto.categoryId());
    verifyNoInteractions(transactionRepository, transactionMapper);
  }

  @Test
  void findAllTransaction_shouldReturnListOfResponseDTOs() {
    UserEntity currentUser = UserEntityFactory.defaultUser();
    TransactionEntity entity = new TransactionEntity();
    List<TransactionEntity> entities = List.of(entity);
    TransactionResponseDTO dto = TransactionResponseDTOFactory.defaultTransactionResponse();

    when(transactionRepository.findAllByAccountId(currentUser.getAccount().getAccountId())).thenReturn(List.of(entity));
    when(transactionMapper.toResponseDTOList(entities)).thenReturn(List.of(dto));

    List<TransactionResponseDTO> result = transactionService.findAllTransaction(currentUser);

    assertEquals(1, result.size());
    assertEquals("buy bread", result.get(0).description());
  }

  @Test
  void findById_shouldReturnTransactionResponseDTO() {
    TransactionEntity entity = TransactionEntityFactory.defaultExpenseNow();
    TransactionResponseDTO dto = TransactionResponseDTOFactory.defaultTransactionResponse();
    UserEntity user = UserEntityFactory.defaultUser();

    when(transactionRepository.findByIdAndAccount(entity.getTransactionId(), user.getAccount())).thenReturn(Optional.of(entity));
    when(transactionMapper.toResponseDTO(entity)).thenReturn(dto);

    TransactionResponseDTO result = transactionService.findById(1, user);

    assertEquals("buy bread", result.description());
  }

  @Test
  void findById_shouldThrowExceptionIfNotFound() {
    UserEntity user = UserEntityFactory.defaultUser();

    when(transactionRepository.findByIdAndAccount(1, user.getAccount())).thenReturn(Optional.empty());
    TransactionNotFoundException exception = assertThrows(TransactionNotFoundException.class, () -> transactionService.findById(1, user));
    assertEquals("Transaction not found.", exception.getMessage());
  }

  @Test
  void update_shouldUpdateAndReturnTransactionResponseDTO() {
    UserEntity currentUser = UserEntityFactory.defaultUser();
    TransactionEntity entity = TransactionEntityFactory.defaultExpenseNow();
    AccountEntity accountEntity = currentUser.getAccount();
    CategoryEntity categoryEntity = entity.getCategory();
    TransactionResponseDTO dtoResponse = TransactionResponseDTOFactory.defaultTransactionResponse();

    TransactionUpdateDTO dto = TransactionUpdateDTOFactory.defaultUpdateTransaction();

    when(transactionRepository.findByIdAndAccount(1, accountEntity)).thenReturn(Optional.of(entity));
    when(categoryService.findById(1)).thenReturn(categoryEntity);
    when(transactionRepository.save(entity)).thenReturn(entity);
    when(transactionMapper.toResponseDTO(entity)).thenReturn(dtoResponse);

    TransactionResponseDTO result = transactionService.update(1, dto, currentUser);

    assertEquals("buy bread", result.description());
  }

  @Test
  void update_shouldUpdateAndReturnTransactionResponseDTOWhenIsIncome() {
    UserEntity currentUser = UserEntityFactory.defaultUser();
    TransactionEntity entity = TransactionEntityFactory.defaultIncomeNow();
    AccountEntity accountEntity = currentUser.getAccount();
    CategoryEntity categoryEntity = entity.getCategory();
    TransactionResponseDTO dtoResponse = TransactionResponseDTOFactory.defaultTransactionResponse();

    TransactionUpdateDTO dto = TransactionUpdateDTOFactory.defaultUpdateTransactionIncome();

    when(transactionRepository.findByIdAndAccount(1, accountEntity)).thenReturn(Optional.of(entity));
    when(categoryService.findById(1)).thenReturn(categoryEntity);
    when(transactionRepository.save(entity)).thenReturn(entity);
    when(transactionMapper.toResponseDTO(entity)).thenReturn(dtoResponse);

    TransactionResponseDTO result = transactionService.update(1, dto, currentUser);

    assertEquals("buy bread", result.description());
  }


  @Test
  void delete_shouldCallRepositoryDeleteById() {
    UserEntity currentUser = UserEntityFactory.defaultUser();
    transactionService.delete(1, currentUser);
    verify(transactionRepository).deleteByIdAndAccountId(1, currentUser.getAccount());
  }

  @Test
  void shouldReturnAmountWhenMonthAndYearMatch() {
    var user = UserEntityFactory.defaultUser();
    var account = AccountEntityFactory.defaultAccount();

    var tr1 = TransactionEntityFactory.defaultIncomeNow();
    var tr2 = TransactionEntityFactory.defaultIncomeNow();

    when(transactionRepository.findAllByAccountId(account.getAccountId()))
        .thenReturn(List.of(tr1, tr2));
    when(accountService.findAccountDefaultByUserId(user.getUserId()))
        .thenReturn(account);

    var result = transactionService.getIncome(user.getUserId());

    assertEquals(BigDecimal.valueOf(5000.0), result);
    verify(accountService).findAccountDefaultByUserId(user.getUserId());
    verify(transactionRepository).findAllByAccountId(account.getAccountId());
  }

  @Test
  void shouldReturnZeroIncomeWhenMonthMatchesButYearDoesNot() {
    var user = UserEntityFactory.defaultUser();
    var account = AccountEntityFactory.defaultAccount();

    var tr = TransactionEntityFactory.expenseCurrentMonthWrongYear(BigDecimal.valueOf(100), "Wrong year", 0);

    when(transactionRepository.findAllByAccountId(account.getAccountId()))
        .thenReturn(List.of(tr));
    when(accountService.findAccountDefaultByUserId(user.getUserId()))
        .thenReturn(account);

    var result = transactionService.getIncome(user.getUserId());

    assertEquals(BigDecimal.ZERO, result);
  }

  @Test
  void shouldReturnZeroIncomeWhenMonthDoesNotMatchButYearMatches() {
    var user = UserEntityFactory.defaultUser();
    var account = AccountEntityFactory.defaultAccount();

    var tr = TransactionEntityFactory.expenseWrongMonthCurrentYear(BigDecimal.valueOf(100), "Wrong month", 0);

    when(transactionRepository.findAllByAccountId(account.getAccountId()))
        .thenReturn(List.of(tr));
    when(accountService.findAccountDefaultByUserId(user.getUserId()))
        .thenReturn(account);

    var result = transactionService.getIncome(user.getUserId());

    assertEquals(BigDecimal.ZERO, result);
  }

  @Test
  void shouldReturnZeroIncomeWhenMonthAndYearDoNotMatch() {
    var user = UserEntityFactory.defaultUser();
    var account = AccountEntityFactory.defaultAccount();

    var tr = TransactionEntityFactory.expenseWrongMonthAndYear(BigDecimal.valueOf(100), "Wrong month and year", 0);

    when(transactionRepository.findAllByAccountId(account.getAccountId()))
        .thenReturn(List.of(tr));

    var result = transactionService.getExpense(user);

    assertEquals(BigDecimal.ZERO, result);
  }

  @Test
  void shouldReturnAmountIncomeWhenMonthAndYearMatch() {
    var user = UserEntityFactory.defaultUser();
    var account = AccountEntityFactory.defaultAccount();

    var tr1 = TransactionEntityFactory.defaultExpenseNow();
    var tr2 = TransactionEntityFactory.defaultExpenseNow();

    when(transactionRepository.findAllByAccountId(account.getAccountId()))
        .thenReturn(List.of(tr1, tr2));

    var result = transactionService.getExpense(user);

    assertEquals(BigDecimal.valueOf(200.0), result);
    verify(transactionRepository).findAllByAccountId(account.getAccountId());
  }

  @Test
  void shouldReturnZeroWhenMonthMatchesButYearDoesNot() {
    var user = UserEntityFactory.defaultUser();
    var account = AccountEntityFactory.defaultAccount();

    var tr = TransactionEntityFactory.expenseCurrentMonthWrongYear(BigDecimal.valueOf(100), "Wrong year",1);

    when(transactionRepository.findAllByAccountId(account.getAccountId()))
        .thenReturn(List.of(tr));

    var result = transactionService.getExpense(user);

    assertEquals(BigDecimal.ZERO, result);
  }

  @Test
  void shouldReturnZeroWhenMonthDoesNotMatchButYearMatches() {
    var user = UserEntityFactory.defaultUser();
    var account = AccountEntityFactory.defaultAccount();

    var tr = TransactionEntityFactory.expenseWrongMonthCurrentYear(BigDecimal.valueOf(100), "Wrong month",1);

    when(transactionRepository.findAllByAccountId(account.getAccountId()))
        .thenReturn(List.of(tr));

    var result = transactionService.getExpense(user);

    assertEquals(BigDecimal.ZERO, result);
  }

  @Test
  void shouldReturnZeroWhenMonthAndYearDoNotMatch() {
    var user = UserEntityFactory.defaultUser();
    var account = AccountEntityFactory.defaultAccount();

    var tr = TransactionEntityFactory.expenseWrongMonthAndYear(BigDecimal.valueOf(100), "Wrong month and year",1);

    when(transactionRepository.findAllByAccountId(account.getAccountId()))
        .thenReturn(List.of(tr));

    var result = transactionService.getExpense(user);

    assertEquals(BigDecimal.ZERO, result);
  }

  @Test
  void shouldReturnPaginatedTransactions() {
    Pageable pageable = PageRequest.of(0, 5);
    UserEntity user = UserEntityFactory.defaultUser();

    TransactionEntity transaction = TransactionEntityFactory.defaultExpenseNow();
    TransactionResponseDTO dto = TransactionResponseDTOFactory.defaultTransactionResponse();

    Page<TransactionEntity> transactionPage = new PageImpl<>(List.of(transaction));

    when(transactionRepository.findAllByFilters(user.getAccount().getAccountId(), "" ,null, null, null, pageable)).thenReturn(transactionPage);
    when(transactionMapper.toResponseDTO(transaction)).thenReturn(dto);

    Page<TransactionResponseDTO> result = transactionService.getPaginatedTransactions(pageable, user, null, null, null);

    assertThat(result.getContent()).containsExactly(dto);
    verify(transactionRepository, times(1)).findAllByFilters(user.getAccount().getAccountId(), "" ,null, null, null, pageable);
    verify(transactionMapper, times(1)).toResponseDTO(transaction);
  }

  @Test
  void shouldUseEmptyNameAndNullDatesWhenDateFilterAndNameAreNull() {
    Pageable pageable = PageRequest.of(0, 5);
    UserEntity user = UserEntityFactory.defaultUser();
    TransactionEntity transaction = TransactionEntityFactory.defaultExpenseNow();
    TransactionResponseDTO dto = TransactionResponseDTOFactory.defaultTransactionResponse();

    Page<TransactionEntity> transactionPage = new PageImpl<>(List.of(transaction));

    when(transactionRepository.findAllByFilters(
        user.getAccount().getAccountId(),
        "",
        null,
        null,
        null,
        pageable
    )).thenReturn(transactionPage);

    when(transactionMapper.toResponseDTO(transaction)).thenReturn(dto);

    Page<TransactionResponseDTO> result = transactionService.getPaginatedTransactions(pageable, user, null, null, null);

    assertThat(result.getContent()).containsExactly(dto);
    verify(transactionRepository, times(1)).findAllByFilters(
        user.getAccount().getAccountId(), "", null, null, null, pageable);
    verify(transactionMapper, times(1)).toResponseDTO(transaction);
  }

  @Test
  void shouldUseDateRangeWhenDateFilterIsProvided() {
    Pageable pageable = PageRequest.of(0, 5);
    UserEntity user = UserEntityFactory.defaultUser();
    TransactionEntity transaction = TransactionEntityFactory.defaultExpenseNow();
    TransactionResponseDTO dto = TransactionResponseDTOFactory.defaultTransactionResponse();

    Page<TransactionEntity> transactionPage = new PageImpl<>(List.of(transaction));


    LocalDateTime start = LocalDateTime.now().minusDays(7);
    LocalDateTime end = LocalDateTime.now();
    var mockDateRange = new DateFilterUtil.DateRange(start, end);

    try (MockedStatic<DateFilterUtil> mocked = mockStatic(DateFilterUtil.class)) {
      mocked.when(() -> DateFilterUtil.getDateRange(DateFilterEnum.LAST_MONTH))
          .thenReturn(mockDateRange);

      when(transactionRepository.findAllByFilters(
          user.getAccount().getAccountId(),
          "Food",
          1L,
          start,
          end,
          pageable
      )).thenReturn(transactionPage);

      when(transactionMapper.toResponseDTO(transaction)).thenReturn(dto);

      Page<TransactionResponseDTO> result = transactionService.getPaginatedTransactions(
          pageable, user, "Food", 1L, DateFilterEnum.LAST_MONTH);

      assertThat(result.getContent()).containsExactly(dto);
      verify(transactionRepository, times(1)).findAllByFilters(
          user.getAccount().getAccountId(), "Food", 1L, start, end, pageable);
    }
  }

  @Test
  void getLast5TransactionsPerCategory_shouldReturnGroupedMapOfTransactions() {
    Integer accountId = 1;
    CategoryEntity categoryFood = CategoryEntityFactory.defaultCategory();
    CategoryEntity categoryRent = CategoryEntityFactory.customCategory(2, "Rent", "blue");

    TransactionEntity t1Food = TransactionEntityFactory.defaultExpenseNow().setTransactionId(1).setCategory(categoryFood);
    TransactionEntity t2Rent = TransactionEntityFactory.defaultExpenseNow().setTransactionId(2).setCategory(categoryRent);
    TransactionEntity t3Food = TransactionEntityFactory.defaultExpenseNow().setTransactionId(3).setCategory(categoryFood);

    List<TransactionEntity> entities = List.of(t1Food, t2Rent, t3Food);

    TransactionResponseDTO dto1Food = TransactionResponseDTOFactory.transactionResponseCustomId(1);
    TransactionResponseDTO dto2Rent = TransactionResponseDTOFactory.transactionResponseCustomId(2);
    TransactionResponseDTO dto3Food = TransactionResponseDTOFactory.transactionResponseCustomId(3);

    when(transactionRepository.findLast5TransactionsPerCategory(accountId)).thenReturn(entities);

    when(transactionMapper.toResponseDTO(t1Food)).thenReturn(dto1Food);
    when(transactionMapper.toResponseDTO(t2Rent)).thenReturn(dto2Rent);
    when(transactionMapper.toResponseDTO(t3Food)).thenReturn(dto3Food);

    Map<CategoryEntity, List<TransactionResponseDTO>> result =
        transactionService.getLast5TransactionsPerCategory(accountId);

    assertNotNull(result);
    assertEquals(2, result.size());

    assertTrue(result.containsKey(categoryFood));
    assertEquals(2, result.get(categoryFood).size());

    assertTrue(result.containsKey(categoryRent));
    assertEquals(1, result.get(categoryRent).size());

    verify(transactionRepository, times(1)).findLast5TransactionsPerCategory(accountId);
    verify(transactionMapper, times(3)).toResponseDTO(any(TransactionEntity.class));
  }

  @Test
  void getLast5TransactionsPerCategory_shouldReturnEmptyMapWhenNoTransactionsFound() {
    Integer accountId = 1;

    when(transactionRepository.findLast5TransactionsPerCategory(accountId)).thenReturn(List.of());

    Map<CategoryEntity, List<TransactionResponseDTO>> result =
        transactionService.getLast5TransactionsPerCategory(accountId);

    assertNotNull(result);
    assertTrue(result.isEmpty());

    verify(transactionMapper, never()).toResponseDTO(any());
  }

  @Test
  void getCurrentMonthTransactions_shouldOnlyReturnTransactionsInCurrentMonth() {
    LocalDateTime startOfMonth = LocalDate.now().withDayOfMonth(1).atStartOfDay();
    LocalDateTime endOfNow = LocalDateTime.now();

    TransactionEntity currentMonthTransaction = TransactionEntityFactory.defaultExpenseNow();

    List<TransactionEntity> expectedResult = List.of(currentMonthTransaction);

    ArgumentCaptor<LocalDateTime> startDateCaptor = ArgumentCaptor.forClass(LocalDateTime.class);
    ArgumentCaptor<LocalDateTime> endDateCaptor = ArgumentCaptor.forClass(LocalDateTime.class);

    when(transactionRepository.findAllBetweenDates(startDateCaptor.capture(), endDateCaptor.capture()))
        .thenReturn(expectedResult);

    List<TransactionEntity> result = transactionService.getCurrentMonthTransactions();

    assertNotNull(result);
    assertEquals(1, result.size());
    assertEquals(currentMonthTransaction.getTransactionId(), result.get(0).getTransactionId());

    verify(transactionRepository, times(1)).findAllBetweenDates(any(LocalDateTime.class), any(LocalDateTime.class));

    assertEquals(startOfMonth.getYear(), startDateCaptor.getValue().getYear());
    assertEquals(startOfMonth.getMonth(), startDateCaptor.getValue().getMonth());
    assertEquals(1, startDateCaptor.getValue().getDayOfMonth());
    assertEquals(0, startDateCaptor.getValue().getHour());

    long timeDifferenceSeconds = Math.abs(java.time.Duration.between(endOfNow, endDateCaptor.getValue()).getSeconds());
    assertTrue(timeDifferenceSeconds < 5, "A data final deve estar dentro de 5 segundos do momento da execução.");
  }

  @Test
  void getCurrentMonthTransactions_shouldReturnEmptyListWhenNoTransactionsFound() {
    when(transactionRepository.findAllBetweenDates(any(LocalDateTime.class), any(LocalDateTime.class)))
        .thenReturn(List.of());

    List<TransactionEntity> result = transactionService.getCurrentMonthTransactions();

    assertNotNull(result);
    assertTrue(result.isEmpty());
    verify(transactionRepository, times(1)).findAllBetweenDates(any(), any());
  }
}
