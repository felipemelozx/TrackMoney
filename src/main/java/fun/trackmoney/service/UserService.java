package fun.trackmoney.service;

import fun.trackmoney.entity.AccountEntity;
import fun.trackmoney.dto.auth.internal.AuthError;
import fun.trackmoney.dto.auth.internal.register.UserRegisterFailure;
import fun.trackmoney.dto.auth.internal.register.UserRegisterResult;
import fun.trackmoney.dto.auth.internal.register.UserRegisterSuccess;
import fun.trackmoney.dto.user.UserRequestDTO;
import fun.trackmoney.dto.user.UserResponseDTO;
import fun.trackmoney.entity.UserEntity;
import fun.trackmoney.mapper.UserMapper;
import fun.trackmoney.repository.AccountRepository;
import fun.trackmoney.repository.BudgetHistoryRepository;
import fun.trackmoney.repository.BudgetsRepository;
import fun.trackmoney.repository.PotsRepository;
import fun.trackmoney.repository.RecurringRepository;
import fun.trackmoney.repository.TransactionRepository;
import fun.trackmoney.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserService {

  private final UserRepository userRepository;
  private final UserMapper userMapper;
  private final PasswordEncoder encoder;
  private final AccountRepository accountRepository;
  private final TransactionRepository transactionRepository;
  private final PotsRepository potsRepository;
  private final BudgetsRepository budgetsRepository;
  private final BudgetHistoryRepository budgetHistoryRepository;
  private final RecurringRepository recurringRepository;

  public UserService(UserRepository userRepository,
                     UserMapper userMapper,
                     PasswordEncoder encoder,
                     AccountRepository accountRepository,
                     TransactionRepository transactionRepository,
                     PotsRepository potsRepository,
                     BudgetsRepository budgetsRepository,
                     BudgetHistoryRepository budgetHistoryRepository,
                     RecurringRepository recurringRepository) {
    this.userRepository = userRepository;
    this.userMapper = userMapper;
    this.encoder = encoder;
    this.accountRepository = accountRepository;
    this.transactionRepository = transactionRepository;
    this.potsRepository = potsRepository;
    this.budgetsRepository = budgetsRepository;
    this.budgetHistoryRepository = budgetHistoryRepository;
    this.recurringRepository = recurringRepository;
  }

  @Transactional
  public UserRegisterResult register(UserRequestDTO userRequestDTO) {
    Optional<UserEntity> userExist= findUserByEmail(userRequestDTO.email());
    if(userExist.isPresent()) {
      return new UserRegisterFailure(AuthError.EMAIL_ALREADY_EXISTS);
    }

    UserEntity user = userMapper.userRequestDTOToEntity(userRequestDTO);
    user.setPassword(encoder.encode(user.getPassword()));

    AccountEntity account = new AccountEntity()
        .setName("Default Account")
        .setBalance(BigDecimal.ZERO)
        .setUser(user);

    user.setAccount(account);
    UserEntity savedUser = userRepository.save(user);

    UserResponseDTO userResponseDTO = userMapper.userEntityToUserResponseDto(savedUser);
    return new UserRegisterSuccess(userResponseDTO);
  }

  public Boolean activateUser(String email) {
    UserEntity user = findUserByEmail(email).orElse(null);

    if(user == null) {
      return false;
    }

    user.activate();
    userRepository.save(user);
    return true;
  }

  public Optional<UserEntity> findUserByEmail(String email) {
    return userRepository.findByEmail(email);
  }

  public Optional<UserEntity> findUserById(UUID userId) {
    return userRepository.findById(userId);
  }

  public void update(UserEntity user) {
    userRepository.save(user);
  }

  @Transactional
  public boolean changePassword(UserEntity currentUser, String currentPassword, String newPassword) {
    Optional<UserEntity> userExist = userRepository.findById(currentUser.getUserId());

    if (userExist.isEmpty()) {
      return false;
    }

    UserEntity user = userExist.get();
    if (!encoder.matches(currentPassword, user.getPassword())) {
      return false;
    }

    user.setPassword(encoder.encode(newPassword));
    userRepository.save(user);
    return true;
  }

  @Transactional
  public boolean deleteAccount(UserEntity currentUser, String password) {
    Optional<UserEntity> userExist = userRepository.findById(currentUser.getUserId());

    if (userExist.isEmpty()) {
      return false;
    }

    UserEntity user = userExist.get();
    if (!encoder.matches(password, user.getPassword())) {
      return false;
    }

    AccountEntity account = user.getAccount();
    if (account == null) {
      return false;
    }
    Integer accountId = account.getAccountId();
    accountRepository.deleteLegacyTransfersByAccountId(accountId);
    accountRepository.deleteLegacyReportsByAccountId(accountId);
    budgetHistoryRepository.deleteAllByAccountAccountId(accountId);
    budgetsRepository.deleteAllByAccountAccountId(accountId);
    potsRepository.deleteAllByAccount(account);
    recurringRepository.deleteAllByAccountId(accountId);
    transactionRepository.deleteAllByAccount(account);

    userRepository.clearAccountReference(user.getUserId());
    accountRepository.deleteByAccountIdDirect(accountId);
    userRepository.deleteByUserIdDirect(user.getUserId());
    return true;
  }
}
