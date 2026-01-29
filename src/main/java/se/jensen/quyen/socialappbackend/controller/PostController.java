package se.jensen.quyen.socialappbackend.controller;

import se.jensen.quyen.socialappbackend.dto.request.PostRequestDTO;
import se.jensen.quyen.socialappbackend.dto.response.PostResponseDTO;
import se.jensen.quyen.socialappbackend.entity.Post;
import se.jensen.quyen.socialappbackend.service.PostService;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/posts")
public class PostController {

    private final PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }

    // CREATE
    @PostMapping
    public ResponseEntity<PostResponseDTO> createPost(
            @RequestBody PostRequestDTO request,
            @AuthenticationPrincipal UserDetails userDetails) {

        Post post = postService.createPost(
                request.getContent(),
                userDetails.getUsername());

        return ResponseEntity.ok(toDto(post));
    }

    // READ ALL
    @GetMapping
    public ResponseEntity<List<PostResponseDTO>> getAll() {
        return ResponseEntity.ok(
                postService.getAllPosts()
                        .stream()
                        .map(this::toDto)
                        .collect(Collectors.toList())
        );
    }

    // UPDATE ✅
    @PutMapping("/{id}")
    public ResponseEntity<PostResponseDTO> updatePost(
            @PathVariable Long id,
            @RequestBody PostRequestDTO request,
            @AuthenticationPrincipal UserDetails userDetails) {

        Post updatedPost = postService.updatePost(
                id,
                request.getContent(),
                userDetails.getUsername());

        return ResponseEntity.ok(toDto(updatedPost));
    }

    // DELETE
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePost(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {

        postService.deletePost(id, userDetails.getUsername());
        return ResponseEntity.noContent().build();
    }

    private PostResponseDTO toDto(Post post) {
        return new PostResponseDTO(
                post.getId(),
                post.getContent(),
                post.getCreatedAt(),
                post.getUser().getUsername()
        );
    }
}
