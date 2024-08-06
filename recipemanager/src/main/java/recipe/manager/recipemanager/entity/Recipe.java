package recipe.manager.recipemanager.entity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import jakarta.persistence.*;


import java.util.List;

@Entity
@Data
public class Recipe {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)

    private Long recipeId;
    private String name;

    @Enumerated(EnumType.STRING)
    @Schema(description = "Type of Recipe", example = "VEGETARIAN, NON_VEGETARIAN, VEGAN")
    private RecipeType recipeType;

    private int servings;
    private Long userId;

    @OneToMany(mappedBy = "recipeId", fetch = FetchType.LAZY)
    private List<RecipeStep> recipeSteps;

    @OneToMany(mappedBy = "recipe", fetch = FetchType.LAZY)
    private List<RecipeIngredient> recipeIngredients;


}
