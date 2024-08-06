package recipe.manager.recipemanager;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import recipe.manager.recipemanager.dto.RecipeDTO;
import recipe.manager.recipemanager.entity.Recipe;
import recipe.manager.recipemanager.exception.ResourceNotFoundException;
import recipe.manager.recipemanager.mapper.RecipeMapper;
import recipe.manager.recipemanager.mapper.RecipeStepMapper;
import recipe.manager.recipemanager.mapper.IngredientMapper;
import recipe.manager.recipemanager.repository.*;
import recipe.manager.recipemanager.service.RecipeService;

import java.util.Optional;

class RecipeServiceTest {

    @Mock
    private RecipeRepository recipeRepository;

    @Mock
    private IngredientRepository ingredientRepository;

    @Mock
    private RecipeIngredientRepository recipeIngredientRepository;

    @Mock
    private RecipeStepRepository recipeStepRepository;

    @Mock
    private RecipeMapper recipeMapper;

    @Mock
    private RecipeStepMapper recipeStepMapper; // Mock this dependency

    @Mock
    private IngredientMapper ingredientMapper; // Mock this dependency

    @InjectMocks
    private RecipeService recipeService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getRecipeById_ShouldReturnRecipe_WhenRecipeExists() {
        Recipe recipe = new Recipe();
        recipe.setRecipeId(1L);
        when(recipeRepository.findById(1L)).thenReturn(Optional.of(recipe));

        RecipeDTO recipeDTO = new RecipeDTO();
        when(recipeMapper.recipeToRecipeDTO(recipe)).thenReturn(recipeDTO);

        Optional<RecipeDTO> result = recipeService.getRecipeById(1L);

        assertTrue(result.isPresent());
        assertEquals(recipeDTO, result.get());
    }

    @Test
    void getRecipeById_ShouldThrowException_WhenRecipeDoesNotExist() {
        when(recipeRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> recipeService.getRecipeById(1L));
    }
}