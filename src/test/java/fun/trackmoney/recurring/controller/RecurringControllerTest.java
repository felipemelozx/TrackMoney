package fun.trackmoney.recurring.controller;

import fun.trackmoney.recurring.dtos.CreateRecurringRequest;
import fun.trackmoney.recurring.dtos.RecurringResponse;
import fun.trackmoney.recurring.service.RecurringService;
import fun.trackmoney.testutils.CreateRecurringRequestFactory;
import fun.trackmoney.testutils.RecurringResponseFactory;
import fun.trackmoney.testutils.UserEntityFactory;
import fun.trackmoney.user.entity.UserEntity;
import fun.trackmoney.utils.response.ApiResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class RecurringControllerTest {

  @Mock
  private RecurringService recurringService;

  @InjectMocks
  private RecurringController recurringController;

  @Test
  void createRecurring_shouldReturn200AndSuccessResponse_whenRecurringIsCreated() {
    CreateRecurringRequest request = CreateRecurringRequestFactory.defaultRequest();
    RecurringResponse expectedResponse = RecurringResponseFactory.defaultResponse();
    UserEntity mockUser = UserEntityFactory.defaultUser();

    when(recurringService.create(any(CreateRecurringRequest.class), any(UserEntity.class)))
        .thenReturn(expectedResponse);

    ResponseEntity<ApiResponse<RecurringResponse>> response =
        recurringController.createRecurring(request, mockUser);

    assertNotNull(response);
    assertEquals(HttpStatus.OK, response.getStatusCode(), "O Status HTTP deve ser 200 OK.");

    ApiResponse<RecurringResponse> body = response.getBody();
    assertNotNull(body, "O corpo da resposta não deve ser nulo.");
    assertTrue(body.isSuccess(), "O status da API deve ser SUCCESS.");
    assertEquals("Recurring created with successfully", body.getMessage(), "A mensagem de sucesso deve estar correta.");
    assertEquals(expectedResponse.id(), body.getData().id(), "O ID da recorrência retornada deve ser o esperado.");
  }

  @Test
  void createRecurring_shouldReturn400AndFailureResponse_whenCategoryIsNotFound() {
    CreateRecurringRequest request = CreateRecurringRequestFactory.defaultRequest();
    UserEntity mockUser = UserEntityFactory.defaultUser();
    String expectedCategoryId = request.categoryId().toString();

    when(recurringService.create(any(CreateRecurringRequest.class), any(UserEntity.class)))
        .thenReturn(null);

    ResponseEntity<ApiResponse<RecurringResponse>> response =
        recurringController.createRecurring(request, mockUser);

    assertNotNull(response);
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode(), "O Status HTTP deve ser 400 Bad Request.");

    ApiResponse<RecurringResponse> body = response.getBody();
    assertNotNull(body, "O corpo da resposta não deve ser nulo.");
    assertFalse(body.isSuccess(), "O status da API deve ser FAILURE.");
    assertEquals("Failure when try to create the recurring transaction", body.getMessage(), "A mensagem de falha deve estar correta.");

    assertNotNull(body.getErrors(), "A lista de erros não deve ser nula.");
    assertTrue(body.getErrors().size() == 1, "Deve haver exatamente um erro.");

    assertEquals("categoryId", body.getErrors().get(0).getField(), "O campo de erro deve ser 'categoryId'.");
    assertEquals("Category with this id: " + expectedCategoryId + " not found.", body.getErrors().get(0).getMessage(), "A mensagem de erro específica deve estar correta.");
  }

  @Test
  void update_shouldReturn200AndSuccessResponse_whenRecurringIsUpdated() {
    CreateRecurringRequest request = CreateRecurringRequestFactory.defaultRequest();
    RecurringResponse expectedResponse = RecurringResponseFactory.defaultResponse();
    UserEntity mockUser = UserEntityFactory.defaultUser();


    when(recurringService.update(1l, request, mockUser))
        .thenReturn(expectedResponse);

    ResponseEntity<ApiResponse<RecurringResponse>> response =
        recurringController.update(1l, request, mockUser);

    assertNotNull(response);
    assertEquals(HttpStatus.OK, response.getStatusCode(), "O Status HTTP deve ser 200 OK.");

    ApiResponse<RecurringResponse> body = response.getBody();
    assertNotNull(body, "O corpo da resposta não deve ser nulo.");
    assertTrue(body.isSuccess(), "O status da API deve ser SUCCESS.");
    assertEquals("Recurring update with successfully", body.getMessage(), "A mensagem de sucesso deve estar correta.");
    assertEquals(1l, body.getData().id(), "O ID da recorrência retornada deve ser o esperado.");
  }

  @Test
  void update_shouldReturn400AndFailureResponse_whenRecurringUpdateFails() {
    CreateRecurringRequest request = CreateRecurringRequestFactory.defaultRequest();
    UserEntity mockUser = UserEntityFactory.defaultUser();
    String expectedCategoryId = request.categoryId().toString();

    when(recurringService.update(1l, request, mockUser))
        .thenReturn(null);

    ResponseEntity<ApiResponse<RecurringResponse>> response =
        recurringController.update(1l, request, mockUser);

    assertNotNull(response);
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode(), "O Status HTTP deve ser 400 Bad Request.");

    ApiResponse<RecurringResponse> body = response.getBody();
    assertNotNull(body, "O corpo da resposta não deve ser nulo.");
    assertFalse(body.isSuccess(), "O status da API deve ser FAILURE.");
    assertEquals("Failure when try to update the recurring transaction", body.getMessage(), "A mensagem de falha deve estar correta.");

    assertNotNull(body.getErrors(), "A lista de erros não deve ser nula.");
    assertTrue(body.getErrors().size() == 1, "Deve haver exatamente um erro.");

    assertEquals("categoryId", body.getErrors().get(0).getField(), "O campo de erro deve ser 'categoryId'.");
    assertEquals("Category with this id: " + expectedCategoryId + " not found.", body.getErrors().get(0).getMessage(), "A mensagem de erro específica deve estar correta.");
  }

  @Test
  void findAll_shouldReturn200AndListOfRecurrences() {
    UserEntity mockUser = UserEntityFactory.defaultUser();
    RecurringResponse resp1 = RecurringResponseFactory.defaultResponse();
    RecurringResponse resp2 = RecurringResponseFactory.defaultResponse();
    List<RecurringResponse> expectedList = List.of(resp1, resp2);

    when(recurringService.findAll(mockUser)).thenReturn(expectedList);

    ResponseEntity<ApiResponse<List<RecurringResponse>>> response =
        recurringController.findAll(mockUser);

    assertNotNull(response);
    assertEquals(HttpStatus.OK, response.getStatusCode(), "O Status HTTP deve ser 200 OK.");

    ApiResponse<List<RecurringResponse>> body = response.getBody();
    assertNotNull(body, "O corpo da resposta não deve ser nulo.");
    assertTrue(body.isSuccess(), "O status da API deve ser SUCCESS.");
    assertEquals("Successfully retrieved all recurring transactions", body.getMessage(), "A mensagem de sucesso deve estar correta.");
    assertEquals(2, body.getData().size(), "O tamanho da lista deve ser 2.");

    verify(recurringService, times(1)).findAll(mockUser);
  }

  @Test
  void delete_shouldReturn200AndSuccessWithNoContent_whenDeletionIsSuccessful() {
    UserEntity mockUser = UserEntityFactory.defaultUser();

    doNothing().when(recurringService).delete(1l, mockUser);

    ResponseEntity<ApiResponse<Void>> response =
        recurringController.delete(1l, mockUser);

    assertNotNull(response);
    assertEquals(HttpStatus.OK, response.getStatusCode(), "O Status HTTP deve ser 200 OK.");

    ApiResponse<Void> body = response.getBody();
    assertNotNull(body, "O corpo da resposta não deve ser nulo.");
    assertTrue(body.isSuccess(), "O status da API deve ser SUCCESS.");
    assertEquals(null, body.getData(), "O campo data deve ser nulo para sucesso sem conteúdo.");

    verify(recurringService, times(1)).delete(1l, mockUser);
  }
}