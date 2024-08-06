package recipe.manager.recipemanager.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import recipe.manager.recipemanager.dto.IngredientDTO;
import recipe.manager.recipemanager.entity.Ingredient;

import java.util.List;

@Mapper(componentModel = "spring")
public interface IngredientMapper {


    IngredientMapper INSTANCE = Mappers.getMapper(IngredientMapper.class);

    IngredientDTO ingredientToIngredientDTO(Ingredient ingredient);
    List<IngredientDTO> ingredientToIngredientDTO(List<Ingredient> ingredient);

    List<Ingredient> ingredientDTOToReciIngredient(List<IngredientDTO> ingredientDto);
    Ingredient ingredientDTOToReciIngredient(IngredientDTO ingredientDto);
}
