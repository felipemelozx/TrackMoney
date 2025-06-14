package fun.trackmoney.pots.service;

import fun.trackmoney.account.entity.AccountEntity;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

class PotsServiceTest {

  @InjectMocks
  private PotsService potsService;

  @Mock
  private PotsRepository potsRepository;

  @Mock
  private PotsMapper potsMapper;

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
}
