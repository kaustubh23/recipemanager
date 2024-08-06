package recipe.manager.recipemanager.cacheservice;

import recipe.manager.recipemanager.entity.Ingredient;

import java.util.List;
import java.util.Map;

public interface CacheService {
    Map<String, Ingredient> getIngredientsFromCache(List<String> ingredientNames);

    void addIngredientsToCache(List<Ingredient> ingredients);

    void addIngredientsToCache(Map<String, Ingredient> ingredientMap);

    void evictIngredientFromCache(String ingredientName);

    void clearCache();
}
