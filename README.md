# Recipe Manager

## Overview

The Recipe Manager application allows users to create, update, search, and delete recipes. It supports functionalities such as managing ingredients, recipe instructions, and user profiles. This README provides details on the architectural choices, setup, and running the application.

## Table of Contents

- [Architectural Choices](#architectural-choices)
- [Running the Application](#running-the-application)
- [API Endpoints](#api-endpoints)
- [Testing](#testing)
- [Contributing](#contributing)
- [License](#license)

## Architectural Choices

### 1. **Architecture**

The Recipe Manager application follows a layered architecture with the following components:

- **Controller Layer:** Manages HTTP requests and responses. It uses Spring Boot's `@RestController` to define API endpoints.

- **Service Layer:** Contains business logic and service methods. This layer is responsible for interacting with the repository layer and processing data.

- **Repository Layer:** Uses Spring Data JPA repositories to handle database operations. It abstracts the data access layer and manages entity persistence.

- **Entity Layer:** Defines the data model and mappings to the database. This includes entities such as `Recipe`, `Ingredient`, `RecipeStep`, and `User`.

### 2. **Database Design**

- **Recipe:** Represents a recipe with attributes such as `name`, `recipeType`, `servings`, and `userId`.
- **Ingredient:** Represents ingredients used in recipes.
- **RecipeIngredient:** A many-to-many relationship between `Recipe` and `Ingredient`.
- **RecipeStep:** Contains instructions for each recipe.
- **User:** Represents users of the application.

### 3. **Data Relationships**

- **One-to-Many:** `Recipe` to `RecipeStep`.
- **Many-to-Many:** `Recipe` to `Ingredient` through `RecipeIngredient`.

### 4. **Technologies Used**

- **Spring Boot:** Framework for building the application.
- **Spring Data JPA:** For ORM and database interaction.
- **H2 Database (for testing):** In-memory database for testing purposes.
- **JUnit & Mockito:** For unit and integration testing.
- **Swagger:** For API documentation and testing.

### 4. ** Other**

- **Separate table for ingredients:** To seperate the concerns and reusability.

User Management:

Create a new user
Update existing user information
Delete a user
Retrieve user details
Recipe Management:

Create a new recipe
Update an existing recipe
Delete a recipe
Retrieve recipe details
To update a recipe, the consumer must first fetch the recipe using the associated userId. This ensures that only the fields requiring modification are presented to the end-user. Certain fields are restricted and cannot be updated.

## Running the Application

### 1. **Prerequisites**

Ensure you have the following installed:

- **Java 21 or later**
- **Maven 3.6 or later**

### 2. **Clone the Repository**

```sh
git clone https://github.com/yourusername/recipe-manager.git
cd recipe-manager
```

### 3. **Build the Application**

```sh
mvn clean install
```

### 4. **Run the Application**

To run the application, use the following command:

```sh
mvn spring-boot:run
```
```sh
Docker image command
mvn spring-boot:build-image
```

The application will start on `http://localhost:8080`.

### 5. **Access Swagger Documentation**

Once the application is running, you can access the Swagger API documentation at:

```
http://localhost:8080/swagger-ui.html
```

## API Endpoints

### 1. **Recipe Endpoints**

- **Create Recipe**
  - `POST /api/recipes/create`
  - Request Body: `RecipeDTO`
  - Response: Recipe creation status

- **Get Recipe by ID**
  - `GET /api/recipes/get/{id}`
  - Response: `RecipeDTO` for the given `id`

- **Update Recipe**
  - `PUT /api/recipes/update/{id}`
  - Request Body: `RecipeDTO`
  - Response: Updated recipe status

- **Delete Recipe**
  - `DELETE /api/recipes/delete/{id}`
  - Response: Deletion status

- **Search Recipes**
  - `GET /api/recipes/search`
  - Query Parameters: `recipeType`, `servings`, `includeIngredients`, `excludeIngredients`, `instructions`
  - Response: List of `RecipeDTO` matching the search criteria

- **Get Recipes by User ID**
  - `GET /api/recipes/user/{userId}`
  - Response: List of `RecipeDTO` for the given `userId`

### 2. **User Endpoints**

- **Create User**
  - `POST /api/users/add`
  - Request Body: `User`
  - Response: Created `User`

- **Get User by ID**
  - `GET /api/users/{id}`
  - Response: `User` for the given `id`

- **Delete User**
  - `DELETE /api/users/{id}`
  - Response: Deletion status

- **Get All Users**
  - `GET /api/users/all`
  - Response: List of all `User` entities

## Testing

### 1. **Unit Tests**

Run unit tests using:

```sh
mvn test
```

### 2. **Integration Tests**

Integration tests can be run alongside unit tests:

```sh
mvn verify
```

### Installation

1. Clone the repository:
    ```bash
    git clone
    ```

2. Build the project:
    ```bash
    mvn clean install
    ```
3. Run the application:
    ```bash
    mvn spring-boot:run
    ```
4. H2 Database URL:
    ```bash
    http://localhost:8080/h2-console
    ```
5. Swagger Documentation:
    ```bash
    http://localhost:8080/swagger-ui/index.html
    ```


## Remaining Tasks
1. Security implementation: Security implementation and also user's password field encryption/decryption.

2. Central configuration: Application profileing

3. Monitoring and deep code cleaning and make it more readable.

4. Devide instructions to multiple steps and the show them using paging to enhance performance.

5. Service layer abstraction

6. Recipe search currently works on 'OR' operation, can be enhanced for 'AND' operation
7.  Replace manual caching with Redis or equivalent
