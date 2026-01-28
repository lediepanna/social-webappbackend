package se.jensen.quyen.socialappbackend.controller;

import se.jensen.quyen.socialappbackend.entity.User;
import se.jensen.quyen.socialappbackend.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@PreAuthorize("hasRole('ADMIN')")
public class UserController {
    private final UserService userService;
    private static final Logger log = LoggerFactory.getLogger(UserController.class);

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/{username}")
    public ResponseEntity<User> getUser(@PathVariable String username) {
        log.info("GET /api/users/{}", username);
        User user = userService.findByUsername(username);
        return ResponseEntity.ok(user);
    }
}