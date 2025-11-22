package fun.trackmoney.pots.service;

import fun.trackmoney.account.service.AccountService;
import fun.trackmoney.enums.TransactionType;
import fun.trackmoney.pots.dtos.CreatePotsDTO;
import fun.trackmoney.pots.dtos.MoneyRequest;
import fun.trackmoney.pots.dtos.PotsResponseDTO;
import fun.trackmoney.pots.dtos.internal.PotsFailure;
import fun.trackmoney.pots.dtos.internal.PotsResult;
import fun.trackmoney.pots.dtos.internal.PotsSuccess;
import fun.trackmoney.pots.entity.PotsEntity;
import fun.trackmoney.pots.enums.PotsErrorType;
import fun.trackmoney.pots.mapper.PotsMapper;
import fun.trackmoney.pots.repository.PotsRepository;
import fun.trackmoney.user.entity.UserEntity;
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
  public PotsResult addMoney(Long id, MoneyRequest money, UserEntity currentUser) {
    Optional<PotsEntity> pot = potsRepository.findByIdAndAccount(id, currentUser.getAccount());

    if(pot.isEmpty()) {
      return new PotsFailure(PotsErrorType.NOT_FOUND,"id", "Pots not found!");
    }

    BigDecimal spreed = pot.get().getTargetAmount().subtract(pot.get().getCurrentAmount());
    boolean isAvailableToAddMoney = !(money.amount().intValue() <= spreed.intValue());
    boolean isAvailableToWithdrawnMoney = !(money.amount().intValue() <= pot.get().getCurrentAmount().intValue());
    boolean isIncome = money.type().equals(TransactionType.INCOME);

    if(isAvailableToAddMoney && isIncome) {
      return new PotsFailure(PotsErrorType.BAD_REQUEST,"Money", "Money is greater than target amount!");
    }

    if(isAvailableToWithdrawnMoney && !isIncome) {
      return new PotsFailure(PotsErrorType.BAD_REQUEST,"Money", "Money is greater than current amount!");
    }

    BigDecimal newValue;
   if(money.type().equals(TransactionType.EXPENSE)){
     newValue = pot.get().getCurrentAmount().subtract(money.amount());
     pot.get().setCurrentAmount(newValue);
   } else {
     newValue = pot.get().getCurrentAmount().add(money.amount());
     pot.get().setCurrentAmount(newValue);
   }

    var potUpdated = potsRepository.save(pot.get());
    accountService.updateAccountBalance(money.amount(), currentUser.getAccount().getAccountId(), !isIncome);
    return new PotsSuccess(potsMapper.toResponse(potUpdated));
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
}
