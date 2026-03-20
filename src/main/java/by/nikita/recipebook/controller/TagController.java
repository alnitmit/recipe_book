package by.nikita.recipebook.controller;

import by.nikita.recipebook.entity.dto.TagDTO;
import by.nikita.recipebook.service.TagService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api/tags")
@Tag(name = "Tag", description = "Tag management endpoints")
public class TagController {

    private final TagService tagService;

    public TagController(TagService tagService) {
        this.tagService = tagService;
    }

    @PostMapping
    @Operation(summary = "Create a new tag", description = "Creates a tag and returns the created object")
    @ApiResponses(value = {@ApiResponse(responseCode = "201", description = "Tag successfully created"),
        @ApiResponse(responseCode = "400", description = "Invalid input data")})
    public ResponseEntity<TagDTO> createTag(@Valid @RequestBody TagDTO tagDTO) {
        TagDTO createdTag = tagService.createTag(tagDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdTag);
    }

    @GetMapping
    @Operation(summary = "Get all tags", description = "Returns a paginated list of tags")
    @ApiResponse(responseCode = "200", description = "Successful operation")
    public ResponseEntity<Page<TagDTO>> getAllTags(@PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(tagService.getAllTags(pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get tag by ID", description = "Returns a single tag")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Successfully retrieved"),
        @ApiResponse(responseCode = "404", description = "Tag not found")})
    public ResponseEntity<TagDTO> getTagById(@PathVariable Long id) {
        TagDTO tag = tagService.getTagById(id)
            .orElseThrow(() -> new NoSuchElementException("Tag not found with id: " + id));
        return ResponseEntity.ok(tag);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing tag", description = "Updates tag data")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Successfully updated"),
        @ApiResponse(responseCode = "400", description = "Invalid input"),
        @ApiResponse(responseCode = "404", description = "Tag not found")})
    public ResponseEntity<TagDTO> updateTag(@PathVariable Long id, @Valid @RequestBody TagDTO tagDTO) {
        TagDTO updatedTag = tagService.updateTag(id, tagDTO);
        return ResponseEntity.ok(updatedTag);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a tag", description = "Deletes a tag by ID")
    @ApiResponses(value = {@ApiResponse(responseCode = "204", description = "Successfully deleted"),
        @ApiResponse(responseCode = "404", description = "Tag not found")})
    public ResponseEntity<Void> deleteTag(@PathVariable Long id) {
        tagService.deleteTag(id);
        return ResponseEntity.noContent().build();
    }
}
