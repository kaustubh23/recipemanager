package recipe.manager.recipemanager.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import recipe.manager.recipemanager.entity.RecipeStep;

import java.util.List;

@Repository
public interface RecipeStepRepository extends JpaRepository<RecipeStep, Long> {
    RecipeStep findByRecipeId(Long recipeId);
    void deleteByRecipeId(Long recipeId);

}