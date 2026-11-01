package fun.trackmoney.controller.docs;

import fun.trackmoney.dto.category.CategoryResponseDTO;
import fun.trackmoney.utils.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;

import java.util.List;

@Tag(name = "Categories", description = "Endpoints for managing transaction categories")
public interface CategoryControllerDocs {

  @Operation(summary = "List all categories", description = "Retrieves all available transaction categories")
  @ApiResponses(value = {
      @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200",
          description = "Categories retrieved successfully")
  })
  ResponseEntity<ApiResponse<List<CategoryResponseDTO>>> findAll();

  @Operation(summary = "Get category by ID", description = "Retrieves details of a specific category by its ID")
  @ApiResponses(value = {
      @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200",
          description = "Category found"),
      @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404",
          description = "Category not found")
  })
  ResponseEntity<ApiResponse<CategoryResponseDTO>> findById(
      @Parameter(description = "Category ID", example = "1") Integer id
  );
}
