package se.jensen.quyen.socialappbackend.controller;

import se.jensen.quyen.socialappbackend.dto.response.PostResponseDTO;
import se.jensen.quyen.socialappbackend.entity.User;
import se.jensen.quyen.socialappbackend.service.PostService;
import se.jensen.quyen.socialappbackend.service.UserService;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@PreAuthorize("hasRole('USER')")
public class UserController {

    private final UserService userService;
    private final PostService postService;

    public UserController(UserService userService,
                          PostService postService) {
        this.userService = userService;
        this.postService = postService;
    }


    // GET /users/username/{username}
    @GetMapping("/username/{username}")
    public ResponseEntity<User> getUserByUsername(
            @PathVariable String username) {

        User user = userService.findByUsername(username);
        return ResponseEntity.ok(user);
    }


    // GET /users/{id}/posts
    @GetMapping("/{id}/posts")
    public ResponseEntity<List<PostResponseDTO>> getUserPosts(
            @PathVariable Long id) {

        List<PostResponseDTO> posts = postService.getUserPosts(id)
                .stream()
                .map(post -> new PostResponseDTO(
                        post.getId(),
                        post.getContent(),
                        post.getCreatedAt(),
                        post.getUser().getUsername()
                ))
                .toList();

        return ResponseEntity.ok(posts);
    }
}
