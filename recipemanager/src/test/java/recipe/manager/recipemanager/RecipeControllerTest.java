package recipe.manager.recipemanager;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import recipe.manager.recipemanager.controller.RecipeController;
import recipe.manager.recipemanager.dto.RecipeDTO;
import recipe.manager.recipemanager.service.RecipeService;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Optional;

class RecipeControllerTest {

    private MockMvc mockMvc;

    @Mock
    private RecipeService recipeService;

    @InjectMocks
    private RecipeController recipeController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(recipeController).build();
    }

    @Test
    void addRecipe_ShouldReturnCreated_WhenRecipeIsAdded() throws Exception {
        RecipeDTO recipeDTO = new RecipeDTO();
        recipeDTO.setName("Recipe 1");

        when(recipeService.addRecipe(any(RecipeDTO.class))).thenReturn(recipeDTO);

        mockMvc.perform(post("/api/recipes/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(recipeDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.statusCode").value("201"));
    }

    @Test
    void getRecipeById_ShouldReturnRecipe_WhenRecipeExists() throws Exception {
        RecipeDTO recipeDTO = new RecipeDTO();
        recipeDTO.setName("Recipe 1");

        when(recipeService.getRecipeById(1L)).thenReturn(Optional.of(recipeDTO));

        mockMvc.perform(get("/api/recipes/get/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Recipe 1"));
    }

    @Test
    void getRecipeById_ShouldReturnNotFound_WhenRecipeDoesNotExist() throws Exception {
        when(recipeService.getRecipeById(1L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/recipes/get/{id}", 1L))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteRecipe_ShouldReturnOk_WhenRecipeIsDeleted() throws Exception {
        when(recipeService.deleteRecipe(1L)).thenReturn(true);

        mockMvc.perform(delete("/api/recipes/delete/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusCode").value("200"));
    }

    @Test
    void updateRecipe_ShouldReturnOk_WhenRecipeIsUpdated() throws Exception {
        RecipeDTO recipeDTO = new RecipeDTO();
        recipeDTO.setName("Updated Recipe");

        when(recipeService.updateRecipe(eq(1L), any(RecipeDTO.class))).thenReturn(recipeDTO);

        mockMvc.perform(put("/api/recipes/update/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(recipeDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusCode").value("200"));
    }


}