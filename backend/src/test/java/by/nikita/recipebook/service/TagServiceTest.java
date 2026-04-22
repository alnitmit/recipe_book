package by.nikita.recipebook.service;

import by.nikita.recipebook.entity.Tag;
import by.nikita.recipebook.entity.dto.TagDTO;
import by.nikita.recipebook.repository.RecipeRepository;
import by.nikita.recipebook.repository.TagRepository;
import by.nikita.recipebook.utils.TagMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.NoSuchElementException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TagServiceTest {

    @Mock
    private TagRepository tagRepository;

    @Mock
    private RecipeRepository recipeRepository;

    @Mock
    private TagMapper tagMapper;

    @InjectMocks
    private TagService tagService;

    @Test
    void createTagShouldFailWhenNameAlreadyExists() {
        TagDTO tagDto = new TagDTO();
        tagDto.setName("quick");

        when(tagRepository.findByName("quick")).thenReturn(Optional.of(new Tag()));

        assertThatThrownBy(() -> tagService.createTag(tagDto))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Тег с названием 'quick' уже существует");
    }

    @Test
    void updateTagShouldPersistNewName() {
        Tag existingTag = new Tag();
        existingTag.setId(2L);
        existingTag.setName("old");

        TagDTO tagDto = new TagDTO(2L, "new");
        Tag savedTag = new Tag();
        TagDTO expectedDto = new TagDTO(2L, "new");

        when(tagRepository.findById(2L)).thenReturn(Optional.of(existingTag));
        when(tagRepository.findByName("new")).thenReturn(Optional.empty());
        when(tagRepository.save(existingTag)).thenReturn(savedTag);
        when(tagMapper.toDto(savedTag)).thenReturn(expectedDto);

        TagDTO actual = tagService.updateTag(2L, tagDto);

        assertThat(actual).isSameAs(expectedDto);
        assertThat(existingTag.getName()).isEqualTo("new");
    }

    @Test
    void deleteTagShouldFailWhenTagDoesNotExist() {
        when(tagRepository.existsById(4L)).thenReturn(false);

        assertThatThrownBy(() -> tagService.deleteTag(4L))
            .isInstanceOf(NoSuchElementException.class)
            .hasMessage("Тег не найден, id: 4");
    }

    @Test
    void deleteTagShouldDeleteExistingTag() {
        when(tagRepository.existsById(4L)).thenReturn(true);
        when(recipeRepository.existsByTagsId(4L)).thenReturn(false);

        tagService.deleteTag(4L);

        verify(tagRepository).deleteById(4L);
    }

    @Test
    void deleteTagShouldFailWhenTagHasRecipes() {
        when(tagRepository.existsById(4L)).thenReturn(true);
        when(recipeRepository.existsByTagsId(4L)).thenReturn(true);

        assertThatThrownBy(() -> tagService.deleteTag(4L))
            .isInstanceOf(IllegalStateException.class)
            .hasMessage("Нельзя удалить тег, который используется в рецептах");
    }
}
