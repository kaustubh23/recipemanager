package recipe.manager.recipemanager.cacheservice;

import org.springframework.stereotype.Service;
import recipe.manager.recipemanager.entity.Ingredient;import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
public class InMemoryCacheService implements CacheService {

    private final ConcurrentHashMap<String, Ingredient> ingredientCache = new ConcurrentHashMap<>();

    @Override
    public Map<String, Ingredient> getIngredientsFromCache(List<String> ingredientNames) {
        return ingredientNames.stream()
                .filter(ingredientCache::containsKey)
                .collect(Collectors.toMap(name -> name, ingredientCache::get));
    }

    @Override
    public void addIngredientsToCache(List<Ingredient> ingredients) {
        ingredients.forEach(ingredient -> ingredientCache.put(ingredient.getIngredientName(), ingredient));
    }

    @Override
    public void addIngredientsToCache(Map<String, Ingredient> ingredientMap) {
        ingredientCache.putAll(ingredientMap);
    }

    @Override
    public void evictIngredientFromCache(String ingredientName) {
        ingredientCache.remove(ingredientName);
    }

    @Override
    public void clearCache() {
        ingredientCache.clear();
    }
}