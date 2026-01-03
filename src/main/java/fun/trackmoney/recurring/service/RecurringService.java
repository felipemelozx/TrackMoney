package fun.trackmoney.recurring.service;

import fun.trackmoney.account.entity.AccountEntity;
import fun.trackmoney.category.entity.CategoryEntity;
import fun.trackmoney.category.service.CategoryService;
import fun.trackmoney.enums.Frequency;
import fun.trackmoney.enums.TransactionType;
import fun.trackmoney.recurring.dtos.CreateRecurringRequest;
import fun.trackmoney.recurring.dtos.RecurringResponse;
import fun.trackmoney.recurring.entity.RecurringEntity;
import fun.trackmoney.recurring.mapper.RecurringMapper;
import fun.trackmoney.recurring.repository.RecurringRepository;
import fun.trackmoney.transaction.dto.BillResponseDTO;
import fun.trackmoney.transaction.dto.CreateTransactionDTO;
import fun.trackmoney.transaction.dto.internal.TransactionFailure;
import fun.trackmoney.transaction.dto.internal.TransactionResult;
import fun.trackmoney.transaction.service.TransactionService;
import fun.trackmoney.user.entity.UserEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class RecurringService {

  private final RecurringRepository recurringRepository;
  private final RecurringMapper recurringMapper;
  private final CategoryService categoryService;
  private final TransactionService transactionService;


  public RecurringService(RecurringRepository recurringRepository,
                          RecurringMapper recurringMapper,
                          CategoryService categoryService,
                          TransactionService transactionService) {
    this.recurringRepository = recurringRepository;
    this.recurringMapper = recurringMapper;
    this.categoryService = categoryService;
    this.transactionService = transactionService;
  }

  @Transactional
  public RecurringResponse create(CreateRecurringRequest request, UserEntity currentUser) {
    AccountEntity account = currentUser.getAccount();
    RecurringEntity recurring = recurringMapper.toEntity(request);

    CategoryEntity category = categoryService.findById(request.categoryId());

    if (category == null) {
      return null;
    }

    LocalDateTime nextDate = calculateNextRun(request.frequency(), request.recurrenceDay());
    recurring.setCategory(category)
        .setAccount(account)
        .setNextDate(nextDate);

    return recurringMapper.toResponse(recurringRepository.save(recurring));
  }


  public RecurringResponse update(Long id, CreateRecurringRequest request, UserEntity currentUser) {
    CategoryEntity category = categoryService.findById(request.categoryId());
    
    if(category == null) {
      return null;
    }
    Integer accountId =  currentUser.getAccount().getAccountId();
    Optional<RecurringEntity> optionalRecurring = recurringRepository.findByIdAndAccount(id, accountId);
    
    if(optionalRecurring.isEmpty()) {
      return null;
    }
    
    RecurringEntity recurring = optionalRecurring.get();
    LocalDateTime nextDate = calculateNextRun(request.frequency(), request.recurrenceDay());
    recurring.setCategory(category)
        .setTransactionName(request.transactionName())
        .setNextDate(nextDate)
        .setDescription(request.description())
        .setTransactionType(request.transactionType())
        .setAmount(request.amount())
        .setFrequency(request.frequency());

    return recurringMapper.toResponse(recurringRepository.save(recurring));
  }

  protected LocalDateTime calculateNextRun(Frequency frequency, LocalDateTime lastDate) {
    return switch (frequency) {
      case DAILY -> lastDate.plusDays(1);
      case WEEKLY -> lastDate.with(TemporalAdjusters.next(DayOfWeek.MONDAY));
      case MONTHLY -> lastDate.plusMonths(1);
      case YEARLY -> lastDate.plusYears(1);
    };
  }

  @Scheduled(cron = "0 0 0 * * *")
  @Transactional
  public void recurringTransactions() {

    List<RecurringEntity> recurrences =
        recurringRepository.findByNextDateBefore(LocalDateTime.now());

    for (RecurringEntity recurring : recurrences) {
      processRecurring(recurring);
    }
  }


  protected void processRecurring(RecurringEntity recurring) {
    CreateTransactionDTO dto = new CreateTransactionDTO(
        recurring.getTransactionName(),
        recurring.getCategory().getCategoryId(),
        recurring.getTransactionType(),
        recurring.getAmount(),
        recurring.getDescription(),
        recurring.getNextDate()
    );

    TransactionResult result =
        transactionService.createTransaction(dto, recurring.getAccount().getUser());

    if (result instanceof TransactionFailure failure) {
      throw new IllegalStateException("Failed to create transaction: " + failure.error());
    }

    LocalDateTime next = calculateNextRun(
        recurring.getFrequency(), recurring.getNextDate());

    recurring.setLastDate(recurring.getNextDate());
    recurring.setNextDate(next);
    recurringRepository.save(recurring);
  }


  public List<RecurringResponse> findAll(UserEntity currentUser) {
    Integer accountId = currentUser.getAccount().getAccountId();
    return recurringMapper.toResponse(recurringRepository.findAllByAccountId(accountId));
  }

  public BillResponseDTO getBill(UserEntity currentUser) {
    Integer accountId = currentUser.getAccount().getAccountId();
    var result = recurringRepository.findAllByAccountId(accountId);

    BigDecimal totalBillsBeforeToday = result.stream()
        .filter(recurring ->
            recurring.getNextDate().toLocalDate().isBefore(LocalDate.now())
                && recurring.getTransactionType().equals(TransactionType.EXPENSE)
        )
        .map(RecurringEntity::getAmount)
        .filter(Objects::nonNull)
        .reduce(BigDecimal.ZERO, BigDecimal::add);


    BigDecimal totalUpComing = result.stream()
        .filter(recurring ->
            recurring.getNextDate().toLocalDate().isAfter(LocalDate.now())
                && recurring.getTransactionType().equals(TransactionType.EXPENSE)
        )
        .map(RecurringEntity::getAmount)
        .filter(Objects::nonNull)
        .reduce(BigDecimal.ZERO, BigDecimal::add);

    BigDecimal totalBueSoon = result.stream()
        .filter(recurring -> {
          LocalDate date = recurring.getNextDate().toLocalDate();
          return !date.isBefore(LocalDate.now())
              && !date.isAfter(LocalDate.now().plusDays(7))
              && recurring.getTransactionType().equals(TransactionType.EXPENSE);
        })
        .map(RecurringEntity::getAmount)
        .filter(Objects::nonNull)
        .reduce(BigDecimal.ZERO, BigDecimal::add);

    return new BillResponseDTO(totalBillsBeforeToday, totalUpComing, totalBueSoon);
  }

  @Transactional
  public void delete(Long id, UserEntity currentUser) {
    recurringRepository.deleteByIdAndAccountId(id, currentUser.getAccount().getAccountId());
  }
}
