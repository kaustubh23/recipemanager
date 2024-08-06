package recipe.manager.recipemanager.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import recipe.manager.recipemanager.entity.RecipeType;

import java.util.List;
@Data
public class RecipeDTO {
    @Schema(description = "The unique ID of the recipe")
    private Long recipeId;
    @Schema(description = "The name of the recipe", example = "Spaghetti Bolognese")
    private String name;
    @Schema(description = "Type of the recipe", example = "VEGETARIAN", allowableValues = {"VEGETARIAN", "NON_VEGETARIAN", "VEGAN"})
    private RecipeType recipeType;
    @Schema(description = "Number of servings", example = "4")
    private int servings;
    @Schema(description = "The ID of the user who created the recipe")
    private Long userId;
    @Schema(description = "List of ingredients", implementation = IngredientDTO.class)
    private List<IngredientDTO> ingredients;
    @Schema(description = "Steps for the recipe", implementation = RecipeStepDTO.class)
    private RecipeStepDTO step;
}