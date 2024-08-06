package recipe.manager.recipemanager.mapper;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import recipe.manager.recipemanager.dto.RecipeDTO;
import recipe.manager.recipemanager.entity.Recipe;

@Mapper(componentModel = "spring")
public interface RecipeMapper {

    RecipeMapper INSTANCE = Mappers.getMapper(RecipeMapper.class);

    RecipeDTO recipeToRecipeDTO(Recipe recipe);


    Recipe recipeDTOToRecipe(RecipeDTO recipeDto);
}
