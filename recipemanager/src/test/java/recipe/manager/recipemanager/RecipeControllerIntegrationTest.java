package recipe.manager.recipemanager;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import recipe.manager.recipemanager.controller.RecipeController;
import recipe.manager.recipemanager.dto.IngredientDTO;
import recipe.manager.recipemanager.dto.RecipeDTO;
import recipe.manager.recipemanager.dto.RecipeStepDTO;
import recipe.manager.recipemanager.dto.ResponseDto;
import recipe.manager.recipemanager.entity.RecipeType;
import recipe.manager.recipemanager.entity.User;
import recipe.manager.recipemanager.service.RecipeService;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class RecipeControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Mock
    private RecipeService recipeService;

    @InjectMocks
    private RecipeController recipeController;
    Long userId=   1L;
    Long recipeId=   1L;
    @BeforeEach
    public void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);
        User user = new User();
        user.setName("TestUser");
        user.setPassword("TestPassword");


        MvcResult userResult = mockMvc.perform(MockMvcRequestBuilders.post("/api/users/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("TestUser"))
                .andExpect(jsonPath("$.password").value("TestPassword"))
                .andReturn();
        String responseString = userResult.getResponse().getContentAsString();
        userId=JsonPath.parse(responseString).read("$.userId", Long.class);

    }

    /*@Test
    @Order(1)
    public void createUser() {

    }*/
    @Order(1)
    @Test
    public void testRecipeProcess() throws Exception {
//User Creation
        RecipeDTO recipeDTO = new RecipeDTO();
        IngredientDTO  ingredientDTO = new IngredientDTO();
        ingredientDTO.setIngredientName("salt");
        List<IngredientDTO> ingredientDTOList = Arrays.asList(ingredientDTO);

        RecipeStepDTO recipeStepDTO = new RecipeStepDTO();

        recipeStepDTO.setInstructions("Cook it well");
        int length = 10;

        boolean useLetters = true;
        boolean useNumbers = false;
        String generatedString = RandomStringUtils.random(length, useLetters, useNumbers);
        recipeDTO.setName(generatedString);
        recipeDTO.setRecipeType(RecipeType.VEGAN);
        recipeDTO.setIngredients(ingredientDTOList);
        recipeDTO.setUserId(userId);
        recipeDTO.setServings(10);
        recipeDTO.setStep(recipeStepDTO);
        // Set other fields as necessary

        ResponseDto responseDto = new ResponseDto("201", "Recipe created successfully");
        MvcResult recipeResult = mockMvc.perform(MockMvcRequestBuilders.post("/api/recipes/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(recipeDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.statusCode").value("201"))
                .andExpect(jsonPath("$.statusMsg").value("Recipe "+generatedString +" created successfully"))
                .andReturn();





        MvcResult recipeId=    mockMvc.perform(get("/api/recipes/user/{userId}", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].recipeType").value("VEGAN"))
                .andReturn();


        System.out.println("Process is the best:"+recipeId.getResponse().getContentAsString());

        String responseString = recipeId.getResponse().getContentAsString();
       // userId=JsonPath.parse(responseString).read("$.userId", Long.class);
        System.out.println("THE VALUE IS:"+responseString);
        Long recipevalue =JsonPath.parse(responseString).read("$[0].recipeId", Long.class);

        mockMvc.perform(get("/api/recipes/search")
                        .param("recipeType", "VEGAN"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].recipeType").value("VEGAN"));

        mockMvc.perform(delete("/api/recipes/delete/{id}", recipevalue))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusCode").value("200"))
                .andExpect(jsonPath("$.statusMsg").value("Request processed successfully"));


    }








}