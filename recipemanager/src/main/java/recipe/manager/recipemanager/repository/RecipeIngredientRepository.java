package recipe.manager.recipemanager.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import recipe.manager.recipemanager.entity.RecipeIngredient;

import java.util.List;

@Repository
public interface RecipeIngredientRepository extends JpaRepository<RecipeIngredient, Long> {
    void deleteByRecipeId(Long recipeId);
    List<RecipeIngredient> findAllByRecipeId(Long recipeId);
    List<RecipeIngredient> findByRecipeId(Long recipeId);
}