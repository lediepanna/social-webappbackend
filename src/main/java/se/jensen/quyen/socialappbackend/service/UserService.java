package se.jensen.quyen.socialappbackend.service;

import se.jensen.quyen.socialappbackend.entity.User;
import se.jensen.quyen.socialappbackend.repository.UserRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@Service
public class UserService {

    private static final Logger log = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // ===================== REGISTER =====================
    @Transactional
    public User registerUser(String username, String password, String role) {
        log.info("Registering new user: {}", username);

        if (userRepository.existsByUsername(username)) {
            log.warn("Username already exists: {}", username);
            throw new ResponseStatusException(
                    CONFLICT, "Username already exists");
        }

        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        user.setRole(role != null ? role : "USER");

        User savedUser = userRepository.save(user);
        log.info("User registered successfully: {}", username);

        return savedUser;
    }

    // ===================== FIND BY USERNAME =====================
    @Transactional(readOnly = true)
    public User findByUsername(String username) {
        log.debug("Finding user by username: {}", username);

        return userRepository.findByUsername(username)
                .orElseThrow(() -> {
                    log.warn("User not found: {}", username);
                    return new ResponseStatusException(
                            NOT_FOUND, "User not found");
                });
    }
}
