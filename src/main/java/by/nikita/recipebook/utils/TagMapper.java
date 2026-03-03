package by.nikita.recipebook.utils;

import by.nikita.recipebook.entity.Tag;
import by.nikita.recipebook.entity.dto.TagDTO;
import org.springframework.stereotype.Component;

@Component
public class TagMapper {

    public TagDTO toDto(Tag tag) {
        if (tag == null) {
            return null;
        }

        TagDTO dto = new TagDTO();
        dto.setId(tag.getId());
        dto.setName(tag.getName());

        return dto;
    }

    public Tag toEntity(TagDTO dto) {
        if (dto == null) {
            return null;
        }

        Tag tag = new Tag();
        tag.setId(dto.getId());
        tag.setName(dto.getName());

        return tag;
    }
}