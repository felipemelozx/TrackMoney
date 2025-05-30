package fun.trackmoney.transaction.service;

import fun.trackmoney.account.dtos.AccountResponseDTO;
import fun.trackmoney.account.entity.AccountEntity;
import fun.trackmoney.account.mapper.AccountMapper;
import fun.trackmoney.account.service.AccountService;
import fun.trackmoney.category.entity.CategoryEntity;
import fun.trackmoney.category.service.CategoryService;
import fun.trackmoney.enums.TransactionType;
import fun.trackmoney.transaction.dto.CreateTransactionDTO;
import fun.trackmoney.transaction.dto.TransactionResponseDTO;
import fun.trackmoney.transaction.dto.TransactionUpdateDTO;
import fun.trackmoney.transaction.entity.TransactionEntity;
import fun.trackmoney.transaction.exception.TransactionNotFoundException;
import fun.trackmoney.transaction.mapper.TransactionMapper;
import fun.trackmoney.transaction.repository.TransactionRepository;
import fun.trackmoney.user.dtos.UserResponseDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.*;

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
  void createTransaction_shouldReturnResponseDTO() {
    CreateTransactionDTO dto = new CreateTransactionDTO(
        1, 2, TransactionType.INCOME, BigDecimal.valueOf(100), "Test", new Timestamp(System.currentTimeMillis()));

    AccountEntity accountEntity = new AccountEntity();
    CategoryEntity categoryEntity = new CategoryEntity();
    TransactionEntity entity = new TransactionEntity();
    TransactionEntity saved = new TransactionEntity();
    TransactionResponseDTO response = new TransactionResponseDTO(1, "Test", BigDecimal.valueOf(100), null);
    UserResponseDTO user = new UserResponseDTO(UUID.randomUUID(), "Jhon Doe", "example@gmail.com");

    when(accountService.findAccountById(1)).thenReturn(new AccountResponseDTO(1, user, "btg", BigDecimal.valueOf(100), true));
    when(accountMapper.accountResponseToEntity(any())).thenReturn(accountEntity);
    when(categoryService.findById(2)).thenReturn(categoryEntity);
    when(transactionMapper.createTransactionToEntity(dto)).thenReturn(entity);
    when(transactionRepository.save(entity)).thenReturn(saved);
    when(transactionMapper.toResponseDTO(saved)).thenReturn(response);

    TransactionResponseDTO result = transactionService.createTransaction(dto);

    assertEquals("Test", result.description());
    verify(transactionRepository).save(entity);
  }

  @Test
  void findAllTransaction_shouldReturnListOfResponseDTOs() {
    TransactionEntity entity = new TransactionEntity();
    List<TransactionEntity> entities = List.of(entity);
    TransactionResponseDTO dto = new TransactionResponseDTO(1, "Test", BigDecimal.TEN, null);

    when(transactionRepository.findAll()).thenReturn(entities);
    when(transactionMapper.toResponseDTOList(entities)).thenReturn(List.of(dto));

    List<TransactionResponseDTO> result = transactionService.findAllTransaction();

    assertEquals(1, result.size());
    assertEquals("Test", result.get(0).description());
  }

  @Test
  void findById_shouldReturnTransactionResponseDTO() {
    TransactionEntity entity = new TransactionEntity();
    TransactionResponseDTO dto = new TransactionResponseDTO(1, "Test", BigDecimal.TEN, null);

    when(transactionRepository.findById(1)).thenReturn(Optional.of(entity));
    when(transactionMapper.toResponseDTO(entity)).thenReturn(dto);

    TransactionResponseDTO result = transactionService.findById(1);

    assertEquals("Test", result.description());
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
    TransactionResponseDTO dtoResponse = new TransactionResponseDTO(1, "Updated", BigDecimal.valueOf(200), null);

    TransactionUpdateDTO dto = new TransactionUpdateDTO("Updated", BigDecimal.valueOf(200), 1, 2, TransactionType.EXPENSE);
    UserResponseDTO user = new UserResponseDTO(UUID.randomUUID(), "Jane", "jane@mail.com");

    when(transactionRepository.findById(1)).thenReturn(Optional.of(entity));
    when(categoryService.findById(2)).thenReturn(categoryEntity);
    when(accountService.findAccountById(1)).thenReturn(new AccountResponseDTO(1, user, "btg", BigDecimal.valueOf(100), true));
    when(accountMapper.accountResponseToEntity(any())).thenReturn(accountEntity);
    when(transactionRepository.save(entity)).thenReturn(entity);
    when(transactionMapper.toResponseDTO(entity)).thenReturn(dtoResponse);

    TransactionResponseDTO result = transactionService.update(1, dto);

    assertEquals("Updated", result.description());
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
    when(transactionRepository.findAllByUserEmail(accountId)).thenReturn(List.of(tr1, tr2));

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
    when(transactionRepository.findAllByUserEmail(accountId)).thenReturn(List.of(tr1, tr2));

    var result = transactionService.getExpense(accountId);

    assertEquals(totalIncome, result);
  }
}
