package fun.trackmoney.pots.service;

import fun.trackmoney.account.entity.AccountEntity;
import fun.trackmoney.account.exception.AccountNotFoundException;
import fun.trackmoney.account.repository.AccountRepository;
import fun.trackmoney.pots.dtos.CreatePotsDTO;
import fun.trackmoney.pots.dtos.PotsResponseDTO;
import fun.trackmoney.pots.entity.PotsEntity;
import fun.trackmoney.pots.mapper.PotsMapper;
import fun.trackmoney.pots.repository.PotsRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

class PotsServiceTest {

  @InjectMocks
  private PotsService potsService;

  @Mock
  private PotsRepository potsRepository;

  @Mock
  private PotsMapper potsMapper;

  @Mock
  private AccountRepository accountRepository;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  void findAllPots_shouldReturnMappedPotsList() {
    Integer accountId = 1;
    PotsEntity pots1 = new PotsEntity(1L,"test name2", "Test Pot1", 100L, 1001L, new AccountEntity());
    PotsEntity pots2 = new PotsEntity(2L,"test name2", "Test Pot2", 100L, 1002L, new AccountEntity());

    List<PotsEntity> potsEntities = List.of(pots1, pots2);

    List<PotsResponseDTO> potsResponseDTOList = List.of(
        new PotsResponseDTO(1L, "test name2", "Test Pot1", 100L, 1001L),
        new PotsResponseDTO(2L, "test name2", "Test Pot2", 100L, 1002L)
    );

    when(potsRepository.findAllPotsByAccountId(accountId)).thenReturn(potsEntities);
    when(potsMapper.listToResponse(potsEntities)).thenReturn(potsResponseDTOList);

    var result = potsService.findAllPots(accountId);

    assertNotNull(result);
    assertEquals(2, result.size());
    assertEquals(1L, result.get(0).potId());
  }

  @Test
  void create_shouldReturnMappedPotsResponse() {
    var account = new AccountEntity();
    account.setAccountId(1);
    PotsEntity potsEntity = new PotsEntity(1L, "test name", "Test Pot", 100L, 100L, account);
    PotsResponseDTO potsResponseDTO = new PotsResponseDTO(1L, "test name", "Test Pot", 100L, 100L);
    CreatePotsDTO createPotsDTO = new CreatePotsDTO("test name", "Test Pot", 1, 100L, 100L);

    when(potsMapper.toEntity(createPotsDTO)).thenReturn(potsEntity);
    when(potsMapper.toResponse(potsEntity)).thenReturn(potsResponseDTO);
    when(potsRepository.save(potsEntity)).thenReturn(potsEntity);
    when(accountRepository.findById(createPotsDTO.accountId())).thenReturn(Optional.of(account));

    var result = potsService.create(createPotsDTO);

    assertNotNull(result);
    assertEquals("Test Pot", result.description());
    assertEquals("test name", result.name());
    assertEquals(100L, result.targetAmount());
    assertEquals(100L, result.currentAmount());
  }

  @Test
  void create_shouldThrowAccountNotFoundException_whenAccountDoesNotExist() {
    var account = new AccountEntity();
    account.setAccountId(1);
    PotsEntity potsEntity = new PotsEntity(1L, "test name", "Test Pot", 100L, 100L, account);
    PotsResponseDTO potsResponseDTO = new PotsResponseDTO(1L, "test name", "Test Pot", 100L, 100L);
    CreatePotsDTO createPotsDTO = new CreatePotsDTO("test name", "Test Pot", 1, 100L, 100L);

    when(potsMapper.toEntity(createPotsDTO)).thenReturn(potsEntity);
    when(potsMapper.toResponse(potsEntity)).thenReturn(potsResponseDTO);
    when(potsRepository.save(potsEntity)).thenReturn(potsEntity);
    when(accountRepository.findById(createPotsDTO.accountId())).thenReturn(Optional.empty());

    assertThrows(AccountNotFoundException.class,() -> potsService.create(createPotsDTO));
  }
}
