package recipe.manager.recipemanager.entity;

import lombok.Data;
import jakarta.persistence.*;

@Data
@Entity
@IdClass(RecipeIngredientId.class)
public class RecipeIngredient {

    @Id
    private Long recipeId;

    @Id
    private Long ingredientId;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ingredientId", insertable = false, updatable = false)
    private Ingredient ingredient;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recipeId", insertable = false, updatable = false)
    private Recipe recipe;



}