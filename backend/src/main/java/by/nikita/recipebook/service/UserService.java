package by.nikita.recipebook.service;

import by.nikita.recipebook.entity.User;
import by.nikita.recipebook.entity.dto.UserDTO;
import by.nikita.recipebook.repository.RecipeRepository;
import by.nikita.recipebook.repository.UserRepository;
import by.nikita.recipebook.utils.UserMapper;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;
import java.util.Optional;

@Service
@AllArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final RecipeRepository recipeRepository;
    private final UserMapper userMapper;

    @Transactional
    public UserDTO createUser(UserDTO userDTO) {
        userRepository.findByUsername(userDTO.getUsername())
            .ifPresent(user -> {
                throw new IllegalArgumentException(
                    "Пользователь с именем '" + userDTO.getUsername() + "' уже существует"
                );
            });

        Optional.ofNullable(userDTO.getEmail())
            .flatMap(userRepository::findByEmail)
            .ifPresent(user -> {
                throw new IllegalArgumentException("Пользователь с email '" + userDTO.getEmail() + "' уже существует");
            });

        User user = userMapper.toEntity(userDTO);
        user.setId(null);
        user.setCreatedAt(null);
        User savedUser = userRepository.save(user);
        return userMapper.toDto(savedUser);
    }

    @Transactional(readOnly = true)
    public Page<UserDTO> getAllUsers(Pageable pageable) {
        return userRepository.findAll(pageable).map(userMapper::toDto);
    }

    @Transactional(readOnly = true)
    public Optional<UserDTO> getUserById(Long id) {
        return userRepository.findById(id).map(userMapper::toDto);
    }

    @Transactional
    public UserDTO updateUser(Long id, UserDTO userDTO) {
        User user = userRepository.findById(id)
            .orElseThrow(() -> new NoSuchElementException("Пользователь не найден, id: " + id));

        Optional.ofNullable(userDTO.getUsername())
            .filter(username -> !username.equals(user.getUsername()))
            .flatMap(userRepository::findByUsername)
            .ifPresent(existingUser -> {
                throw new IllegalArgumentException(
                    "Пользователь с именем '" + userDTO.getUsername() + "' уже существует"
                );
            });

        Optional.ofNullable(userDTO.getEmail())
            .filter(email -> !email.equals(user.getEmail()))
            .flatMap(userRepository::findByEmail)
            .ifPresent(existingUser -> {
                throw new IllegalArgumentException("Пользователь с email '" + userDTO.getEmail() + "' уже существует");
            });

        user.setUsername(userDTO.getUsername());
        user.setEmail(userDTO.getEmail());

        User updatedUser = userRepository.save(user);
        return userMapper.toDto(updatedUser);
    }

    @Transactional
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new NoSuchElementException("Пользователь не найден, id: " + id);
        }

        if (recipeRepository.existsByAuthorId(id)) {
            throw new IllegalStateException("Нельзя удалить пользователя, который указан автором рецептов");
        }

        userRepository.deleteById(id);
    }
}
