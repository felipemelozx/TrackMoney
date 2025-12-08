package fun.trackmoney.transaction.service;

import fun.trackmoney.account.entity.AccountEntity;
import fun.trackmoney.account.service.AccountService;
import fun.trackmoney.category.entity.CategoryEntity;
import fun.trackmoney.category.service.CategoryService;
import fun.trackmoney.enums.TransactionType;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class TransactionService {

  private final TransactionRepository transactionRepository;
  private final TransactionMapper transactionMapper;
  private final AccountService accountService;
  private final CategoryService categoryService;

  public TransactionService(TransactionRepository transactionRepository,
                            TransactionMapper transactionMapper,
                            AccountService accountService,
                            CategoryService categoryService) {
    this.transactionRepository = transactionRepository;
    this.transactionMapper = transactionMapper;
    this.accountService = accountService;
    this.categoryService = categoryService;
  }

  @Transactional
  public TransactionResult createTransaction(CreateTransactionDTO transactionDTO, UserEntity currentUser) {
    AccountEntity account = currentUser.getAccount();

    if (account == null) {
      return new TransactionFailure(TransactionsError.ACCOUNT_NOT_FOUND);
    }

    CategoryEntity category = categoryService.findById(transactionDTO.categoryId());

    if (category == null) {
      return new TransactionFailure(TransactionsError.CATEGORY_NOT_FOUND);
    }

    TransactionEntity transactionEntity = transactionMapper.createTransactionToEntity(transactionDTO);

    transactionEntity.setAccount(account);
    transactionEntity.setCategory(category);

    var response = transactionMapper.toResponseDTO(transactionRepository.save(transactionEntity));

    boolean isCredit = TransactionType.INCOME.equals(transactionDTO.transactionType());
    accountService.updateAccountBalance(transactionDTO.amount(), account.getAccountId(), isCredit);

    return new TransactionSuccess(response);
  }

  public List<TransactionResponseDTO> findAllTransaction(UserEntity currentUser) {
    Integer accountId = currentUser.getAccount().getAccountId();
    return transactionMapper.toResponseDTOList(transactionRepository.findAllByAccountId(accountId));
  }

  public TransactionResponseDTO findById(Integer id, UserEntity currentUser) {
    TransactionEntity transaction = transactionRepository.findByIdAndAccount(id, currentUser.getAccount())
        .orElseThrow(() -> new TransactionNotFoundException("Transaction not found."));
    return transactionMapper.toResponseDTO(transaction);
  }

  @Transactional
  public TransactionResponseDTO update(Integer id, TransactionUpdateDTO dto, UserEntity currentUser) {
    AccountEntity account = currentUser.getAccount();
    TransactionEntity oldTransaction = transactionRepository.findByIdAndAccount(id, account)
        .orElseThrow(() -> new TransactionNotFoundException("Transaction not found."));

    if (oldTransaction.getTransactionType() == TransactionType.INCOME) {
      accountService.updateAccountBalance(oldTransaction.getAmount(), account.getAccountId(), false);
    } else {
      accountService.updateAccountBalance(oldTransaction.getAmount(), account.getAccountId(), true);
    }

    boolean newIsCredit = (dto.transactionType() == TransactionType.INCOME);
    accountService.updateAccountBalance(dto.amount(), account.getAccountId(), newIsCredit);

    oldTransaction.setTransactionName(dto.transactionName());
    oldTransaction.setAmount(dto.amount());
    oldTransaction.setDescription(dto.description());
    oldTransaction.setCategory(categoryService.findById(dto.categoryId()));
    oldTransaction.setTransactionType(dto.transactionType());
    oldTransaction.setTransactionDate(dto.transactionDate());

    return transactionMapper.toResponseDTO(transactionRepository.save(oldTransaction));
  }

  @Transactional
  public void delete(Integer id, UserEntity currentUser) {
    TransactionEntity transaction = transactionRepository.findById(id).orElse(null);

    if(transaction != null){
      boolean isCredit = !transaction.getTransactionType().equals(TransactionType.INCOME);
      Integer accountId = currentUser.getAccount().getAccountId();
      accountService.updateAccountBalance(transaction.getAmount(), accountId , isCredit);
    }

    transactionRepository.deleteByIdAndAccountId(id, currentUser.getAccount());
  }

  public BigDecimal getIncome(UUID userId) {
    Integer accountId = accountService.findAccountDefaultByUserId(userId).getAccountId();
    LocalDate today = LocalDate.now();
    int currentMonth = today.getMonthValue();
    int currentYear = today.getYear();

    return transactionRepository.findAllByAccountId(accountId)
        .stream()
        .filter(t -> TransactionType.INCOME.equals(t.getTransactionType()))
        .filter(t -> {
          LocalDate date = t.getTransactionDate().toLocalDate();
          return date.getMonthValue() == currentMonth && date.getYear() == currentYear;
        })
        .map(TransactionEntity::getAmount)
        .filter(Objects::nonNull)
        .reduce(BigDecimal.ZERO, BigDecimal::add);
  }

  public BigDecimal getExpense(UserEntity currentUser) {
    Integer accountId = currentUser.getAccount().getAccountId();
    LocalDate today = LocalDate.now();
    int currentMonth = today.getMonthValue();
    int currentYear = today.getYear();

    return transactionRepository.findAllByAccountId(accountId)
        .stream()
        .filter(t -> TransactionType.EXPENSE.equals(t.getTransactionType()))
        .filter(t -> {
          LocalDate date = t.getTransactionDate().toLocalDate();
          return date.getMonthValue() == currentMonth && date.getYear() == currentYear;
        })
        .map(TransactionEntity::getAmount)
        .filter(Objects::nonNull)
        .reduce(BigDecimal.ZERO, BigDecimal::add);
  }

  public Page<TransactionResponseDTO> getPaginatedTransactions(Pageable pageable,
                                                               UserEntity currentUser,
                                                               String name,
                                                               Long categoryId,
                                                               DateFilterEnum dateFilter
                                                               ) {
    Integer accountId = currentUser.getAccount().getAccountId();

    LocalDateTime startDate = null;
    LocalDateTime endDate = null;
    if(dateFilter != null){
      var dateRager = DateFilterUtil.getDateRange(dateFilter);
      startDate = dateRager.start();
      endDate = dateRager.end();
    }
    if(name == null){
      name = "";
    }
    return transactionRepository.findAllByFilters(accountId, name, categoryId, startDate, endDate, pageable)
        .map(transactionMapper::toResponseDTO);
  }

  public Map<CategoryEntity, List<TransactionResponseDTO>> getLast5TransactionsPerCategory(Integer accountId) {
    List<TransactionEntity> transactions =
        transactionRepository.findLast5TransactionsPerCategory(accountId);

    return transactions.stream()
        .collect(Collectors.groupingBy(
            TransactionEntity::getCategory,
            Collectors.mapping(
                transactionMapper::toResponseDTO,
                Collectors.toList()
            )
        ));
  }

  public List<TransactionEntity> getCurrentMonthTransactions() {
    LocalDateTime startDate = LocalDate.now().withDayOfMonth(1).atStartOfDay();
    LocalDateTime endDate = LocalDateTime.now();

    return transactionRepository.findAllBetweenDates(startDate, endDate);
  }
}
