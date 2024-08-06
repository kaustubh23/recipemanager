package recipe.manager.recipemanager.service;

import jakarta.persistence.criteria.*;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import recipe.manager.recipemanager.cacheservice.InMemoryCacheService;
import recipe.manager.recipemanager.dto.IngredientDTO;
import recipe.manager.recipemanager.dto.RecipeDTO;
import recipe.manager.recipemanager.dto.RecipeStepDTO;
import recipe.manager.recipemanager.entity.*;
import recipe.manager.recipemanager.exception.DuplicateResourceException;
import recipe.manager.recipemanager.exception.InvalidDataException;
import recipe.manager.recipemanager.exception.InvalidEnumTypeException;
import recipe.manager.recipemanager.exception.ResourceNotFoundException;
import recipe.manager.recipemanager.mapper.IngredientMapper;
import recipe.manager.recipemanager.mapper.RecipeMapper;
import recipe.manager.recipemanager.mapper.RecipeStepMapper;
import recipe.manager.recipemanager.repository.*;
import recipe.manager.recipemanager.utils.RecipeUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Service class for managing recipes, including operations for adding, deleting,
 * updating, fetching, and searching recipes.
 */
@Service
@AllArgsConstructor
public class RecipeService {

    private static final Logger logger = LoggerFactory.getLogger(RecipeService.class);

    private final RecipeRepository recipeRepository;
    private final IngredientRepository ingredientRepository;
    private final RecipeIngredientRepository recipeIngredientRepository;
    private final RecipeStepRepository recipeStepRepository;
    private final IngredientMapper ingredientMapper;
    private final RecipeStepMapper recipeStepMapper;
    private final RecipeMapper recipeMapper;
    private final UserRepository userRepository;
    private final InMemoryCacheService inMemoryCacheService;

    /**
     * Adds a new recipe.
     */
    @Transactional
    public RecipeDTO addRecipe(RecipeDTO recipeDTO) {
        logger.info("Adding a new recipe: {}", recipeDTO);

        validateRecipeData(recipeDTO);

        Recipe recipeEntity = recipeMapper.recipeDTOToRecipe(recipeDTO);
        Recipe savedRecipe = recipeRepository.save(recipeEntity);
        RecipeDTO response = recipeMapper.recipeToRecipeDTO(savedRecipe);

        List<Ingredient> allIngredients = RecipeUtils.handleIngredients(recipeDTO, ingredientRepository, inMemoryCacheService, ingredientMapper);
        recipeIngredientRepository.saveAll(createRecipeIngredients(allIngredients, savedRecipe));
        response.setIngredients(ingredientMapper.ingredientToIngredientDTO(allIngredients));

        if (recipeDTO.getStep() != null) {
            RecipeStep stepEntities = recipeStepMapper.recipeStepDTOToRecipe(recipeDTO.getStep());
            stepEntities.setRecipeId(savedRecipe.getRecipeId());
            recipeStepRepository.save(stepEntities);
            response.setStep(recipeStepMapper.recipeStepToRecipeStepDTO(stepEntities));
        }

        logger.info("Recipe added successfully: {}", response);
        return response;
    }

    /**
     * Deletes a recipe by its ID.
     *
     * @param recipeId the ID of the recipe to delete
     * @return true if the recipe was deleted successfully
     * @throws ResourceNotFoundException if the recipe is not found
     */
    @Transactional
    public boolean deleteRecipe(Long recipeId) {
        logger.info("Deleting recipe with ID: {}", recipeId);

        if (!recipeRepository.existsById(recipeId)) {
            logger.error("Recipe not found with ID: {}", recipeId);
            throw new ResourceNotFoundException("Recipe", "RecipeID", recipeId.toString());
        }

        recipeIngredientRepository.deleteByRecipeId(recipeId);
        recipeStepRepository.deleteByRecipeId(recipeId);
        recipeRepository.deleteById(recipeId);

        logger.info("Recipe deleted successfully with ID: {}", recipeId);
        return true;
    }

    /**
     * Fetches a recipe by its ID.
     *
     * @param recipeId the ID of the recipe to fetch
     * @return an optional containing the recipe data transfer object if found
     * @throws ResourceNotFoundException if the recipe is not found
     */
    public Optional<RecipeDTO> getRecipeById(Long recipeId) {
        logger.info("Fetching recipe with ID: {}", recipeId);

        Recipe recipe = recipeRepository.findById(recipeId)
                .orElseThrow(() -> new ResourceNotFoundException("Recipe", "RecipeID", recipeId.toString()));

        RecipeDTO recipeDTO = convertRecipeToDTO(recipe);
        logger.info("Recipe fetched successfully with ID: {}", recipeId);
        return Optional.of(recipeDTO);
    }

    /**
     * Updates an existing recipe by its ID.
     *
     * @param recipeId the ID of the recipe to update
     * @param recipeDTO the new recipe data
     * @return the updated recipe data transfer object
     * @throws ResourceNotFoundException if the recipe is not found
     */
    @Transactional
    public RecipeDTO updateRecipe(Long recipeId, RecipeDTO recipeDTO) {
        logger.info("Updating recipe with ID: {}", recipeId);

        Recipe existingRecipe = recipeRepository.findById(recipeId)
                .orElseThrow(() -> new ResourceNotFoundException("Recipe", "RecipeID", recipeId.toString()));

        updateRecipeDetails(existingRecipe, recipeDTO);

        Recipe savedRecipe = recipeRepository.save(existingRecipe);
        List<Ingredient> existingIngredients = getUpdatedIngredients(recipeDTO);

        recipeIngredientRepository.deleteByRecipeId(recipeId);
        List<RecipeIngredient> recipeIngredients = createRecipeIngredients(existingIngredients, savedRecipe);
        recipeIngredientRepository.saveAll(recipeIngredients);

        recipeStepRepository.deleteByRecipeId(recipeId);
        RecipeStep stepEntities = recipeStepMapper.recipeStepDTOToRecipe(recipeDTO.getStep());
        recipeStepRepository.save(stepEntities);

        RecipeDTO responseUpdate = convertRecipeToDTO(savedRecipe);
        responseUpdate.setStep(recipeStepMapper.recipeStepToRecipeStepDTO(stepEntities));
        responseUpdate.setIngredients(ingredientMapper.ingredientToIngredientDTO(existingIngredients));

        logger.info("Recipe updated successfully with ID: {}", recipeId);
        return responseUpdate;
    }

    /**
     * Searches for recipes based on various filters.
     *
     * @param recipeType the type of recipe
     * @param servings the number of servings
     * @param includeIngredients list of ingredients to include
     * @param excludeIngredients list of ingredients to exclude
     * @param instructionsKeyword keyword to search in instructions
     * @return a list of matching recipe data transfer objects
     */
    public List<RecipeDTO> searchRecipes(
            RecipeType recipeType,
            Integer servings,
            List<String> includeIngredients,
            List<String> excludeIngredients,
            String instructionsKeyword) {

        logger.info("Searching recipes with filters - Type: {}, Servings: {}, Include Ingredients: {}, Exclude Ingredients: {}, Instructions Keyword: {}",
                recipeType, servings, includeIngredients, excludeIngredients, instructionsKeyword);

        Specification<Recipe> spec = Specification.where(null);
        spec = applyFilters(spec, recipeType, servings, includeIngredients, excludeIngredients, instructionsKeyword);

        List<Recipe> recipes = recipeRepository.findAll(spec);
        List<RecipeDTO> recipeDTOs = convertRecipesToDTOs(recipes);

        logger.info("Found {} recipes matching the criteria", recipeDTOs.size());
        return recipeDTOs;
    }

    /**
     * Fetches all recipes associated with a specific user.
     *
     * @param userId the ID of the user
     * @return a list of recipe data transfer objects for the user
     * @throws ResourceNotFoundException if the user is not found
     */
    public List<RecipeDTO> getRecipesByUserId(Long userId) {
        logger.info("Fetching recipes for user ID: {}", userId);

        if (!userRepository.existsById(userId)) {
            logger.error("User not found with ID: {}", userId);
            throw new ResourceNotFoundException("User", "UserID", userId.toString());
        }

        List<Recipe> recipes = recipeRepository.findByUserId(userId);
        List<RecipeDTO> recipeDTOs = convertRecipesToDTOs(recipes);

        logger.info("Found {} recipes for user ID: {}", recipeDTOs.size(), userId);
        return recipeDTOs;
    }

    // Helper Methods

    private void validateRecipeData(RecipeDTO recipeDTO) {
        if (recipeDTO == null || recipeDTO.getName() == null || recipeDTO.getName().isEmpty()) {
            logger.error("Invalid recipe data: {}", recipeDTO);
            throw new InvalidDataException("Recipe data is incomplete or invalid.");
        }

        if (recipeDTO.getUserId() == null || !userRepository.existsById(recipeDTO.getUserId())) {
            logger.error("User not found for ID: {}", recipeDTO.getUserId());
            throw new ResourceNotFoundException("User", "UserID", recipeDTO.getUserId().toString());
        }

        String recipeName = recipeDTO.getName().trim().toLowerCase();
        if (recipeRepository.existsByNameIgnoreCase(recipeName)) {
            logger.error("Duplicate recipe found with name: {}", recipeDTO.getName());
            throw new DuplicateResourceException("Recipe", "name", recipeDTO.getName());
        }

        if (!EnumSet.allOf(RecipeType.class).contains(recipeDTO.getRecipeType())) {
            logger.error("Invalid recipe type provided: {}", recipeDTO.getRecipeType());
            throw new InvalidEnumTypeException("Invalid type provided: " + recipeDTO.getRecipeType());
        }
    }

    private void updateRecipeDetails(Recipe existingRecipe, RecipeDTO recipeDTO) {
        existingRecipe.setName(recipeDTO.getName());
        existingRecipe.setRecipeType(recipeDTO.getRecipeType());
        existingRecipe.setServings(recipeDTO.getServings());
    }

    private List<Ingredient> getUpdatedIngredients(RecipeDTO recipeDTO) {
        List<Ingredient> existingIngredients = new ArrayList<>();
        List<Ingredient> newIngredients = new ArrayList<>();

        for (IngredientDTO ingredientDTO : recipeDTO.getIngredients()) {
            if (ingredientDTO.getIngredientId() != null) {
                Ingredient existingIngredient = ingredientRepository.findById(ingredientDTO.getIngredientId())
                        .orElseThrow(() -> new ResourceNotFoundException("Ingredient", "IngredientID", ingredientDTO.getIngredientId().toString()));
                existingIngredient.setIngredientName(ingredientDTO.getIngredientName());
                existingIngredients.add(existingIngredient);
            } else {
                Ingredient newIngredient = ingredientMapper.ingredientDTOToReciIngredient(ingredientDTO);
                newIngredients.add(newIngredient);
            }
        }

        List<Ingredient> savedNewIngredients = ingredientRepository.saveAll(newIngredients);
        existingIngredients.addAll(savedNewIngredients);

        return existingIngredients;
    }

    private Specification<Recipe> applyFilters(
            Specification<Recipe> spec,
            RecipeType recipeType,
            Integer servings,
            List<String> includeIngredients,
            List<String> excludeIngredients,
            String instructionsKeyword) {

        if (recipeType != null) {
            spec = spec.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.equal(root.get("recipeType"), recipeType));
        }

        if (servings != null) {
            spec = spec.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.equal(root.get("servings"), servings));
        }

        if (includeIngredients != null && !includeIngredients.isEmpty()) {
            spec = spec.and((root, query, criteriaBuilder) -> {
                Join<Recipe, RecipeIngredient> recipeIngredients = root.join("recipeIngredients", JoinType.LEFT);
                Join<RecipeIngredient, Ingredient> ingredients = recipeIngredients.join("ingredient", JoinType.LEFT);

                List<String> lowerCaseIncludeIngredients = includeIngredients.stream()
                        .map(String::toLowerCase)
                        .collect(Collectors.toList());

                return criteriaBuilder.lower(ingredients.get("ingredientName")).in(lowerCaseIncludeIngredients);
            });
        }

        if (excludeIngredients != null && !excludeIngredients.isEmpty()) {
            spec = spec.and((root, query, criteriaBuilder) -> {
                Join<Recipe, RecipeIngredient> recipeIngredients = root.join("recipeIngredients", JoinType.LEFT);
                Join<RecipeIngredient, Ingredient> ingredients = recipeIngredients.join("ingredient", JoinType.LEFT);

                List<String> lowerCaseExcludeIngredients = excludeIngredients.stream()
                        .map(String::toLowerCase)
                        .collect(Collectors.toList());

                return criteriaBuilder.not(
                        criteriaBuilder.lower(ingredients.get("ingredientName")).in(lowerCaseExcludeIngredients)
                );
            });
        }

        if (StringUtils.hasText(instructionsKeyword)) {
            spec = spec.and((root, query, criteriaBuilder) -> {
                Join<Recipe, RecipeStep> steps = root.join("recipeSteps", JoinType.LEFT);
                String lowerKeyword = instructionsKeyword.toLowerCase();
                return criteriaBuilder.like(
                        criteriaBuilder.lower(steps.get("instructions")),
                        "%" + lowerKeyword + "%"
                );
            });
        }

        return spec;
    }

    private List<RecipeIngredient> createRecipeIngredients(List<Ingredient> allIngredients, Recipe savedRecipe) {
        return allIngredients.stream()
                .map(ingredient -> {
                    RecipeIngredient recipeIngredient = new RecipeIngredient();
                    recipeIngredient.setRecipeId(savedRecipe.getRecipeId());
                    recipeIngredient.setIngredientId(ingredient.getIngredientId());
                    return recipeIngredient;
                }).collect(Collectors.toList());
    }

    private RecipeDTO convertRecipeToDTO(Recipe recipe) {
        List<RecipeIngredient> recipeIngredients = recipeIngredientRepository.findAllByRecipeId(recipe.getRecipeId());
        List<IngredientDTO> ingredients = recipeIngredients.stream()
                .map(ri -> ingredientMapper.ingredientToIngredientDTO(
                        ingredientRepository.findById(ri.getIngredientId())
                                .orElseThrow(() -> new ResourceNotFoundException("Ingredient", "IngredientID", ri.getIngredientId().toString()))))
                .collect(Collectors.toList());

        RecipeStep recipeSteps = recipeStepRepository.findByRecipeId(recipe.getRecipeId());
        RecipeStepDTO steps = recipeStepMapper.recipeStepToRecipeStepDTO(recipeSteps);

        RecipeDTO recipeDTO = recipeMapper.recipeToRecipeDTO(recipe);
        recipeDTO.setIngredients(ingredients);
        recipeDTO.setStep(steps);

        return recipeDTO;
    }

    private List<RecipeDTO> convertRecipesToDTOs(List<Recipe> recipes) {
        return recipes.stream()
                .map(this::convertRecipeToDTO)
                .collect(Collectors.toList());
    }
}
