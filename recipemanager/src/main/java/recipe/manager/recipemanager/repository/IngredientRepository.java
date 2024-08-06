package recipe.manager.recipemanager.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import recipe.manager.recipemanager.entity.Ingredient;

import java.util.List;


@Repository
public interface IngredientRepository extends JpaRepository<Ingredient, Long> {
    void deleteByIngredientId(Long recipeId);
    List<Ingredient> findAllByIngredientNameIn(List<String> ingredients);
}