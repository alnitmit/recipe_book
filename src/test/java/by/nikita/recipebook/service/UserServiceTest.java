package by.nikita.recipebook.service;

import by.nikita.recipebook.entity.User;
import by.nikita.recipebook.entity.dto.UserDTO;
import by.nikita.recipebook.repository.RecipeRepository;
import by.nikita.recipebook.repository.UserRepository;
import by.nikita.recipebook.utils.UserMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.NoSuchElementException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RecipeRepository recipeRepository;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserService userService;

    @Test
    void createUserShouldFailWhenUsernameAlreadyExists() {
        UserDTO userDto = new UserDTO();
        userDto.setUsername("chef");

        when(userRepository.findByUsername("chef")).thenReturn(Optional.of(new User()));

        assertThatThrownBy(() -> userService.createUser(userDto))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("User with username 'chef' already exists");
    }

    @Test
    void createUserShouldIgnoreClientProvidedIdAndCreatedAt() {
        UserDTO userDto = new UserDTO(1L, "chef", "chef@example.com", java.time.LocalDateTime.now());
        User user = new User();
        user.setId(1L);
        user.setCreatedAt(userDto.getCreatedAt());

        when(userRepository.findByUsername("chef")).thenReturn(Optional.empty());
        when(userRepository.findByEmail("chef@example.com")).thenReturn(Optional.empty());
        when(userMapper.toEntity(userDto)).thenReturn(user);
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(userMapper.toDto(any(User.class))).thenReturn(userDto);

        userService.createUser(userDto);

        assertThat(user.getId()).isNull();
        assertThat(user.getCreatedAt()).isNull();
        verify(userRepository).save(user);
    }

    @Test
    void deleteUserShouldFailWhenUserHasRecipes() {
        when(userRepository.existsById(1L)).thenReturn(true);
        when(recipeRepository.existsByAuthorId(1L)).thenReturn(true);

        assertThatThrownBy(() -> userService.deleteUser(1L))
            .isInstanceOf(IllegalStateException.class)
            .hasMessage("Cannot delete user with existing recipes");
    }

    @Test
    void deleteUserShouldFailWhenUserDoesNotExist() {
        when(userRepository.existsById(4L)).thenReturn(false);

        assertThatThrownBy(() -> userService.deleteUser(4L))
            .isInstanceOf(NoSuchElementException.class)
            .hasMessage("User not found with id: 4");
    }
}
