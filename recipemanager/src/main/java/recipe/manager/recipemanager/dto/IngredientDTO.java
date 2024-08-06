package recipe.manager.recipemanager.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class IngredientDTO {
    @Schema(description = "ingredientId")
    private Long ingredientId;
    @Schema(description = "Ingredient name",example = "Salt, Oil, Rice")
    private String ingredientName;
}