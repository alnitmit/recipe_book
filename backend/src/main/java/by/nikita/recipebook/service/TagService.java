package by.nikita.recipebook.service;

import by.nikita.recipebook.entity.Tag;
import by.nikita.recipebook.entity.dto.TagDTO;
import by.nikita.recipebook.repository.RecipeRepository;
import by.nikita.recipebook.repository.TagRepository;
import by.nikita.recipebook.utils.TagMapper;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;
import java.util.Optional;

@Service
@AllArgsConstructor
public class TagService {

    private final TagRepository tagRepository;
    private final RecipeRepository recipeRepository;
    private final TagMapper tagMapper;

    @Transactional
    public TagDTO createTag(TagDTO tagDTO) {
        tagRepository.findByName(tagDTO.getName())
            .ifPresent(tag -> {
                throw new IllegalArgumentException("Тег с названием '" + tagDTO.getName() + "' уже существует");
            });

        Tag tag = tagMapper.toEntity(tagDTO);
        tag.setId(null);
        Tag savedTag = tagRepository.save(tag);
        return tagMapper.toDto(savedTag);
    }

    @Transactional(readOnly = true)
    public Page<TagDTO> getAllTags(Pageable pageable) {
        return tagRepository.findAll(pageable).map(tagMapper::toDto);
    }

    @Transactional(readOnly = true)
    public Optional<TagDTO> getTagById(Long id) {
        return tagRepository.findById(id).map(tagMapper::toDto);
    }

    @Transactional
    public TagDTO updateTag(Long id, TagDTO tagDTO) {
        Tag tag = tagRepository.findById(id)
            .orElseThrow(() -> new NoSuchElementException("Тег не найден, id: " + id));

        Optional.ofNullable(tagDTO.getName())
            .filter(name -> !name.equals(tag.getName()))
            .flatMap(tagRepository::findByName)
            .ifPresent(existingTag -> {
                throw new IllegalArgumentException("Тег с названием '" + tagDTO.getName() + "' уже существует");
            });

        tag.setName(tagDTO.getName());

        Tag updatedTag = tagRepository.save(tag);
        return tagMapper.toDto(updatedTag);
    }

    @Transactional
    public void deleteTag(Long id) {
        if (!tagRepository.existsById(id)) {
            throw new NoSuchElementException("Тег не найден, id: " + id);
        }

        if (recipeRepository.existsByTagsId(id)) {
            throw new IllegalStateException("Нельзя удалить тег, который используется в рецептах");
        }

        tagRepository.deleteById(id);
    }
}
