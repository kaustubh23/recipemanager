package recipe.manager.recipemanager.repository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import recipe.manager.recipemanager.entity.Recipe;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import recipe.manager.recipemanager.entity.RecipeType;

@Repository
public interface RecipeRepository extends JpaRepository<Recipe, Long>, JpaSpecificationExecutor<Recipe> {
    List<Recipe> findByRecipeType(RecipeType recipeType);
    List<Recipe> findByServings(int servings);
    @Query("SELECT r FROM Recipe r JOIN RecipeIngredient ri ON r.recipeId = ri.recipeId JOIN Ingredient i ON ri.ingredientId = i.ingredientId WHERE i.ingredientName = :ingredientName")
    List<Recipe> findByIngredient(String ingredientName);
    @Query("SELECT r FROM Recipe r JOIN RecipeStep rs ON r.recipeId = rs.recipeId WHERE rs.instructions LIKE %:text%")
    List<Recipe> findByInstructionsContaining(String text);
    List<Recipe> findByUserId(Long userId);
    boolean existsByNameIgnoreCase(String name);
}


