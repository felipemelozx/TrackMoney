package fun.trackmoney.service;

import fun.trackmoney.enums.TransactionType;
import fun.trackmoney.dto.pots.CreatePotsDTO;
import fun.trackmoney.dto.pots.MoneyRequest;
import fun.trackmoney.dto.pots.PotsResponseDTO;
import fun.trackmoney.common.result.ErrorCode;
import fun.trackmoney.common.result.Result;
import fun.trackmoney.entity.PotsEntity;
import fun.trackmoney.mapper.PotsMapper;
import fun.trackmoney.repository.PotsRepository;
import fun.trackmoney.entity.UserEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
public class PotsService {

  private final PotsRepository potsRepository;
  private final PotsMapper potsMapper;
  private final AccountService accountService;

  public PotsService(PotsRepository potsRepository, PotsMapper potsMapper, AccountService accountService) {
    this.potsRepository = potsRepository;
    this.potsMapper = potsMapper;
    this.accountService = accountService;
  }

  @Transactional
  public PotsResponseDTO create(CreatePotsDTO dto, UserEntity currentUser) {
    var entity = potsMapper.toEntity(dto);
    var account = currentUser.getAccount();
    entity.setAccount(account);
    var potsResponse = potsRepository.save(entity);
    return potsMapper.toResponse(potsResponse);
  }

  public List<PotsResponseDTO> findAllPots(UserEntity currentUser) {
    return potsMapper.listToResponse(potsRepository.findAllByAccount(currentUser.getAccount()));
  }

  @Transactional
  public Result<PotsResponseDTO> addMoney(Long id, MoneyRequest money, UserEntity currentUser) {
    Optional<PotsEntity> pot = potsRepository.findByIdAndAccount(id, currentUser.getAccount());

    if(pot.isEmpty()) {
      return Result.err(ErrorCode.POT_NOT_FOUND, "id", "Pots not found!");
    }

    BigDecimal spread = pot.get().getTargetAmount().subtract(pot.get().getCurrentAmount());
    boolean exceedsTargetAmount = money.amount().compareTo(spread) > 0;
    boolean exceedsCurrentBalance = money.amount().compareTo(pot.get().getCurrentAmount()) > 0;
    boolean isIncome = money.type().equals(TransactionType.INCOME);

    if (isIncome && exceedsTargetAmount) {
      return Result.err(ErrorCode.POT_INCOME_EXCEEDS_TARGET, "Money", null);
    }

    if (!isIncome && exceedsCurrentBalance) {
      return Result.err(ErrorCode.POT_EXPENSE_EXCEEDS_BALANCE, "Money", null);
    }

    BigDecimal newValue;
    if (money.type().equals(TransactionType.EXPENSE)) {
      newValue = pot.get().getCurrentAmount().subtract(money.amount());
    } else {
      newValue = pot.get().getCurrentAmount().add(money.amount());
    }

    pot.get().setCurrentAmount(newValue);

    var potUpdated = potsRepository.save(pot.get());
    accountService.updateAccountBalance(money.amount(), currentUser.getAccount().getAccountId(), !isIncome);
    return Result.ok(potsMapper.toResponse(potUpdated));
  }

  @Transactional
  public void delete(Long id, UserEntity currentUser) {
    Optional<PotsEntity> optionalPots = potsRepository.findByIdAndAccount(id, currentUser.getAccount());

    if(optionalPots.isEmpty()){
      return;
    }
    PotsEntity pot = optionalPots.get();

    potsRepository.deleteByIdAccount(id, currentUser.getAccount());
    boolean isCredit = true;
    Integer accountId = currentUser.getAccount().getAccountId();
    if(!(pot.getCurrentAmount().equals(BigDecimal.ZERO))) {
      accountService.updateAccountBalance(pot.getCurrentAmount(), accountId, isCredit);
    }
  }

  @Transactional
  public Result<PotsResponseDTO> update(Long id, CreatePotsDTO dto, UserEntity currentUser) {
    Optional<PotsEntity> optionalPots = potsRepository.findByIdAndAccount(id, currentUser.getAccount());

    if(optionalPots.isEmpty()) {
      return Result.err(ErrorCode.POT_NOT_FOUND, "id", "Pots not found!");
    }

    PotsEntity pot = optionalPots.get();

    if(pot.getCurrentAmount().compareTo(dto.targetAmount()) > 0){
      String message = "Target amount cannot be less than current amount!";
      return Result.err(ErrorCode.POT_TARGET_BELOW_CURRENT, "targetAmount", message);
    }

    pot.setTargetAmount(dto.targetAmount())
        .setName(dto.name())
        .setColor(dto.color());

    PotsResponseDTO responseDTO = potsMapper.toResponse(potsRepository.save(pot));
    return Result.ok(responseDTO);
  }
}
