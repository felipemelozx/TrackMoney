package fun.trackmoney.pots.controller;

import fun.trackmoney.pots.dtos.CreatePotsDTO;
import fun.trackmoney.pots.dtos.PotsResponseDTO;
import fun.trackmoney.pots.dtos.internal.PotsResult;
import fun.trackmoney.pots.dtos.internal.PotsSuccess;
import fun.trackmoney.pots.service.PotsService;
import fun.trackmoney.testutils.UserEntityFactory;
import fun.trackmoney.user.entity.UserEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

class PotsControllerTest {

  @InjectMocks
  private PotsController potsController;

  @Mock
  private PotsService potsService;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }
/*
  @Test
  void findAllPots() {
    Integer accountId = 1;

    List<PotsResponseDTO> potsResponseDTOList = List.of(
        new PotsResponseDTO(1L, "test name2", "Test Pot1", 100L, 1001L),
        new PotsResponseDTO(2L, "test name2", "Test Pot2", 100L, 1002L)
    );

    when(potsService.findAllPots(accountId)).thenReturn(potsResponseDTOList);
    var result = potsController.getPots(accountId);

    assertNotNull(result);
    assertEquals(HttpStatus.OK, result.getStatusCode());
    assertEquals("Pots retrieved successfully", result.getBody().getMessage());
    assertEquals(2, result.getBody().getData().size());
    assertEquals(1L, result.getBody().getData().get(0).potId());
  }

  @Test
  void createPots() {
    PotsResponseDTO potsResponseDTO = new PotsResponseDTO(1L, "test name", "Test Pot", 100L, 100L);
    CreatePotsDTO createPotsDTO = new CreatePotsDTO("test name", "Test Pot", 1, 100L, 100L);
    UserEntity currentUser = UserEntityFactory.defaultUser();
    PotsResult pot = new PotsSuccess(potsResponseDTO);
    when(potsService.create(createPotsDTO, currentUser)).thenReturn(pot);

    var result = potsController.createPots(createPotsDTO, currentUser);

    assertNotNull(result);
    assertEquals(HttpStatus.OK, result.getStatusCode());
    assertEquals("Pots register successfully", result.getBody().getMessage());
    assertEquals("test name", result.getBody().getData().name());
    assertEquals("Test Pot", result.getBody().getData().description());
    assertEquals(100L, result.getBody().getData().currentAmount());
    assertEquals(100L, result.getBody().getData().targetAmount());
  }*/
}
