package recipe.manager.recipemanager.entity;

import lombok.Data;
import jakarta.persistence.*;

import java.util.List;

@Entity
@Data
@Table(name = "users") // Specify a different table name
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    private String name;

    private String password;;


}