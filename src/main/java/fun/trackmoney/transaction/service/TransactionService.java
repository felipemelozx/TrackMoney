package fun.trackmoney.transaction.service;

import fun.trackmoney.account.entity.AccountEntity;
import fun.trackmoney.account.mapper.AccountMapper;
import fun.trackmoney.account.service.AccountService;
import fun.trackmoney.category.entity.CategoryEntity;
import fun.trackmoney.category.service.CategoryService;
import fun.trackmoney.transaction.dto.CreateTransactionDTO;
import fun.trackmoney.transaction.dto.TransactionResponseDTO;
import fun.trackmoney.transaction.dto.TransactionUpdateDTO;
import fun.trackmoney.transaction.entity.TransactionEntity;
import fun.trackmoney.transaction.exception.TransactionNotFoundException;
import fun.trackmoney.transaction.mapper.TransactionMapper;
import fun.trackmoney.transaction.repository.TransactionRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TransactionService {

  private final TransactionRepository transactionRepository;
  private final TransactionMapper transactionMapper;
  private final AccountService accountService;
  private final AccountMapper accountMapper;
  private final CategoryService categoryService;

  public TransactionService(TransactionRepository transactionRepository, TransactionMapper transactionMapper,
                            AccountService accountService, AccountMapper accountMapper,
                            CategoryService categoryService) {
    this.transactionRepository = transactionRepository;
    this.transactionMapper = transactionMapper;
    this.accountService = accountService;
    this.accountMapper = accountMapper;
    this.categoryService = categoryService;
  }

  public TransactionResponseDTO createTransaction(CreateTransactionDTO transactionDTO) {
    AccountEntity account = accountMapper.accountResponseToEntity(
        accountService.findAccountById(transactionDTO.accountId()));
    CategoryEntity category = categoryService.findById(transactionDTO.categoryId());
    TransactionEntity transactionEntity = transactionMapper.createTransactionToEntity(transactionDTO);

    transactionEntity.setAccount(account);
    transactionEntity.setCategory(category);

    return transactionMapper.toResponseDTO(transactionRepository.save(transactionEntity));
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
}
