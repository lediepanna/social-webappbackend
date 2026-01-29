package se.jensen.quyen.socialappbackend.service;

import se.jensen.quyen.socialappbackend.entity.User;
import se.jensen.quyen.socialappbackend.repository.UserRepository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    // ✅ TEST 1 – REGISTER SUCCESS
    @Test
    void testRegisterUser_Success() {
        when(userRepository.existsByUsername("testuser"))
                .thenReturn(false);

        when(passwordEncoder.encode("password123"))
                .thenReturn("encodedPassword");

        when(userRepository.save(any(User.class)))
                .thenAnswer(invocation -> {
                    User user = invocation.getArgument(0);
                    user.setId(1L);
                    return user;
                });

        User result = userService.registerUser(
                "testuser", "password123", "USER");

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("testuser", result.getUsername());
        assertEquals("encodedPassword", result.getPassword());
        assertEquals("USER", result.getRole());

        verify(userRepository).save(any(User.class));
    }

    // ✅ TEST 2 – REGISTER USERNAME EXISTS
    @Test
    void testRegisterUser_UsernameExists() {
        when(userRepository.existsByUsername("existinguser"))
                .thenReturn(true);

        assertThrows(ResponseStatusException.class, () ->
                userService.registerUser(
                        "existinguser", "password123", "USER")
        );

        verify(userRepository, never()).save(any());
    }

    // ✅ TEST 3 – FIND BY USERNAME
    @Test
    void testFindByUsername_Success() {
        User user = new User("anna", "password", "USER");

        when(userRepository.findByUsername("anna"))
                .thenReturn(Optional.of(user));

        User result = userService.findByUsername("anna");

        assertEquals("anna", result.getUsername());
        verify(userRepository).findByUsername("anna");
    }
}
