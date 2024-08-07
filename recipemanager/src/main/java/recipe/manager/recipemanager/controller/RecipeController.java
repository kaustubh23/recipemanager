package recipe.manager.recipemanager.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import recipe.manager.recipemanager.constant.RecipeConstants;
import recipe.manager.recipemanager.dto.ErrorResponseDto;
import recipe.manager.recipemanager.dto.RecipeDTO;
import recipe.manager.recipemanager.dto.ResponseDto;
import recipe.manager.recipemanager.entity.RecipeType;
import recipe.manager.recipemanager.service.RecipeService;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/recipes")
public class RecipeController {

    private final RecipeService recipeService;

    @Autowired
    public RecipeController(RecipeService recipeService) {
        this.recipeService = recipeService;
    }

    @Operation(
            summary = "Create Recipe REST API",
            description = "REST API to create new Recipe"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Recipe created successfully"),
            @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)))
    })
    @PostMapping("/create")
    public ResponseEntity<ResponseDto> addRecipe(@Valid @RequestBody RecipeDTO recipeDTO) {
        try {
            RecipeDTO savedRecipe = recipeService.addRecipe(recipeDTO);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new ResponseDto(RecipeConstants.STATUS_201, String.format(RecipeConstants.MESSAGE_201, savedRecipe.getName())));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(new ResponseDto("Error while creating recipe", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseDto("Error while creating recipe", e.getMessage()));
        }
    }

    @Operation(summary = "Fetch Recipe by recipeId", description = "REST API to fetch recipe by recipeId")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Recipe fetched successfully"),
            @ApiResponse(responseCode = "404", description = "Recipe not found"),
            @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)))
    })
    @GetMapping("/get/{id}")
    public ResponseEntity<RecipeDTO> getRecipeById(@PathVariable Long id) {
        Optional<RecipeDTO> recipeDetails = recipeService.getRecipeById(id);
        return recipeDetails.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @Operation(summary = "Update Recipe Details REST API", description = "REST API to update Recipe details")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Recipe updated successfully"),
            @ApiResponse(responseCode = "417", description = "Expectation Failed"),
            @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)))
    })
    @PutMapping("/update/{id}")
    public ResponseEntity<ResponseDto> updateRecipe(@PathVariable Long id, @Valid @RequestBody RecipeDTO recipeDTO) {
        try {
            RecipeDTO updatedRecipe = recipeService.updateRecipe(id, recipeDTO);
            if (updatedRecipe != null) {
                return ResponseEntity.ok(new ResponseDto(RecipeConstants.STATUS_200, String.format(RecipeConstants.MESSAGE_200, updatedRecipe.getName())));
            } else {
                return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED)
                        .body(new ResponseDto(RecipeConstants.STATUS_417, RecipeConstants.MESSAGE_417_UPDATE));
            }
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(new ResponseDto("Error while updating recipe", e.getMessage()));
        }
    }

    @Operation(summary = "Delete Recipe Details REST API", description = "REST API to delete Recipe details")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Recipe deleted successfully"),
            @ApiResponse(responseCode = "417", description = "Expectation Failed"),
            @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)))
    })
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<ResponseDto> deleteRecipe(@PathVariable Long id) {
        boolean isDeleted = recipeService.deleteRecipe(id);
        if (isDeleted) {
            return ResponseEntity.ok(new ResponseDto(RecipeConstants.STATUS_200, RecipeConstants.MESSAGE_200));
        } else {
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED)
                    .body(new ResponseDto(RecipeConstants.STATUS_417, RecipeConstants.MESSAGE_417_DELETE));
        }
    }

    @Operation(summary = "Search Recipe Details REST API", description = "REST API to search Recipe details based on type, ingredient, instructions, and servings")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Recipes found"),
            @ApiResponse(responseCode = "404", description = "No recipes found"),
            @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)))
    })
    @GetMapping("/search")
    public ResponseEntity<List<RecipeDTO>> searchRecipes(
            @RequestParam(required = false) RecipeType recipeType,
            @RequestParam(required = false) Integer servings,
            @RequestParam(required = false) List<String> includeIngredients,
            @RequestParam(required = false) List<String> excludeIngredients,
            @RequestParam(required = false) String instructions) {

        List<RecipeDTO> recipes = recipeService.searchRecipes(recipeType, servings, includeIngredients, excludeIngredients, instructions);

        if (recipes != null && !recipes.isEmpty()) {
            return ResponseEntity.ok(recipes);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @Operation(summary = "Fetch Recipe by userId", description = "REST API to fetch recipes by userId")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Recipes fetched successfully"),
            @ApiResponse(responseCode = "404", description = "No recipes found"),
            @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)))
    })
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<RecipeDTO>> getRecipesByUserId(@PathVariable Long userId) {
        List<RecipeDTO> recipes = recipeService.getRecipesByUserId(userId);

        if (recipes != null && !recipes.isEmpty()) {
            return ResponseEntity.ok(recipes);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
}

