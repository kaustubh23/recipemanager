package recipe.manager.recipemanager.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;
@Data
@Entity
public class RecipeStep {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long rsid;
    private String instructions;
    private Long recipeId;
}
