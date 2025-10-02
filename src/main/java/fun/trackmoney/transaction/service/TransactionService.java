package fun.trackmoney.transaction.service;

import fun.trackmoney.account.entity.AccountEntity;
import fun.trackmoney.account.mapper.AccountMapper;
import fun.trackmoney.account.service.AccountService;
import fun.trackmoney.category.entity.CategoryEntity;
import fun.trackmoney.category.service.CategoryService;
import fun.trackmoney.enums.TransactionType;
import fun.trackmoney.transaction.dto.BillResponseDTO;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
public class TransactionService {

  private final TransactionRepository transactionRepository;
  private final TransactionMapper transactionMapper;
  private final AccountService accountService;
  private final AccountMapper accountMapper;
  private final CategoryService categoryService;

  public TransactionService(TransactionRepository transactionRepository,
                            TransactionMapper transactionMapper,
                            AccountService accountService,
                            AccountMapper accountMapper,
                            CategoryService categoryService) {
    this.transactionRepository = transactionRepository;
    this.transactionMapper = transactionMapper;
    this.accountService = accountService;
    this.accountMapper = accountMapper;
    this.categoryService = categoryService;
  }

  public TransactionResult createTransaction(CreateTransactionDTO transactionDTO, UUID userId) {
    AccountEntity optionalAccount = accountService.findAccountDefaultByUserId(userId);

    if(optionalAccount == null) {
      return new TransactionFailure(TransactionsError.ACCOUNT_NOT_FOUND);
    }

    CategoryEntity category = categoryService.findById(transactionDTO.categoryId());

    if(category == null) {
      return new TransactionFailure(TransactionsError.CATEGORY_NOT_FOUND);
    }

    TransactionEntity transactionEntity = transactionMapper.createTransactionToEntity(transactionDTO);

    transactionEntity.setAccount(optionalAccount);
    transactionEntity.setCategory(category);

    var response = transactionMapper.toResponseDTO(transactionRepository.save(transactionEntity));

    boolean isCredit = TransactionType.INCOME.equals(transactionDTO.transactionType());
    var wasUpdate = accountService.updateAccountBalance(transactionDTO.amount(), optionalAccount.getAccountId(), isCredit);
    if(!wasUpdate){
      return new TransactionFailure(TransactionsError.ACCOUNT_NOT_FOUND);
    }
    return new TransactionSuccess(response);
  }

  public List<TransactionResponseDTO> findAllTransaction() {
     return transactionMapper.toResponseDTOList(transactionRepository.findAll());
  }

  public TransactionResponseDTO findById(Integer id) {
    return transactionMapper.toResponseDTO(transactionRepository.findById(id)
        .orElseThrow(() -> new TransactionNotFoundException("Transaction not found.")));
  }

  public TransactionResponseDTO update(Integer id, TransactionUpdateDTO dto) {
    TransactionEntity transaction = transactionRepository.findById(id)
        .orElseThrow(() -> new TransactionNotFoundException("Transaction not found."));

    transaction.setAmount(dto.amount());
    transaction.setDescription(dto.description());
    transaction.setCategory(categoryService.findById(dto.categoryId()));
    transaction.setAccount(accountMapper.accountResponseToEntity(accountService.findAccountById(dto.accountId())));

    return transactionMapper.toResponseDTO(transactionRepository.save(transaction));
  }

  public void delete(Integer id) {
    transactionRepository.deleteById(id);
  }

  public BigDecimal getIncome(Integer accountId) {
    return transactionRepository.findAllByAccountId(accountId)
        .stream()
        .filter(t -> TransactionType.INCOME.equals(t.getTransactionType()))
        .map(TransactionEntity::getAmount)
        .filter(Objects::nonNull)
        .reduce(BigDecimal.ZERO, BigDecimal::add);
  }

  public BigDecimal getExpense(Integer accountId) {
    return transactionRepository.findAllByAccountId(accountId)
        .stream()
        .filter(t -> TransactionType.EXPENSE.equals(t.getTransactionType()))
        .map(TransactionEntity::getAmount)
        .filter(Objects::nonNull)
        .reduce(BigDecimal.ZERO, BigDecimal::add);
  }

  public Page<TransactionResponseDTO> getPaginatedTransactions(Pageable pageable) {
    return transactionRepository.findAll(pageable)
        .map(transactionMapper::toResponseDTO);
  }

  public BillResponseDTO getBill(Integer id) {
    var result = transactionRepository.findAllByAccountId(id);

    BigDecimal totalBillsBeforeToday = result.stream()
        .filter(transaction ->
            transaction.getCategory().getName().equalsIgnoreCase("bill") &&
                transaction.getTransactionDate().toLocalDateTime().toLocalDate().isBefore(LocalDate.now())
        )
        .map(TransactionEntity::getAmount)
        .filter(Objects::nonNull)
        .reduce(BigDecimal.ZERO, BigDecimal::add);


    BigDecimal totalUpComing = result.stream()
        .filter(transaction ->
            transaction.getCategory().getName().equalsIgnoreCase("bill") &&
                transaction.getTransactionDate().toLocalDateTime().toLocalDate().isAfter(LocalDate.now())
        )
        .map(TransactionEntity::getAmount)
        .filter(Objects::nonNull)
        .reduce(BigDecimal.ZERO, BigDecimal::add);

    BigDecimal totalBueSoon = result.stream()
        .filter(transaction -> {
          LocalDate date = transaction.getTransactionDate().toLocalDateTime().toLocalDate();
          return transaction.getCategory().getName().equalsIgnoreCase("bill")
              && !date.isBefore(LocalDate.now())
              && !date.isAfter(LocalDate.now().plusDays(7));
        })

        .map(TransactionEntity::getAmount)
        .filter(Objects::nonNull)
        .reduce(BigDecimal.ZERO, BigDecimal::add);


    return new BillResponseDTO(totalBillsBeforeToday, totalUpComing, totalBueSoon);
  }
}
