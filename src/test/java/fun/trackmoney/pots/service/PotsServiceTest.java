package fun.trackmoney.pots.service;

import fun.trackmoney.account.entity.AccountEntity;
import fun.trackmoney.account.service.AccountService;
import fun.trackmoney.enums.TransactionType;
import fun.trackmoney.pots.dtos.CreatePotsDTO;
import fun.trackmoney.pots.dtos.MoneyRequest;
import fun.trackmoney.pots.dtos.PotsResponseDTO;
import fun.trackmoney.pots.dtos.internal.PotsFailure;
import fun.trackmoney.pots.dtos.internal.PotsSuccess;
import fun.trackmoney.pots.entity.PotsEntity;
import fun.trackmoney.pots.enums.ColorPick;
import fun.trackmoney.pots.enums.PotsErrorType;
import fun.trackmoney.pots.mapper.PotsMapper;
import fun.trackmoney.pots.repository.PotsRepository;
import fun.trackmoney.testutils.CreatePotsDTOFactory;
import fun.trackmoney.testutils.PotsEntityFactory;
import fun.trackmoney.testutils.PotsResponseDTOFactory;
import fun.trackmoney.testutils.UserEntityFactory;
import fun.trackmoney.user.entity.UserEntity;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PotsServiceTest {

  @InjectMocks
  private PotsService potsService;

  @Mock
  private PotsRepository potsRepository;

  @Mock
  private PotsMapper potsMapper;

  @Mock
  private AccountService accountService;

  @Test
  void findAllPots_shouldReturnMappedPotsList() {
    UserEntity currentUser = UserEntityFactory.defaultUser();
    AccountEntity account = currentUser.getAccount();
    PotsEntity pots1 = PotsEntityFactory.defaultPot();
    PotsEntity pots2 = PotsEntityFactory.vacationPot();

    List<PotsEntity> potsEntities = List.of(pots1, pots2);

    List<PotsResponseDTO> potsResponseDTOList = List.of(
        PotsResponseDTOFactory.defaultPotResponse(),
        PotsResponseDTOFactory.vacationPotResponse()
    );

    when(potsRepository.findAllByAccount(account)).thenReturn(potsEntities);
    when(potsMapper.listToResponse(potsEntities)).thenReturn(potsResponseDTOList);

    var result = potsService.findAllPots(currentUser);

    assertNotNull(result);
    assertEquals(2, result.size());
    assertEquals(potsResponseDTOList.get(0), result.get(0));
    assertEquals(potsResponseDTOList.get(1), result.get(1));
  }

  @Test
  void create_shouldReturnMappedPotsResponse() {
    UserEntity currentUser = UserEntityFactory.defaultUser();
    PotsEntity potsEntity = PotsEntityFactory.defaultPot();
    PotsResponseDTO potsResponseDTO = PotsResponseDTOFactory.defaultPotResponse();
    CreatePotsDTO createPotsDTO = CreatePotsDTOFactory.defaultCreatePot();

    when(potsMapper.toEntity(createPotsDTO)).thenReturn(potsEntity);
    when(potsMapper.toResponse(potsEntity)).thenReturn(potsResponseDTO);
    when(potsRepository.save(potsEntity)).thenReturn(potsEntity);

    var result = potsService.create(createPotsDTO, currentUser);

    assertNotNull(result);
    assertEquals(potsResponseDTO, result);
  }


  @Test
  void delete_shouldDeleteTransactionAndUpdateTheAccountBalance() {
    PotsEntity potsEntity = PotsEntityFactory.defaultPot();
    Long potsId = potsEntity.getPotId();
    UserEntity currentUser = UserEntityFactory.defaultUser();

    when(potsRepository.findByIdAndAccount(potsId, currentUser.getAccount())).thenReturn(Optional.of(potsEntity));
    when(accountService.updateAccountBalance(potsEntity.getCurrentAmount(), currentUser.getAccount().getAccountId(), true)).thenReturn(true);

    potsService.delete(potsId, currentUser);

    verify(potsRepository,times(1)).findByIdAndAccount(potsId, currentUser.getAccount());
    verify(potsRepository, times(1)).deleteByIdAccount(potsId, currentUser.getAccount());
    verify(accountService, times(1)).updateAccountBalance(potsEntity.getCurrentAmount(), currentUser.getAccount().getAccountId(), true);
  }

  @Test
  void delete_shouldDeleteTransactionAndNotUpdateTheAccountBalance() {
    PotsEntity potsEntity = PotsEntityFactory.withoutCurrentAmount();
    Long potsId = potsEntity.getPotId();
    UserEntity currentUser = UserEntityFactory.defaultUser();

    when(potsRepository.findByIdAndAccount(potsId, currentUser.getAccount())).thenReturn(Optional.of(potsEntity));

    potsService.delete(potsId, currentUser);

    verify(potsRepository,times(1)).findByIdAndAccount(potsId, currentUser.getAccount());
    verify(potsRepository, times(1)).deleteByIdAccount(potsId, currentUser.getAccount());
    verify(accountService, times(0)).updateAccountBalance(potsEntity.getCurrentAmount(), currentUser.getAccount().getAccountId(), true);
  }

  @Test
  void delete_shouldDoNothingWhenPotsIsNotFound() {
    UserEntity currentUser = UserEntityFactory.defaultUser();

    when(potsRepository.findByIdAndAccount(1l, currentUser.getAccount())).thenReturn(Optional.empty());

    potsService.delete(1l, currentUser);

    verify(potsRepository,times(1)).findByIdAndAccount(1l, currentUser.getAccount());
    verify(potsRepository, times(0)).deleteByIdAccount(1l, currentUser.getAccount());
    verify(accountService, times(0)).updateAccountBalance(any(),any(),any());
  }

  @Test
  void addMoney_shouldReturnNotFound_whenPotDoesNotExist() {
    Long potId = 1L;
    UserEntity currentUser = UserEntityFactory.defaultUser();
    MoneyRequest moneyRequest = new MoneyRequest(TransactionType.INCOME, BigDecimal.TEN);

    when(potsRepository.findByIdAndAccount(potId, currentUser.getAccount()))
        .thenReturn(Optional.empty());

    var result = potsService.addMoney(potId, moneyRequest, currentUser);

    assertTrue(result instanceof PotsFailure);
    PotsFailure failure = (PotsFailure) result;
    assertEquals(PotsErrorType.NOT_FOUND, failure.type());
    verify(potsRepository, times(0)).save(any());
  }

  @Test
  void addMoney_shouldFail_whenIncomeExceedsTargetAmount() {
    UserEntity currentUser = UserEntityFactory.defaultUser();
    PotsEntity pot = PotsEntityFactory.defaultPot();
    pot.setTargetAmount(new BigDecimal("100"));
    pot.setCurrentAmount(new BigDecimal("80"));

    MoneyRequest moneyRequest = new MoneyRequest(TransactionType.INCOME, new BigDecimal("30"));

    when(potsRepository.findByIdAndAccount(pot.getPotId(), currentUser.getAccount()))
        .thenReturn(Optional.of(pot));

    var result = potsService.addMoney(pot.getPotId(), moneyRequest, currentUser);

    assertTrue(result instanceof PotsFailure);
    PotsFailure failure = (PotsFailure) result;
    assertEquals(PotsErrorType.BAD_REQUEST, failure.type());
    assertEquals("Money is greater than target amount!", failure.message());
    verify(potsRepository, times(0)).save(any());
  }

  @Test
  void addMoney_shouldFail_whenExpenseExceedsCurrentAmount() {
    UserEntity currentUser = UserEntityFactory.defaultUser();
    PotsEntity pot = PotsEntityFactory.defaultPot();
    pot.setCurrentAmount(new BigDecimal("50"));

    MoneyRequest moneyRequest = new MoneyRequest(TransactionType.EXPENSE, new BigDecimal("60"));

    when(potsRepository.findByIdAndAccount(pot.getPotId(), currentUser.getAccount()))
        .thenReturn(Optional.of(pot));

    var result = potsService.addMoney(pot.getPotId(), moneyRequest, currentUser);

    assertTrue(result instanceof PotsFailure);
    PotsFailure failure = (PotsFailure) result;
    assertEquals(PotsErrorType.BAD_REQUEST, failure.type());
    assertEquals("Money is greater than current amount!", failure.message());
    verify(potsRepository, times(0)).save(any());
  }

  @Test
  void addMoney_shouldAddAmountSuccessfully_whenTypeIsIncome() {
    UserEntity currentUser = UserEntityFactory.defaultUser();
    PotsEntity pot = PotsEntityFactory.defaultPot();
    pot.setTargetAmount(new BigDecimal("100"));
    pot.setCurrentAmount(new BigDecimal("50"));

    MoneyRequest moneyRequest = new MoneyRequest(TransactionType.INCOME, new BigDecimal("20"));
    PotsResponseDTO responseDTO = PotsResponseDTOFactory.defaultPotResponse();

    when(potsRepository.findByIdAndAccount(pot.getPotId(), currentUser.getAccount()))
        .thenReturn(Optional.of(pot));
    when(potsRepository.save(pot)).thenAnswer(invocation -> invocation.getArgument(0)); // Retorna o próprio objeto salvo
    when(potsMapper.toResponse(any(PotsEntity.class))).thenReturn(responseDTO);

    var result = potsService.addMoney(pot.getPotId(), moneyRequest, currentUser);

    assertTrue(result instanceof PotsSuccess);

    assertEquals(new BigDecimal("70"), pot.getCurrentAmount());

    verify(potsRepository, times(1)).save(pot);

    verify(accountService, times(1))
        .updateAccountBalance(moneyRequest.amount(), currentUser.getAccount().getAccountId(), false);
  }

  @Test
  void addMoney_shouldSubtractAmountSuccessfully_whenTypeIsExpense() {
    UserEntity currentUser = UserEntityFactory.defaultUser();
    PotsEntity pot = PotsEntityFactory.defaultPot();
    pot.setCurrentAmount(new BigDecimal("50"));

    MoneyRequest moneyRequest = new MoneyRequest(TransactionType.EXPENSE, new BigDecimal("20"));
    PotsResponseDTO responseDTO = PotsResponseDTOFactory.defaultPotResponse();

    when(potsRepository.findByIdAndAccount(pot.getPotId(), currentUser.getAccount()))
        .thenReturn(Optional.of(pot));
    when(potsRepository.save(pot)).thenAnswer(invocation -> invocation.getArgument(0));
    when(potsMapper.toResponse(any(PotsEntity.class))).thenReturn(responseDTO);

    var result = potsService.addMoney(pot.getPotId(), moneyRequest, currentUser);

    assertTrue(result instanceof PotsSuccess);

    assertEquals(new BigDecimal("30"), pot.getCurrentAmount());

    verify(potsRepository, times(1)).save(pot);

    verify(accountService, times(1))
        .updateAccountBalance(moneyRequest.amount(), currentUser.getAccount().getAccountId(), true);
  }

  @Test
  void addMoney_shouldSucceed_whenIncomeIsGreaterThanCurrentBalance() {
    UserEntity currentUser = UserEntityFactory.defaultUser();
    PotsEntity pot = PotsEntityFactory.defaultPot();

    pot.setTargetAmount(new BigDecimal("1000"));
    pot.setCurrentAmount(new BigDecimal("10"));

    MoneyRequest moneyRequest = new MoneyRequest(TransactionType.INCOME, new BigDecimal("50"));
    PotsResponseDTO responseDTO = PotsResponseDTOFactory.defaultPotResponse();

    when(potsRepository.findByIdAndAccount(pot.getPotId(), currentUser.getAccount()))
        .thenReturn(Optional.of(pot));
    when(potsRepository.save(any())).thenAnswer(i -> i.getArgument(0));
    when(potsMapper.toResponse(any())).thenReturn(responseDTO);

    var result = potsService.addMoney(pot.getPotId(), moneyRequest, currentUser);

    assertTrue(result instanceof PotsSuccess);
    assertEquals(new BigDecimal("60"), pot.getCurrentAmount()); // 10 + 50
  }

  @Test
  void addMoney_shouldSucceed_whenExpenseIsGreaterThanRemainingSpread() {
    UserEntity currentUser = UserEntityFactory.defaultUser();
    PotsEntity pot = PotsEntityFactory.defaultPot();

    pot.setTargetAmount(new BigDecimal("100"));
    pot.setCurrentAmount(new BigDecimal("90"));

    MoneyRequest moneyRequest = new MoneyRequest(TransactionType.EXPENSE, new BigDecimal("50"));
    PotsResponseDTO responseDTO = PotsResponseDTOFactory.defaultPotResponse();

    when(potsRepository.findByIdAndAccount(pot.getPotId(), currentUser.getAccount()))
        .thenReturn(Optional.of(pot));
    when(potsRepository.save(any())).thenAnswer(i -> i.getArgument(0));
    when(potsMapper.toResponse(any())).thenReturn(responseDTO);

    var result = potsService.addMoney(pot.getPotId(), moneyRequest, currentUser);

    assertTrue(result instanceof PotsSuccess);
    assertEquals(new BigDecimal("40"), pot.getCurrentAmount());
  }

  @Test
  void update_shouldUpdatePotsSuccessfully_whenDataIsValid() {
    UserEntity currentUser = UserEntityFactory.defaultUser();
    PotsEntity existingPot = PotsEntityFactory.defaultPot();
    existingPot.setCurrentAmount(new BigDecimal("50"));
    existingPot.setTargetAmount(new BigDecimal("100"));
    Long potId = existingPot.getPotId();

    CreatePotsDTO updateDTO = new CreatePotsDTO(
        "New Name",
        new BigDecimal("200"),
        ColorPick.DARK_BLUE
    );

    PotsResponseDTO responseDTO = PotsResponseDTOFactory.defaultPotResponse();

    when(potsRepository.findByIdAndAccount(potId, currentUser.getAccount()))
        .thenReturn(Optional.of(existingPot));

    when(potsRepository.save(any(PotsEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));
    when(potsMapper.toResponse(any(PotsEntity.class))).thenReturn(responseDTO);

    var result = potsService.update(potId, updateDTO, currentUser);

    assertTrue(result instanceof PotsSuccess);

    assertEquals("New Name", existingPot.getName());
    assertEquals(ColorPick.DARK_BLUE, existingPot.getColor());
    assertEquals(new BigDecimal("200"), existingPot.getTargetAmount());

    verify(potsRepository, times(1)).save(existingPot);
  }

  @Test
  void update_shouldReturnNotFound_whenPotDoesNotExist() {
    UserEntity currentUser = UserEntityFactory.defaultUser();
    Long potId = 99L;
    CreatePotsDTO updateDTO = CreatePotsDTOFactory.defaultCreatePot();

    when(potsRepository.findByIdAndAccount(potId, currentUser.getAccount()))
        .thenReturn(Optional.empty());

    var result = potsService.update(potId, updateDTO, currentUser);

    assertTrue(result instanceof PotsFailure);
    PotsFailure failure = (PotsFailure) result;
    assertEquals(PotsErrorType.NOT_FOUND, failure.type());
    assertEquals("Pots not found!", failure.message());

    verify(potsRepository, times(0)).save(any());
  }

  @Test
  void update_shouldFail_whenNewTargetAmountIsLessThanCurrentAmount() {
    UserEntity currentUser = UserEntityFactory.defaultUser();

    PotsEntity existingPot = PotsEntityFactory.defaultPot();
    existingPot.setCurrentAmount(new BigDecimal("100"));
    Long potId = existingPot.getPotId();

    CreatePotsDTO updateDTO = new CreatePotsDTO(
        "Update Name",
        new BigDecimal("50"),
        ColorPick.DARK_BLUE
    );

    when(potsRepository.findByIdAndAccount(potId, currentUser.getAccount()))
        .thenReturn(Optional.of(existingPot));

    var result = potsService.update(potId, updateDTO, currentUser);

    assertTrue(result instanceof PotsFailure);
    PotsFailure failure = (PotsFailure) result;

    assertEquals(PotsErrorType.BAD_REQUEST, failure.type());
    assertEquals("Target amount cannot be less than current amount!", failure.message());

    verify(potsRepository, times(0)).save(any());
  }
}
