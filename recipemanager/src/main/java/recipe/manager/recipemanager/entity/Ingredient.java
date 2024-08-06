package recipe.manager.recipemanager.entity;
import lombok.Data;
import jakarta.persistence.*;



@Entity
@Data
public class Ingredient {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)

    private Long ingredientId;
    private String ingredientName;

}
