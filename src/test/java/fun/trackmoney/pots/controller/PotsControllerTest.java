package fun.trackmoney.pots.controller;

import fun.trackmoney.enums.TransactionType;
import fun.trackmoney.pots.dtos.CreatePotsDTO;
import fun.trackmoney.pots.dtos.MoneyRequest;
import fun.trackmoney.pots.dtos.PotsResponseDTO;
import fun.trackmoney.pots.dtos.internal.PotsFailure;
import fun.trackmoney.pots.dtos.internal.PotsSuccess;
import fun.trackmoney.pots.enums.PotsErrorType;
import fun.trackmoney.pots.service.PotsService;
import fun.trackmoney.testutils.CreatePotsDTOFactory;
import fun.trackmoney.testutils.PotsResponseDTOFactory;
import fun.trackmoney.testutils.UserEntityFactory;
import fun.trackmoney.user.entity.UserEntity;
import fun.trackmoney.utils.response.ApiResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PotsControllerTest {

  @Mock
  private PotsService potsService;

  @InjectMocks
  private PotsController potsController;

  @Test
  @DisplayName("createPots: Should return 200 OK and expected data")
  void createPots_shouldReturnOk() {
    UserEntity user = UserEntityFactory.defaultUser();
    CreatePotsDTO dto = CreatePotsDTOFactory.defaultCreatePot();
    PotsResponseDTO expectedResponse = PotsResponseDTOFactory.defaultPotResponse();

    when(potsService.create(dto, user)).thenReturn(expectedResponse);

    ResponseEntity<ApiResponse<PotsResponseDTO>> response = potsController.createPots(dto, user);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getBody());
    assertEquals("Pots register successfully", response.getBody().getMessage());
    assertEquals(expectedResponse, response.getBody().getData());
  }

  @Test
  @DisplayName("getPots: Should return 200 OK and list of pots")
  void getPots_shouldReturnList() {
    UserEntity user = UserEntityFactory.defaultUser();
    List<PotsResponseDTO> expectedList = List.of(PotsResponseDTOFactory.defaultPotResponse());

    when(potsService.findAllPots(user)).thenReturn(expectedList);

    ResponseEntity<ApiResponse<List<PotsResponseDTO>>> response = potsController.getPots(user);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getBody());
    assertEquals(expectedList, response.getBody().getData());
  }

  @Test
  @DisplayName("delete: Should call service and return 200 OK")
  void delete_shouldReturnOk() {
    UserEntity user = UserEntityFactory.defaultUser();
    Long potId = 1L;

    doNothing().when(potsService).delete(potId, user);

    ResponseEntity<ApiResponse<Void>> response = potsController.delete(potId, user);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    verify(potsService, times(1)).delete(potId, user);
  }

  @Test
  @DisplayName("addMoney: Should return 200 OK when result is Success")
  void addMoney_shouldReturnOk_whenSuccess() {
    UserEntity user = UserEntityFactory.defaultUser();
    Long potId = 1L;
    MoneyRequest request = new MoneyRequest(TransactionType.INCOME, new BigDecimal("100"));
    PotsResponseDTO responseDTO = PotsResponseDTOFactory.defaultPotResponse();

    when(potsService.addMoney(potId, request, user))
        .thenReturn(new PotsSuccess(responseDTO));

    ResponseEntity<ApiResponse<PotsResponseDTO>> response = potsController.addMoney(potId, request, user);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(responseDTO, response.getBody().getData());
  }

  @Test
  @DisplayName("addMoney: Should return 404 NOT_FOUND when result is NOT_FOUND error")
  void addMoney_shouldReturn404_whenNotFound() {
    UserEntity user = UserEntityFactory.defaultUser();
    Long potId = 1L;
    MoneyRequest request = new MoneyRequest(TransactionType.INCOME, new BigDecimal("100"));

    when(potsService.addMoney(potId, request, user))
        .thenReturn(new PotsFailure(PotsErrorType.NOT_FOUND, "id", "Pot not found"));

    ResponseEntity<ApiResponse<PotsResponseDTO>> response = potsController.addMoney(potId, request, user);

    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    assertEquals("Pot not found", response.getBody().getMessage());
  }

  @Test
  @DisplayName("addMoney: Should return 400 BAD_REQUEST when result is BAD_REQUEST error")
  void addMoney_shouldReturn400_whenBadRequest() {
    UserEntity user = UserEntityFactory.defaultUser();
    Long potId = 1L;
    MoneyRequest request = new MoneyRequest(TransactionType.INCOME, new BigDecimal("5000"));

    when(potsService.addMoney(potId, request, user))
        .thenReturn(new PotsFailure(PotsErrorType.BAD_REQUEST, "Money", "Limit exceeded"));

    ResponseEntity<ApiResponse<PotsResponseDTO>> response = potsController.addMoney(potId, request, user);

    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    assertEquals("Limit exceeded", response.getBody().getMessage());
  }

  @Test
  @DisplayName("update: Should return 200 OK when update is successful")
  void update_shouldReturnOk_whenSuccess() {
    Long id = 1L;
    UserEntity user = UserEntityFactory.defaultUser();
    CreatePotsDTO request = CreatePotsDTOFactory.defaultCreatePot();
    PotsResponseDTO responseDTO = PotsResponseDTOFactory.defaultPotResponse();

    when(potsService.update(id, request, user))
        .thenReturn(new PotsSuccess(responseDTO));

    ResponseEntity<ApiResponse<PotsResponseDTO>> response = potsController.update(id, request, user);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getBody());
    assertEquals("Pot updated successfully", response.getBody().getMessage());
    assertEquals(responseDTO, response.getBody().getData());
  }

  @Test
  @DisplayName("update: Should return 404 NOT_FOUND when pot does not exist")
  void update_shouldReturn404_whenNotFound() {
    Long id = 1L;
    UserEntity user = UserEntityFactory.defaultUser();
    CreatePotsDTO request = CreatePotsDTOFactory.defaultCreatePot();

    when(potsService.update(id, request, user))
        .thenReturn(new PotsFailure(PotsErrorType.NOT_FOUND, "id", "Pots not found!"));

    ResponseEntity<ApiResponse<PotsResponseDTO>> response = potsController.update(id, request, user);

    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    assertNotNull(response.getBody());
    assertEquals("Pots not found!", response.getBody().getMessage());
  }

  @Test
  @DisplayName("update: Should return 400 BAD_REQUEST when logic fails (e.g. target < current)")
  void update_shouldReturn400_whenBadRequest() {
    Long id = 1L;
    UserEntity user = UserEntityFactory.defaultUser();
    CreatePotsDTO request = CreatePotsDTOFactory.defaultCreatePot();

    when(potsService.update(id, request, user))
        .thenReturn(new PotsFailure(PotsErrorType.BAD_REQUEST, "targetAmount", "Target amount cannot be less than current amount!"));

    ResponseEntity<ApiResponse<PotsResponseDTO>> response = potsController.update(id, request, user);

    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    assertNotNull(response.getBody());
    assertEquals("Target amount cannot be less than current amount!", response.getBody().getMessage());
    assertEquals("targetAmount", response.getBody().getErrors().get(0).getField());
  }
}