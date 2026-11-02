package fun.trackmoney.controller.docs;

import fun.trackmoney.dto.pots.CreatePotsDTO;
import fun.trackmoney.dto.pots.MoneyRequest;
import fun.trackmoney.dto.pots.PotsResponseDTO;
import fun.trackmoney.entity.UserEntity;
import fun.trackmoney.utils.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;

import java.util.List;

@Tag(name = "Pots", description = "Endpoints for managing money pots / savings envelopes")
public interface PotsControllerDocs {

  @Operation(summary = "Create pot", description = "Creates a new money pot for the user")
  @ApiResponses(value = {
      @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200",
          description = "Pot created successfully"),
      @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400",
          description = "Invalid pot creation data")
  })
  ResponseEntity<ApiResponse<PotsResponseDTO>> createPots(
      CreatePotsDTO dto,
      @Parameter(hidden = true) UserEntity currentUser
  );

  @Operation(summary = "List all pots", description = "Retrieves all pots belonging to the authenticated user")
  @ApiResponses(value = {
      @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200",
          description = "Pots retrieved successfully")
  })
  ResponseEntity<ApiResponse<List<PotsResponseDTO>>> getPots(
      @Parameter(hidden = true) UserEntity currentUser
  );

  @Operation(summary = "Add or withdraw money from pot", description = "Updates the balance of an existing pot")
  @ApiResponses(value = {
      @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200",
          description = "Money added or deducted successfully"),
      @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400",
          description = "Insufficient funds or invalid amount"),
      @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404",
          description = "Pot not found")
  })
  ResponseEntity<ApiResponse<PotsResponseDTO>> addMoney(
      @Parameter(description = "Pot ID", example = "1") Long id,
      MoneyRequest moneyRequest,
      @Parameter(hidden = true) UserEntity currentUser
  );

  @Operation(summary = "Delete pot", description = "Deletes a pot by its ID")
  @ApiResponses(value = {
      @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200",
          description = "Pot deleted successfully"),
      @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404",
          description = "Pot not found")
  })
  ResponseEntity<ApiResponse<Void>> delete(
      @Parameter(description = "Pot ID", example = "1") Long id,
      @Parameter(hidden = true) UserEntity currentUser
  );

  @Operation(summary = "Update pot", description = "Updates details of an existing pot")
  @ApiResponses(value = {
      @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200",
          description = "Pot updated successfully"),
      @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400",
          description = "Invalid update data"),
      @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404",
          description = "Pot not found")
  })
  ResponseEntity<ApiResponse<PotsResponseDTO>> update(
      @Parameter(description = "Pot ID", example = "1") Long id,
      CreatePotsDTO request,
      @Parameter(hidden = true) UserEntity currentUser
  );
}
