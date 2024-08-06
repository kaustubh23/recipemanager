package recipe.manager.recipemanager.utils;


import org.springframework.stereotype.Component;
import recipe.manager.recipemanager.cacheservice.CacheService;
import recipe.manager.recipemanager.dto.IngredientDTO;
import recipe.manager.recipemanager.dto.RecipeDTO;
import recipe.manager.recipemanager.entity.Ingredient;
import recipe.manager.recipemanager.entity.Recipe;
import recipe.manager.recipemanager.mapper.IngredientMapper;
import recipe.manager.recipemanager.repository.IngredientRepository;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class RecipeUtils {

    public static List<Ingredient> handleIngredients(RecipeDTO recipeDTO,
                                                     IngredientRepository ingredientRepository,
                                                     CacheService cacheService,
                                                     IngredientMapper ingredientMapper) {
        List<String> ingredientNames = recipeDTO.getIngredients().stream()
                .map(IngredientDTO::getIngredientName)
                .collect(Collectors.toList());

        // Fetch ingredients from cache
        Map<String, Ingredient> cachedIngredients = cacheService.getIngredientsFromCache(ingredientNames);

        // Identify non-cached ingredients
        List<String> nonCachedNames = ingredientNames.stream()
                .filter(name -> !cachedIngredients.containsKey(name))
                .collect(Collectors.toList());

        // Fetch non-cached ingredients from the database
        List<Ingredient> existingIngredients = nonCachedNames.isEmpty() ?
                Collections.emptyList() : ingredientRepository.findAllByIngredientNameIn(nonCachedNames);

        // Update cache with the fetched ingredients
        cacheService.addIngredientsToCache(existingIngredients);

        // Merge cached and existing ingredients
        Map<String, Ingredient> allExistingIngredients = new HashMap<>(cachedIngredients);
        existingIngredients.forEach(ingredient -> allExistingIngredients.put(ingredient.getIngredientName(), ingredient));

        // Prepare list for new ingredients
        List<Ingredient> newIngredients = recipeDTO.getIngredients().stream()
                .filter(ingredientDTO -> !allExistingIngredients.containsKey(ingredientDTO.getIngredientName()))
                .map(ingredientDTO -> ingredientMapper.ingredientDTOToReciIngredient(ingredientDTO))
                .collect(Collectors.toList());

        // Save new ingredients in bulk
        List<Ingredient> savedNewIngredients = new ArrayList<>();
        if (!newIngredients.isEmpty()) {
            savedNewIngredients = ingredientRepository.saveAll(newIngredients);
            cacheService.addIngredientsToCache(savedNewIngredients); // Update cache
        }

        // Merge new ingredients into allExistingIngredients
        allExistingIngredients.putAll(savedNewIngredients.stream()
                .collect(Collectors.toMap(Ingredient::getIngredientName, ingredient -> ingredient)));

        return new ArrayList<>(allExistingIngredients.values());
    }

    public static List<RecipeDTO>  mapRecipeToRecipeDTOList(List<Recipe> recipe){
        List<RecipeDTO> recipeDTOList = new ArrayList<>();
        for(Recipe item: recipe){
            RecipeDTO recipeDTO = new RecipeDTO();
            recipeDTO.setRecipeId(item.getRecipeId());
            recipeDTO.setRecipeType(item.getRecipeType());
            recipeDTO.setName(item.getName());
            recipeDTO.setServings(item.getServings());
            recipeDTO.setUserId(item.getUserId());
            recipeDTOList.add(recipeDTO);
        }
        return recipeDTOList;
    }
}
