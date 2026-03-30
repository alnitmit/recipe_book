package by.nikita.recipebook.service;

import by.nikita.recipebook.entity.Category;
import by.nikita.recipebook.entity.Ingredient;
import by.nikita.recipebook.entity.Recipe;
import by.nikita.recipebook.entity.Tag;
import by.nikita.recipebook.entity.Unit;
import by.nikita.recipebook.entity.User;
import by.nikita.recipebook.entity.dto.CategoryDTO;
import by.nikita.recipebook.entity.dto.IngredientDTO;
import by.nikita.recipebook.entity.dto.RecipeDTO;
import by.nikita.recipebook.entity.dto.TagDTO;
import by.nikita.recipebook.entity.dto.UnitDTO;
import by.nikita.recipebook.entity.dto.UserDTO;
import by.nikita.recipebook.repository.CategoryRepository;
import by.nikita.recipebook.repository.IngredientRepository;
import by.nikita.recipebook.repository.RecipeRepository;
import by.nikita.recipebook.repository.TagRepository;
import by.nikita.recipebook.repository.UnitRepository;
import by.nikita.recipebook.repository.UserRepository;
import by.nikita.recipebook.utils.CategoryMapper;
import by.nikita.recipebook.utils.IngredientMapper;
import by.nikita.recipebook.utils.RecipeMapper;
import by.nikita.recipebook.utils.TagMapper;
import by.nikita.recipebook.utils.UnitMapper;
import by.nikita.recipebook.utils.UserMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.util.HashSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ServiceCoverageSupplementTest {

    @Mock
    private CategoryRepository categoryRepository;
    @Mock
    private RecipeRepository recipeRepository;
    @Mock
    private CategoryMapper categoryMapper;
    @InjectMocks
    private CategoryService categoryService;

    @Mock
    private UserRepository userRepository;
    @Mock
    private UserMapper userMapper;
    @InjectMocks
    private UserService userService;

    @Mock
    private TagRepository tagRepository;
    @Mock
    private TagMapper tagMapper;
    @InjectMocks
    private TagService tagService;

    @Mock
    private UnitRepository unitRepository;
    @Mock
    private UnitMapper unitMapper;
    @InjectMocks
    private UnitService unitService;

    @Mock
    private IngredientRepository ingredientRepository;
    @Mock
    private IngredientMapper ingredientMapper;
    @InjectMocks
    private IngredientService ingredientService;

    @Mock
    private RecipeMapper recipeMapper;
    @InjectMocks
    private RecipeService recipeService;

    @Test
    void categoryServiceShouldCoverSuccessReadAndDeleteNotFoundFlows() {
        CategoryDTO dto = new CategoryDTO(1L, "Desserts", "Sweet");
        Category category = new Category();
        Page<Category> entityPage = new PageImpl<>(List.of(category), PageRequest.of(0, 10), 1);

        when(categoryRepository.findByName("Desserts")).thenReturn(Optional.empty());
        when(categoryMapper.toEntity(dto)).thenReturn(category);
        when(categoryRepository.save(category)).thenReturn(category);
        when(categoryMapper.toDto(category)).thenReturn(dto);
        when(categoryRepository.findAll(PageRequest.of(0, 10))).thenReturn(entityPage);
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(categoryRepository.existsById(99L)).thenReturn(false);

        assertThat(categoryService.createCategory(dto)).isSameAs(dto);
        assertThat(categoryService.getAllCategories(PageRequest.of(0, 10)).getContent()).containsExactly(dto);
        assertThat(categoryService.getCategoryById(1L)).contains(dto);
        assertThatThrownBy(() -> categoryService.deleteCategory(99L))
            .isInstanceOf(NoSuchElementException.class)
            .hasMessage("Category not found with id: 99");
    }

    @Test
    void categoryServiceShouldFailWhenUpdatingToDuplicateName() {
        Category existing = new Category();
        existing.setId(1L);
        existing.setName("Old");
        CategoryDTO duplicateNameUpdate = new CategoryDTO(1L, "New", "Desc");

        when(categoryRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(categoryRepository.findByName("New")).thenReturn(Optional.of(new Category()));

        assertThatThrownBy(() -> categoryService.updateCategory(1L, duplicateNameUpdate))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Category with name 'New' already exists");
    }

    @Test
    void categoryServiceShouldAllowUpdateWhenNameDoesNotChange() {
        Category existing = new Category();
        existing.setId(1L);
        existing.setName("Same");
        CategoryDTO dto = new CategoryDTO(1L, "Same", "Updated");

        when(categoryRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(categoryRepository.save(existing)).thenReturn(existing);
        when(categoryMapper.toDto(existing)).thenReturn(dto);

        assertThat(categoryService.updateCategory(1L, dto)).isSameAs(dto);
    }

    @Test
    void userServiceShouldCoverSuccessReadUpdateAndDeleteFlows() {
        UserDTO dto = new UserDTO(1L, "chef", "chef@example.com", null);
        User user = new User();
        user.setId(1L);
        user.setUsername("chef");
        user.setEmail("chef@example.com");
        Page<User> entityPage = new PageImpl<>(List.of(user), PageRequest.of(0, 10), 1);

        when(userRepository.findByUsername("chef")).thenReturn(Optional.empty());
        when(userRepository.findByEmail("chef@example.com")).thenReturn(Optional.empty());
        when(userMapper.toEntity(dto)).thenReturn(user);
        when(userRepository.save(user)).thenReturn(user);
        when(userMapper.toDto(user)).thenReturn(dto);
        when(userRepository.findAll(PageRequest.of(0, 10))).thenReturn(entityPage);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.findByUsername("chef-updated")).thenReturn(Optional.empty());
        when(userRepository.findByEmail("new@example.com")).thenReturn(Optional.empty());
        when(userRepository.existsById(1L)).thenReturn(true);
        when(recipeRepository.existsByAuthorId(1L)).thenReturn(false);

        assertThat(userService.createUser(dto)).isSameAs(dto);
        assertThat(userService.getAllUsers(PageRequest.of(0, 10)).getContent()).containsExactly(dto);
        assertThat(userService.getUserById(1L)).contains(dto);
        assertThat(userService.updateUser(1L, new UserDTO(1L, "chef-updated", "new@example.com", null))).isSameAs(dto);
        userService.deleteUser(1L);

        verify(userRepository).deleteById(1L);
    }

    @Test
    void userServiceShouldFailOnDuplicateEmailAndUsernameDuringUpdateAndCreate() {
        User existing = new User();
        existing.setId(1L);
        existing.setUsername("old");
        existing.setEmail("old@example.com");

        when(userRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(userRepository.findByUsername("taken")).thenReturn(Optional.of(new User()));
        UserDTO duplicateUsernameUpdate = new UserDTO(1L, "taken", "new@example.com", null);

        assertThatThrownBy(() -> userService.updateUser(1L, duplicateUsernameUpdate))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("User with username 'taken' already exists");

        when(userRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(userRepository.findByEmail("taken@example.com")).thenReturn(Optional.of(new User()));
        UserDTO duplicateEmailUpdate = new UserDTO(1L, "old", "taken@example.com", null);
        assertThatThrownBy(() -> userService.updateUser(1L, duplicateEmailUpdate))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("User with email 'taken@example.com' already exists");

        when(userRepository.findByUsername("chef")).thenReturn(Optional.empty());
        when(userRepository.findByEmail("chef@example.com")).thenReturn(Optional.of(new User()));
        UserDTO duplicateCreate = new UserDTO(1L, "chef", "chef@example.com", null);
        assertThatThrownBy(() -> userService.createUser(duplicateCreate))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("User with email 'chef@example.com' already exists");
    }

    @Test
    void userServiceShouldHandleUpdateNotFoundAndUnchangedFields() {
        User existing = new User();
        existing.setId(1L);
        existing.setUsername("same");
        existing.setEmail("same@example.com");
        UserDTO dto = new UserDTO(1L, "same", "same@example.com", null);

        when(userRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(userRepository.save(existing)).thenReturn(existing);
        when(userMapper.toDto(existing)).thenReturn(dto);

        assertThat(userService.updateUser(1L, dto)).isSameAs(dto);

        when(userRepository.findById(99L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> userService.updateUser(99L, dto))
            .isInstanceOf(NoSuchElementException.class)
            .hasMessage("User not found with id: 99");
    }

    @Test
    void tagServiceShouldCoverSuccessAndFailureFlows() {
        TagDTO dto = new TagDTO(1L, "quick");
        Tag tag = new Tag();
        tag.setId(1L);
        tag.setName("quick");
        Page<Tag> entityPage = new PageImpl<>(List.of(tag), PageRequest.of(0, 10), 1);

        when(tagRepository.findByName("quick")).thenReturn(Optional.empty());
        when(tagMapper.toEntity(dto)).thenReturn(tag);
        when(tagRepository.save(tag)).thenReturn(tag);
        when(tagMapper.toDto(tag)).thenReturn(dto);
        when(tagRepository.findAll(PageRequest.of(0, 10))).thenReturn(entityPage);
        when(tagRepository.findById(1L)).thenReturn(Optional.of(tag));
        when(tagRepository.existsById(1L)).thenReturn(true);

        assertThat(tagService.createTag(dto)).isSameAs(dto);
        assertThat(tagService.getAllTags(PageRequest.of(0, 10)).getContent()).containsExactly(dto);
        assertThat(tagService.getTagById(1L)).contains(dto);
        assertThat(tagService.updateTag(1L, dto)).isSameAs(dto);
        tagService.deleteTag(1L);

        when(tagRepository.findByName("new")).thenReturn(Optional.of(new Tag()));
        TagDTO duplicateTagUpdate = new TagDTO(1L, "new");
        assertThatThrownBy(() -> tagService.updateTag(1L, duplicateTagUpdate))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Tag with name 'new' already exists");

        when(tagRepository.existsById(7L)).thenReturn(false);
        assertThatThrownBy(() -> tagService.deleteTag(7L))
            .isInstanceOf(NoSuchElementException.class)
            .hasMessage("Tag not found with id: 7");
    }

    @Test
    void tagServiceShouldFailWhenUpdatingMissingTag() {
        when(tagRepository.findById(99L)).thenReturn(Optional.empty());
        TagDTO missingTag = new TagDTO(99L, "missing");

        assertThatThrownBy(() -> tagService.updateTag(99L, missingTag))
            .isInstanceOf(NoSuchElementException.class)
            .hasMessage("Tag not found with id: 99");
    }

    @Test
    void unitServiceShouldCoverSuccessAndFailureFlows() {
        UnitDTO dto = new UnitDTO(1L, "gram", "g", "Metric");
        Unit unit = new Unit(1L, "gram", "g", "Metric");
        Page<Unit> entityPage = new PageImpl<>(List.of(unit), PageRequest.of(0, 10), 1);

        when(unitRepository.findByName("gram")).thenReturn(Optional.empty());
        when(unitMapper.toEntity(dto)).thenReturn(unit);
        when(unitRepository.save(unit)).thenReturn(unit);
        when(unitMapper.toDto(unit)).thenReturn(dto);
        when(unitRepository.findAll(PageRequest.of(0, 10))).thenReturn(entityPage);
        when(unitRepository.findById(1L)).thenReturn(Optional.of(unit));
        when(unitRepository.existsById(1L)).thenReturn(true);

        assertThat(unitService.createUnit(dto)).isSameAs(dto);
        assertThat(unitService.getAllUnits(PageRequest.of(0, 10)).getContent()).containsExactly(dto);
        assertThat(unitService.getUnitById(1L)).contains(dto);
        assertThat(unitService.updateUnit(1L, dto)).isSameAs(dto);
        unitService.deleteUnit(1L);

        when(unitRepository.findByName("new")).thenReturn(Optional.of(new Unit()));
        UnitDTO duplicateUnitUpdate = new UnitDTO(1L, "new", "n", "desc");
        assertThatThrownBy(() -> unitService.updateUnit(1L, duplicateUnitUpdate))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Unit with name 'new' already exists");

        when(unitRepository.existsById(3L)).thenReturn(false);
        assertThatThrownBy(() -> unitService.deleteUnit(3L))
            .isInstanceOf(NoSuchElementException.class)
            .hasMessage("Unit not found");
    }

    @Test
    void ingredientServiceShouldCoverReadUpdateDeleteAndFailureBranches() {
        Recipe recipe = new Recipe();
        recipe.setId(1L);
        Unit unit = new Unit(2L, "gram", "g", "Metric");
        Ingredient ingredient = new Ingredient(3L, "Salt", "1 tsp", unit, recipe);
        IngredientDTO dto = new IngredientDTO(3L, "Salt", "1 tsp", 2L, "gram", 1L);
        Page<Ingredient> entityPage = new PageImpl<>(List.of(ingredient), PageRequest.of(0, 10), 1);

        when(recipeRepository.findById(1L)).thenReturn(Optional.of(recipe));
        when(unitRepository.findById(2L)).thenReturn(Optional.of(unit));
        when(ingredientMapper.toEntity(dto, recipe, unit)).thenReturn(ingredient);
        when(ingredientRepository.save(ingredient)).thenReturn(ingredient);
        when(ingredientMapper.toDto(ingredient)).thenReturn(dto);
        when(ingredientRepository.findAll(PageRequest.of(0, 10))).thenReturn(entityPage);
        when(ingredientRepository.findById(3L)).thenReturn(Optional.of(ingredient));
        when(ingredientRepository.findByRecipeId(1L, PageRequest.of(0, 10))).thenReturn(entityPage);
        when(ingredientRepository.existsById(3L)).thenReturn(true);

        assertThat(ingredientService.createIngredient(dto)).isSameAs(dto);
        assertThat(ingredientService.getAllIngredients(PageRequest.of(0, 10)).getContent()).containsExactly(dto);
        assertThat(ingredientService.getIngredientById(3L)).contains(dto);
        assertThat(ingredientService.getIngredientsByRecipe(1L, PageRequest.of(0, 10)).getContent())
            .containsExactly(dto);
        assertThat(ingredientService.updateIngredient(3L, dto)).isSameAs(dto);
        ingredientService.deleteIngredient(3L);

        Ingredient noUnitIngredient = new Ingredient();
        noUnitIngredient.setId(3L);
        noUnitIngredient.setRecipe(recipe);
        when(ingredientRepository.findById(3L)).thenReturn(Optional.of(noUnitIngredient));
        when(ingredientRepository.save(any(Ingredient.class))).thenReturn(noUnitIngredient);
        when(ingredientMapper.toDto(noUnitIngredient)).thenReturn(new IngredientDTO(3L, "Water", "100 ml", null, null, 1L));
        assertThat(ingredientService.updateIngredient(3L, new IngredientDTO(3L, "Water", "100 ml", null, null, 1L)))
            .extracting(IngredientDTO::getName)
            .isEqualTo("Water");

        Recipe newRecipe = new Recipe();
        newRecipe.setId(2L);
        when(recipeRepository.findById(2L)).thenReturn(Optional.of(newRecipe));
        when(ingredientMapper.toDto(noUnitIngredient)).thenReturn(new IngredientDTO(3L, "Salt", "1 tsp", null, null, 2L));
        ingredientService.updateIngredient(3L, new IngredientDTO(3L, "Salt", "1 tsp", null, null, 2L));
        assertThat(noUnitIngredient.getRecipe()).isSameAs(newRecipe);

        when(ingredientRepository.existsById(8L)).thenReturn(false);
        assertThatThrownBy(() -> ingredientService.deleteIngredient(8L))
            .isInstanceOf(NoSuchElementException.class)
            .hasMessage("Ingredient not found");

        IngredientDTO bulkMissingUnit = new IngredientDTO(null, "Salt", "1 tsp", 99L, null, 1L);
        List<IngredientDTO> missingUnitBulk = List.of(bulkMissingUnit);
        when(recipeRepository.findById(1L)).thenReturn(Optional.of(recipe));
        when(unitRepository.findById(99L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> ingredientService.createIngredientsBulk(missingUnitBulk))
            .isInstanceOf(NoSuchElementException.class)
            .hasMessage("Unit not found with id: 99");

        IngredientDTO createMissingUnit = new IngredientDTO(null, "Sugar", "1 tsp", 88L, null, 1L);
        when(recipeRepository.findById(1L)).thenReturn(Optional.of(recipe));
        when(unitRepository.findById(88L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> ingredientService.createIngredient(createMissingUnit))
            .isInstanceOf(NoSuchElementException.class)
            .hasMessage("Unit not found with id: 88");

        IngredientDTO bulkMissingRecipe = new IngredientDTO(null, "Salt", "1 tsp", null, null, 123L);
        List<IngredientDTO> missingRecipeBulk = List.of(bulkMissingRecipe);
        when(recipeRepository.findById(123L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> ingredientService.createIngredientsBulk(missingRecipeBulk))
            .isInstanceOf(NoSuchElementException.class)
            .hasMessage("Recipe not found with id: 123");
    }

    @Test
    void ingredientServiceShouldCoverEmptyNullAndNoUnitBranches() {
        Recipe recipe = new Recipe();
        recipe.setId(1L);
        Ingredient ingredient = new Ingredient();
        ingredient.setId(10L);
        ingredient.setRecipe(recipe);
        IngredientDTO createWithoutUnit = new IngredientDTO(10L, "Water", "100 ml", null, null, 1L);
        IngredientDTO updateWithoutRecipeId = new IngredientDTO(10L, "Juice", "150 ml", null, null, null);

        when(recipeRepository.findById(1L)).thenReturn(Optional.of(recipe));
        when(ingredientMapper.toEntity(createWithoutUnit, recipe, null)).thenReturn(ingredient);
        when(ingredientRepository.save(ingredient)).thenReturn(ingredient);
        when(ingredientMapper.toDto(ingredient)).thenReturn(createWithoutUnit);
        when(ingredientRepository.findById(10L)).thenReturn(Optional.of(ingredient));

        assertThat(ingredientService.createIngredient(createWithoutUnit)).isSameAs(createWithoutUnit);
        assertThat(ingredientService.updateIngredient(10L, createWithoutUnit)).isSameAs(createWithoutUnit);
        when(ingredientMapper.toDto(ingredient)).thenReturn(updateWithoutRecipeId);
        assertThat(ingredientService.updateIngredient(10L, updateWithoutRecipeId)).isSameAs(updateWithoutRecipeId);
        List<IngredientDTO> emptyBulk = List.of();
        assertThatThrownBy(() -> ingredientService.createIngredientsBulk(null))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Ingredient list must not be empty");
        assertThatThrownBy(() -> ingredientService.createIngredientsBulk(emptyBulk))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Ingredient list must not be empty");
    }

    @Test
    void ingredientServiceShouldCoverBulkSuccessAndMissingIngredientUpdate() {
        Recipe recipe = new Recipe();
        recipe.setId(1L);
        Unit unit = new Unit(2L, "gram", "g", "Metric");
        Ingredient withUnit = new Ingredient(11L, "Salt", "1 tsp", unit, recipe);
        Ingredient withoutUnit = new Ingredient(12L, "Water", "100 ml", null, recipe);
        IngredientDTO withUnitDto = new IngredientDTO(11L, "Salt", "1 tsp", 2L, "gram", 1L);
        IngredientDTO withoutUnitDto = new IngredientDTO(12L, "Water", "100 ml", null, null, 1L);

        when(recipeRepository.findById(1L)).thenReturn(Optional.of(recipe));
        when(unitRepository.findById(2L)).thenReturn(Optional.of(unit));
        when(ingredientMapper.toEntity(withUnitDto, recipe, unit)).thenReturn(withUnit);
        when(ingredientMapper.toEntity(withoutUnitDto, recipe, null)).thenReturn(withoutUnit);
        when(ingredientRepository.saveAll(List.of(withUnit, withoutUnit))).thenReturn(List.of(withUnit, withoutUnit));
        when(ingredientMapper.toDto(withUnit)).thenReturn(withUnitDto);
        when(ingredientMapper.toDto(withoutUnit)).thenReturn(withoutUnitDto);
        when(ingredientRepository.findById(999L)).thenReturn(Optional.empty());

        assertThat(ingredientService.createIngredientsBulk(List.of(withUnitDto, withoutUnitDto)))
            .containsExactly(withUnitDto, withoutUnitDto);
        assertThatThrownBy(() -> ingredientService.updateIngredient(999L, withUnitDto))
            .isInstanceOf(NoSuchElementException.class)
            .hasMessage("Ingredient not found");
    }

    @Test
    void recipeServiceShouldCoverReadDeleteSearchCacheAndRelationBranches() {
        Recipe recipe = new Recipe();
        recipe.setId(1L);
        recipe.setTags(new HashSet<>());
        RecipeDTO dto = new RecipeDTO();
        dto.setId(1L);
        dto.setTitle("Soup");
        dto.setDescription("Warm");
        dto.setInstructions("Boil");
        Page<Recipe> entityPage = new PageImpl<>(List.of(recipe), PageRequest.of(0, 10), 1);

        when(recipeMapper.toEntity(dto)).thenReturn(recipe);
        when(recipeRepository.save(recipe)).thenReturn(recipe);
        when(recipeMapper.toDto(recipe)).thenReturn(dto);
        when(recipeRepository.findAll(PageRequest.of(0, 10))).thenReturn(entityPage);
        when(recipeRepository.findById(1L)).thenReturn(Optional.of(recipe));
        when(recipeRepository.existsById(1L)).thenReturn(true);
        when(recipeRepository.findRecipeIdsByFiltersJPQL("none", 1L, PageRequest.of(0, 10)))
            .thenReturn(Page.empty(PageRequest.of(0, 10)));

        assertThat(recipeService.createRecipe(dto)).isSameAs(dto);
        assertThat(recipeService.getAllRecipes(PageRequest.of(0, 10)).getContent()).containsExactly(dto);
        assertThat(recipeService.getRecipeById(1L)).contains(dto);
        assertThat(recipeService.searchRecipesJPQL("none", 1L, PageRequest.of(0, 10))).isEmpty();
        recipeService.deleteRecipe(1L);

        PageRequest pageable = PageRequest.of(0, 2);
        Page<Long> idPage = new PageImpl<>(List.of(5L, 2L), pageable, 2);
        Recipe first = new Recipe();
        first.setId(2L);
        Recipe second = new Recipe();
        second.setId(5L);
        RecipeDTO firstDto = new RecipeDTO();
        firstDto.setId(5L);
        RecipeDTO secondDto = new RecipeDTO();
        secondDto.setId(2L);

        when(recipeRepository.findRecipeIdsByFiltersJPQL("Dinner", 1L, pageable)).thenReturn(idPage);
        when(recipeRepository.findByIdIn(List.of(5L, 2L), Sort.by(Sort.Direction.ASC, "id")))
            .thenReturn(List.of(first, second));
        when(recipeMapper.toDto(second)).thenReturn(firstDto);
        when(recipeMapper.toDto(first)).thenReturn(secondDto);

        assertThat(recipeService.searchRecipesJPQL("Dinner", 1L, pageable).getContent())
            .containsExactly(firstDto, secondDto);
        assertThat(recipeService.searchRecipesJPQL("Dinner", 1L, pageable).getContent())
            .containsExactly(firstDto, secondDto);
        verify(recipeRepository, times(1)).findRecipeIdsByFiltersJPQL("Dinner", 1L, pageable);

        RecipeDTO relatedDto = new RecipeDTO();
        relatedDto.setCategoryId(10L);
        relatedDto.setAuthorId(20L);
        relatedDto.setTags(List.of(new TagDTO(30L, "quick")));

        Category category = new Category();
        category.setId(10L);
        User author = new User();
        author.setId(20L);
        Tag tag = new Tag();
        tag.setId(30L);

        when(recipeMapper.toEntity(relatedDto)).thenReturn(recipe);
        when(categoryRepository.findById(10L)).thenReturn(Optional.of(category));
        when(userRepository.findById(20L)).thenReturn(Optional.of(author));
        when(tagRepository.findById(30L)).thenReturn(Optional.of(tag));
        when(recipeRepository.save(recipe)).thenReturn(recipe);
        when(recipeMapper.toDto(recipe)).thenReturn(relatedDto);

        assertThat(recipeService.createRecipe(relatedDto)).isSameAs(relatedDto);

        when(recipeRepository.findById(2L)).thenReturn(Optional.of(new Recipe()));
        RecipeDTO missingTagId = new RecipeDTO();
        missingTagId.setTags(List.of(new TagDTO(null, "broken")));
        assertThatThrownBy(() -> recipeService.updateRecipe(2L, missingTagId))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Each tag must contain an id");

        when(recipeRepository.existsById(99L)).thenReturn(false);
        assertThatThrownBy(() -> recipeService.deleteRecipe(99L))
            .isInstanceOf(NoSuchElementException.class)
            .hasMessage("Recipe not found with id: 99");

        when(recipeMapper.toEntity(relatedDto)).thenReturn(recipe);
        when(categoryRepository.findById(10L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> recipeService.createRecipe(relatedDto))
            .isInstanceOf(NoSuchElementException.class)
            .hasMessage("Category not found with id: 10");

        when(recipeRepository.findById(404L)).thenReturn(Optional.empty());
        RecipeDTO recipeDto = new RecipeDTO();
        assertThatThrownBy(() -> recipeService.updateRecipe(404L, recipeDto))
            .isInstanceOf(NoSuchElementException.class)
            .hasMessage("Recipe not found with id: 404");

        when(recipeMapper.toEntity(relatedDto)).thenReturn(recipe);
        when(categoryRepository.findById(10L)).thenReturn(Optional.of(category));
        when(userRepository.findById(20L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> recipeService.createRecipe(relatedDto))
            .isInstanceOf(NoSuchElementException.class)
            .hasMessage("User not found with id: 20");

        when(recipeMapper.toEntity(relatedDto)).thenReturn(recipe);
        when(userRepository.findById(20L)).thenReturn(Optional.of(author));
        when(tagRepository.findById(30L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> recipeService.createRecipe(relatedDto))
            .isInstanceOf(NoSuchElementException.class)
            .hasMessage("Tag not found with id: 30");
    }

    @Test
    void recipeServiceShouldCoverSortedSearchAndUpdateWithoutRelations() {
        Recipe recipe = new Recipe();
        recipe.setId(7L);
        recipe.setTags(new HashSet<>());
        RecipeDTO dto = new RecipeDTO();
        dto.setId(7L);
        dto.setTitle("Plain");
        dto.setDescription("Simple");
        dto.setInstructions("Mix");

        PageRequest sortedPageable = PageRequest.of(0, 5, Sort.by(Sort.Direction.DESC, "title"));
        Page<Long> idPage = new PageImpl<>(List.of(7L), sortedPageable, 1);

        when(recipeRepository.findRecipeIdsByFiltersJPQL("Simple", 0L, sortedPageable)).thenReturn(idPage);
        when(recipeRepository.findByIdIn(List.of(7L), sortedPageable.getSort())).thenReturn(List.of(recipe));
        when(recipeMapper.toDto(recipe)).thenReturn(dto);
        when(recipeRepository.findById(7L)).thenReturn(Optional.of(recipe));
        when(recipeRepository.save(recipe)).thenReturn(recipe);

        assertThat(recipeService.searchRecipesJPQL("Simple", 0L, sortedPageable).getContent()).containsExactly(dto);
        assertThat(recipeService.updateRecipe(7L, dto)).isSameAs(dto);
    }
}
