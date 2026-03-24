package by.nikita.recipebook.service;

import by.nikita.recipebook.entity.Recipe;
import by.nikita.recipebook.entity.User;
import by.nikita.recipebook.entity.dto.UserDTO;
import by.nikita.recipebook.repository.UserRepository;
import by.nikita.recipebook.utils.UserMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

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
    void deleteUserShouldFailWhenUserHasRecipes() {
        User user = new User();
        user.setId(1L);
        user.setRecipes(List.of(new Recipe()));

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        assertThatThrownBy(() -> userService.deleteUser(1L))
            .isInstanceOf(IllegalStateException.class)
            .hasMessage("Cannot delete user with existing recipes");
    }

    @Test
    void deleteUserShouldFailWhenUserDoesNotExist() {
        when(userRepository.findById(4L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.deleteUser(4L))
            .isInstanceOf(NoSuchElementException.class)
            .hasMessage("User not found with id: 4");
    }
}
