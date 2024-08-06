package recipe.manager.recipemanager.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import recipe.manager.recipemanager.dto.RecipeDTO;
import recipe.manager.recipemanager.dto.RecipeStepDTO;
import recipe.manager.recipemanager.entity.Recipe;
import recipe.manager.recipemanager.entity.RecipeStep;

import java.util.List;

@Mapper(componentModel = "spring")
public interface RecipeStepMapper {


    RecipeStepMapper INSTANCE = Mappers.getMapper(RecipeStepMapper.class);

    RecipeStepDTO recipeStepToRecipeStepDTO(RecipeStep recipe);


    RecipeStep recipeStepDTOToRecipe(RecipeStepDTO recipestepDto);

    /*List<RecipeStepDTO> recipeStepToRecipeStepDTO(List<RecipeStep> recipe);


    List<RecipeStep> recipeStepDTOToRecipeStep(List<RecipeStepDTO> recipestepDto);*/
}

