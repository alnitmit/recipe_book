package by.nikita.recipebook.entity.dto;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class DtoCoverageTest {

    @Test
    void categoryDtoShouldCoverDataMethods() {
        CategoryDTO dto = new CategoryDTO();
        dto.setId(1L);
        dto.setName("Desserts");
        dto.setDescription("Sweet");

        CategoryDTO same = new CategoryDTO(1L, "Desserts", "Sweet");

        assertThat(dto).isEqualTo(same).hasSameHashCodeAs(same).hasToString(same.toString());
    }

    @Test
    void errorResponseShouldCoverDataMethods() {
        LocalDateTime now = LocalDateTime.now();
        ErrorResponse response = new ErrorResponse();
        response.setTimestamp(now);
        response.setStatus(400);
        response.setError("Bad Request");
        response.setMessage("Validation failed");
        response.setPath("/api/test");
        response.setDetails(Map.of("field", "must not be blank"));

        ErrorResponse same = new ErrorResponse(now, 400, "Bad Request", "Validation failed", "/api/test",
            Map.of("field", "must not be blank"));

        assertThat(response).isEqualTo(same).hasSameHashCodeAs(same).hasToString(same.toString());
    }

    @Test
    void ingredientDtoShouldCoverDataMethods() {
        IngredientDTO dto = new IngredientDTO();
        dto.setId(1L);
        dto.setName("Salt");
        dto.setQuantity("1 tsp");
        dto.setUnitId(2L);
        dto.setUnitName("gram");
        dto.setRecipeId(3L);

        IngredientDTO same = new IngredientDTO(1L, "Salt", "1 tsp", 2L, "gram", 3L);

        assertThat(dto).isEqualTo(same).hasSameHashCodeAs(same).hasToString(same.toString());
    }

    @Test
    void recipeDtoShouldCoverConstructorsAndDataMethods() {
        RecipeDTO dto = new RecipeDTO();
        dto.setId(1L);
        dto.setTitle("Soup");
        dto.setDescription("Warm");
        dto.setInstructions("Boil");
        dto.setCategoryId(2L);
        dto.setCategoryName("Dinner");
        dto.setAuthorId(3L);
        dto.setAuthorUsername("chef");
        dto.setTags(List.of(new TagDTO(4L, "quick")));
        dto.setIngredients(List.of(new IngredientDTO(5L, "Salt", "1 tsp", 6L, "gram", 1L)));

        RecipeDTO allArgs = new RecipeDTO(
            1L,
            "Soup",
            "Warm",
            "Boil",
            2L,
            "Dinner",
            3L,
            "chef",
            List.of(new TagDTO(4L, "quick")),
            List.of(new IngredientDTO(5L, "Salt", "1 tsp", 6L, "gram", 1L))
        );
        RecipeDTO shortcut = new RecipeDTO();
        shortcut.setId(1L);
        shortcut.setTitle("Soup");
        shortcut.setDescription("Warm");
        shortcut.setInstructions("Boil");
        shortcut.setCategoryId(2L);
        shortcut.setCategoryName("Dinner");
        shortcut.setAuthorId(3L);
        shortcut.setAuthorUsername("chef");

        assertThat(dto).isEqualTo(allArgs).hasSameHashCodeAs(allArgs).hasToString(allArgs.toString());
        assertThat(shortcut.getAuthorUsername()).isEqualTo("chef");
    }

    @Test
    void tagDtoShouldCoverDataMethods() {
        TagDTO dto = new TagDTO();
        dto.setId(1L);
        dto.setName("quick");

        TagDTO same = new TagDTO(1L, "quick");

        assertThat(dto).isEqualTo(same).hasSameHashCodeAs(same).hasToString(same.toString());
    }

    @Test
    void unitDtoShouldCoverDataMethods() {
        UnitDTO dto = new UnitDTO();
        dto.setId(1L);
        dto.setName("gram");
        dto.setAbbreviation("g");
        dto.setDescription("Metric");

        UnitDTO same = new UnitDTO(1L, "gram", "g", "Metric");

        assertThat(dto).isEqualTo(same).hasSameHashCodeAs(same).hasToString(same.toString());
    }

    @Test
    void userDtoShouldCoverDataMethods() {
        LocalDateTime now = LocalDateTime.now();
        UserDTO dto = new UserDTO();
        dto.setId(1L);
        dto.setUsername("chef");
        dto.setEmail("chef@example.com");
        dto.setCreatedAt(now);

        UserDTO same = new UserDTO(1L, "chef", "chef@example.com", now);

        assertThat(dto).isEqualTo(same).hasSameHashCodeAs(same).hasToString(same.toString());
    }
}
