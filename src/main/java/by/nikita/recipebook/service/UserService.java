package by.nikita.recipebook.service;

import by.nikita.recipebook.entity.User;
import by.nikita.recipebook.entity.dto.UserDTO;
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
    private final UserMapper userMapper;

    @Transactional
    public UserDTO createUser(UserDTO userDTO) {
        if (userRepository.findByUsername(userDTO.getUsername()).isPresent()) {
            throw new IllegalArgumentException("User with username '" + userDTO.getUsername() + "' already exists");
        }

        if (userDTO.getEmail() != null && userRepository.findByEmail(userDTO.getEmail()).isPresent()) {
            throw new IllegalArgumentException("User with email '" + userDTO.getEmail() + "' already exists");
        }

        User user = userMapper.toEntity(userDTO);
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
            .orElseThrow(() -> new NoSuchElementException("User not found with id: " + id));

        if (!user.getUsername().equals(userDTO.getUsername())
            && userRepository.findByUsername(userDTO.getUsername()).isPresent()) {
            throw new IllegalArgumentException("User with username '" + userDTO.getUsername() + "' already exists");
        }

        if (userDTO.getEmail() != null && !userDTO.getEmail().equals(user.getEmail())
            && userRepository.findByEmail(userDTO.getEmail()).isPresent()) {
            throw new IllegalArgumentException("User with email '" + userDTO.getEmail() + "' already exists");
        }

        user.setUsername(userDTO.getUsername());
        user.setEmail(userDTO.getEmail());

        User updatedUser = userRepository.save(user);
        return userMapper.toDto(updatedUser);
    }

    @Transactional
    public void deleteUser(Long id) {
        User user = userRepository.findById(id)
            .orElseThrow(() -> new NoSuchElementException("User not found with id: " + id));

        if (user.getRecipes() != null && !user.getRecipes().isEmpty()) {
            throw new IllegalStateException("Cannot delete user with existing recipes");
        }

        userRepository.deleteById(id);
    }
}
