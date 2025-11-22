package fun.trackmoney.pots.service;

import fun.trackmoney.account.entity.AccountEntity;
import fun.trackmoney.account.service.AccountService;
import fun.trackmoney.pots.dtos.CreatePotsDTO;
import fun.trackmoney.pots.dtos.PotsResponseDTO;
import fun.trackmoney.pots.entity.PotsEntity;
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

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
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
}
