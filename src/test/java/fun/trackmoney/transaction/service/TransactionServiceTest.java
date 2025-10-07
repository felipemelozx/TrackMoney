package fun.trackmoney.transaction.service;

import fun.trackmoney.account.dtos.AccountResponseDTO;
import fun.trackmoney.account.entity.AccountEntity;
import fun.trackmoney.account.mapper.AccountMapper;
import fun.trackmoney.account.service.AccountService;
import fun.trackmoney.category.entity.CategoryEntity;
import fun.trackmoney.category.service.CategoryService;
import fun.trackmoney.enums.TransactionType;
import fun.trackmoney.testutils.AccountEntityFactory;
import fun.trackmoney.testutils.CategoryEntityFactory;
import fun.trackmoney.testutils.CreateTransactionDTOBuilder;
import fun.trackmoney.testutils.TransactionEntityFactory;
import fun.trackmoney.testutils.TransactionResponseDTOFactory;
import fun.trackmoney.transaction.dto.CreateTransactionDTO;
import fun.trackmoney.transaction.dto.TransactionResponseDTO;
import fun.trackmoney.transaction.dto.TransactionUpdateDTO;
import fun.trackmoney.transaction.dto.TransactionsError;
import fun.trackmoney.transaction.dto.internal.TransactionFailure;
import fun.trackmoney.transaction.dto.internal.TransactionResult;
import fun.trackmoney.transaction.dto.internal.TransactionSuccess;
import fun.trackmoney.transaction.entity.TransactionEntity;
import fun.trackmoney.transaction.exception.TransactionNotFoundException;
import fun.trackmoney.transaction.mapper.TransactionMapper;
import fun.trackmoney.transaction.repository.TransactionRepository;
import fun.trackmoney.user.dtos.UserResponseDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TransactionServiceTest {

  @InjectMocks
  private TransactionService transactionService;

  @Mock
  private TransactionRepository transactionRepository;

  @Mock
  private TransactionMapper transactionMapper;

  @Mock
  private AccountService accountService;

  @Mock
  private AccountMapper accountMapper;

  @Mock
  private CategoryService categoryService;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  void shouldReturnTransactionSuccessWhenTransactionIsCreatedSuccessfully() {
    UUID userId = UUID.randomUUID();
    CreateTransactionDTO dto = CreateTransactionDTOBuilder.incomeTransaction();
    AccountEntity account = AccountEntityFactory.defaultAccount();
    CategoryEntity category = CategoryEntityFactory.defaultCategory();
    TransactionEntity transaction = TransactionEntityFactory.defaultTransaction();
    TransactionResponseDTO responseDTO = TransactionResponseDTOFactory.defaultTransactionResponse();

    when(accountService.findAccountDefaultByUserId(userId)).thenReturn(account);
    when(categoryService.findById(dto.categoryId())).thenReturn(category);
    when(transactionMapper.createTransactionToEntity(dto)).thenReturn(transaction);
    when(transactionRepository.save(transaction)).thenReturn(transaction);
    when(accountService.updateAccountBalance(dto.amount(), account.getAccountId(), true)).thenReturn(true);
    when(transactionMapper.toResponseDTO(transaction)).thenReturn(responseDTO);

    TransactionResult result = transactionService.createTransaction(dto, userId);

    assertInstanceOf(TransactionSuccess.class, result);
    TransactionSuccess transactionSuccess = (TransactionSuccess) result;
    assertEquals("buy bread", transactionSuccess.response().description());
    verify(transactionRepository, times(1)).save(transaction);
    verify(accountService, times(1)).findAccountDefaultByUserId(userId);
    verify(categoryService, times(1)).findById(dto.categoryId());
  }

  @Test
  void shouldReturnTransactionFailureWhenAccountIsNotFound() {
    UUID userId = UUID.randomUUID();
    CreateTransactionDTO dto = CreateTransactionDTOBuilder.defaultTransaction();

    when(accountService.findAccountDefaultByUserId(userId)).thenReturn(null);

    TransactionResult result = transactionService.createTransaction(dto, userId);

    assertInstanceOf(TransactionFailure.class, result);
    TransactionFailure failure = (TransactionFailure) result;
    assertEquals(TransactionsError.ACCOUNT_NOT_FOUND, failure.error());

    verify(accountService, times(1)).findAccountDefaultByUserId(userId);
    verifyNoInteractions(categoryService, transactionRepository, transactionMapper);
  }

  @Test
  void shouldReturnTransactionFailureWhenCategoryIsNotFound() {
    UUID userId = UUID.randomUUID();
    CreateTransactionDTO dto = CreateTransactionDTOBuilder.defaultTransaction();
    AccountEntity account = AccountEntityFactory.defaultAccount();

    when(accountService.findAccountDefaultByUserId(userId)).thenReturn(account);
    when(categoryService.findById(dto.categoryId())).thenReturn(null);

    TransactionResult result = transactionService.createTransaction(dto, userId);

    assertInstanceOf(TransactionFailure.class, result);
    TransactionFailure failure = (TransactionFailure) result;
    assertEquals(TransactionsError.CATEGORY_NOT_FOUND, failure.error());

    verify(accountService, times(1)).findAccountDefaultByUserId(userId);
    verify(categoryService, times(1)).findById(dto.categoryId());
    verifyNoInteractions(transactionRepository, transactionMapper);
  }

  @Test
  void findAllTransaction_shouldReturnListOfResponseDTOs() {
    TransactionEntity entity = new TransactionEntity();
    List<TransactionEntity> entities = List.of(entity);
    TransactionResponseDTO dto = TransactionResponseDTOFactory.defaultTransactionResponse();

    when(transactionRepository.findAll()).thenReturn(entities);
    when(transactionMapper.toResponseDTOList(entities)).thenReturn(List.of(dto));

    List<TransactionResponseDTO> result = transactionService.findAllTransaction();

    assertEquals(1, result.size());
    assertEquals("buy bread", result.get(0).description());
  }

  @Test
  void findById_shouldReturnTransactionResponseDTO() {
    TransactionEntity entity = new TransactionEntity();
    TransactionResponseDTO dto = TransactionResponseDTOFactory.defaultTransactionResponse();

    when(transactionRepository.findById(1)).thenReturn(Optional.of(entity));
    when(transactionMapper.toResponseDTO(entity)).thenReturn(dto);

    TransactionResponseDTO result = transactionService.findById(1);

    assertEquals("buy bread", result.description());
  }

  @Test
  void findById_shouldThrowExceptionIfNotFound() {
    when(transactionRepository.findById(1)).thenReturn(Optional.empty());

    TransactionNotFoundException exception = assertThrows(TransactionNotFoundException.class, () -> transactionService.findById(1));
    assertEquals("Transaction not found.", exception.getMessage());
  }

  @Test
  void update_shouldUpdateAndReturnTransactionResponseDTO() {
    TransactionEntity entity = new TransactionEntity();
    AccountEntity accountEntity = new AccountEntity();
    CategoryEntity categoryEntity = new CategoryEntity();
    TransactionResponseDTO dtoResponse = TransactionResponseDTOFactory.defaultTransactionResponse();

    TransactionUpdateDTO dto = new TransactionUpdateDTO("Updated", BigDecimal.valueOf(200), 1, 2, TransactionType.EXPENSE);
    UserResponseDTO user = new UserResponseDTO(UUID.randomUUID(), "Jane", "jane@mail.com");

    when(transactionRepository.findById(1)).thenReturn(Optional.of(entity));
    when(categoryService.findById(2)).thenReturn(categoryEntity);
    when(accountService.findAccountById(1)).thenReturn(new AccountResponseDTO(1, user, "btg", BigDecimal.valueOf(100), true));
    when(accountMapper.accountResponseToEntity(any())).thenReturn(accountEntity);
    when(transactionRepository.save(entity)).thenReturn(entity);
    when(transactionMapper.toResponseDTO(entity)).thenReturn(dtoResponse);

    TransactionResponseDTO result = transactionService.update(1, dto);

    assertEquals("buy bread", result.description());
  }

  @Test
  void delete_shouldCallRepositoryDeleteById() {
    transactionService.delete(1);
    verify(transactionRepository).deleteById(1);
  }

  @Test
  void getIncome_shouldReturnTotalIncome() {
    BigDecimal totalIncome = BigDecimal.valueOf(100);
    var tr1 = new TransactionEntity();
    tr1.setTransactionType(TransactionType.INCOME);
    tr1.setAmount(BigDecimal.valueOf(50));
    var tr2 = new TransactionEntity();
    tr2.setAmount(BigDecimal.valueOf(50));
    tr2.setTransactionType(TransactionType.INCOME);

    var accountId = 1;
    when(transactionRepository.findAllByAccountId(accountId)).thenReturn(List.of(tr1, tr2));

    var result = transactionService.getIncome(accountId);

    assertEquals(totalIncome, result);
  }

  @Test
  void getExpense_shouldReturnTotalExpense() {
    BigDecimal totalIncome = BigDecimal.valueOf(100);
    var tr1 = new TransactionEntity();
    tr1.setTransactionType(TransactionType.EXPENSE);
    tr1.setAmount(BigDecimal.valueOf(50));
    var tr2 = new TransactionEntity();
    tr2.setAmount(BigDecimal.valueOf(50));
    tr2.setTransactionType(TransactionType.EXPENSE);

    var accountId = 1;
    when(transactionRepository.findAllByAccountId(accountId)).thenReturn(List.of(tr1, tr2));

    var result = transactionService.getExpense(accountId);

    assertEquals(totalIncome, result);
  }

  @Test
  void getBill_shouldReturnBillResponseDTO() {
    var accountId = 1;

    var transaction1 = new TransactionEntity();
    var transaction2 = new TransactionEntity();
    var transaction3 = new TransactionEntity();

    transaction1.setAmount(BigDecimal.valueOf(100));
    transaction2.setAmount(BigDecimal.valueOf(100));
    transaction3.setAmount(BigDecimal.valueOf(100));

    LocalDateTime baseDate = LocalDate.now().atStartOfDay();


    var day1 = Timestamp.valueOf(baseDate.minusDays(8));
    transaction1.setTransactionDate(day1);

    var day2 = Timestamp.valueOf(baseDate.plusDays(2));
    transaction2.setTransactionDate(day2);

    var day3 = Timestamp.valueOf(baseDate.plusDays(8));
    transaction3.setTransactionDate(day3);

    CategoryEntity category = new CategoryEntity(1, "bill", "anyColor");

    transaction1.setCategory(category);
    transaction2.setCategory(category);
    transaction3.setCategory(category);

    when(transactionRepository.findAllByAccountId(accountId)).thenReturn(List.of(transaction1, transaction2));

    var result = transactionService.getBill(accountId);

    assertNotNull(result);
    assertEquals(BigDecimal.valueOf(100), result.totalUpcoming());
    assertEquals(BigDecimal.valueOf(100), result.bill());
    assertEquals(BigDecimal.valueOf(100), result.bueSoon());
  }

  @Test
  void getBill_shouldReturnEmptyBillResponseDTOWhenNoBills() {
    var accountId = 1;

    var transaction1 = new TransactionEntity();
    var transaction2 = new TransactionEntity();
    var transaction3 = new TransactionEntity();

    transaction1.setAmount(BigDecimal.valueOf(100));
    transaction2.setAmount(BigDecimal.valueOf(100));
    transaction3.setAmount(BigDecimal.valueOf(100));

    LocalDateTime baseDate = LocalDate.now().atStartOfDay();


    var day1 = Timestamp.valueOf(baseDate.minusDays(8));
    transaction1.setTransactionDate(day1);

    var day2 = Timestamp.valueOf(baseDate.plusDays(2));
    transaction2.setTransactionDate(day2);

    var day3 = Timestamp.valueOf(baseDate.plusDays(8));
    transaction3.setTransactionDate(day3);

    CategoryEntity category = new CategoryEntity(1, "otherCategory", "anyColor");

    transaction1.setCategory(category);
    transaction2.setCategory(category);
    transaction3.setCategory(category);

    when(transactionRepository.findAllByAccountId(accountId)).thenReturn(List.of(transaction1, transaction2));

    var result = transactionService.getBill(accountId);

    assertNotNull(result);
    assertEquals(BigDecimal.valueOf(0), result.totalUpcoming());
    assertEquals(BigDecimal.valueOf(0), result.bill());
    assertEquals(BigDecimal.valueOf(0), result.bueSoon());
  }

  @Test
  void shouldReturnPaginatedTransactions() {
    Pageable pageable = PageRequest.of(0, 5);

    TransactionEntity transaction = new TransactionEntity(1, null, null, null,   BigDecimal.valueOf(100),"Some description", null );
    TransactionResponseDTO dto = TransactionResponseDTOFactory.defaultTransactionResponse();

    Page<TransactionEntity> transactionPage = new PageImpl<>(List.of(transaction));

    when(transactionRepository.findAll(pageable)).thenReturn(transactionPage);
    when(transactionMapper.toResponseDTO(transaction)).thenReturn(dto);

    Page<TransactionResponseDTO> result = transactionService.getPaginatedTransactions(pageable);

    assertThat(result.getContent()).containsExactly(dto);
    verify(transactionRepository).findAll(pageable);
    verify(transactionMapper).toResponseDTO(transaction);
  }
}
