package recipe.manager.recipemanager.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.ALREADY_REPORTED)
public class DuplicateResourceException extends RuntimeException{

    public DuplicateResourceException(String resourceName, String fieldName, String fieldValue) {
        super(String.format("%s Recipe already exists with the same name %s : '%s'", resourceName, fieldName, fieldValue));
    }
}
