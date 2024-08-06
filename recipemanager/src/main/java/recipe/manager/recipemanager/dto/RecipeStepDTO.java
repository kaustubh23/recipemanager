package recipe.manager.recipemanager.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RecipeStepDTO {
    @Schema(description = "Recipe ingredient Id")
    private Long rsid;
    @Schema(description = "Instruction steps ", implementation = RecipeStepDTO.class)
    @NotEmpty(message = "Name can not be a null or empty")
    @Size(min = 5, max = 500, message = "The length of the instruction should be between 5 and 500")
    private String instructions;
    @Schema(description = "Recipe Id")
    private Long recipeId;
}