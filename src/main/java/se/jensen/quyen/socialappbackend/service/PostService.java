package se.jensen.quyen.socialappbackend.service;

import se.jensen.quyen.socialappbackend.entity.Post;
import se.jensen.quyen.socialappbackend.entity.User;
import se.jensen.quyen.socialappbackend.repository.PostRepository;
import se.jensen.quyen.socialappbackend.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PostService {
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private static final Logger log = LoggerFactory.getLogger(PostService.class);

    public PostService(PostRepository postRepository, UserRepository userRepository) {
        this.postRepository = postRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public Post createPost(String content, String username) {
        log.info("=== CREATE POST DEBUG START ===");
        log.info("Username: {}", username);
        log.info("Content: {}", content);

        try {
            // 1. Hämta användare
            log.debug("Step 1: Looking for user '{}' in database", username);

            java.util.Optional<User> userOptional = userRepository.findByUsername(username);
            if (!userOptional.isPresent()) {
                log.error("ERROR: User '{}' not found in database!", username);
                log.error("Available users in database:");
                userRepository.findAll().forEach(u ->
                        log.error("  - {} (ID: {})", u.getUsername(), u.getId())
                );
                throw new RuntimeException("User not found: " + username);
            }

            User user = userOptional.get();
            log.info("Step 1: User found - ID: {}, Username: {}", user.getId(), user.getUsername());

            // 2. Skapa post-objekt
            log.debug("Step 2: Creating Post object");
            Post post = new Post();
            post.setContent(content);
            post.setUser(user);

            // Kontrollera att allt är korrekt
            log.debug("Post content set: {}", post.getContent());
            log.debug("Post user set: {}", post.getUser().getUsername());
            log.debug("Post user ID: {}", post.getUser().getId());

            // 3. Spara till databas
            log.debug("Step 3: Saving to database...");
            Post savedPost = postRepository.save(post);
            log.info("Step 3: Post saved successfully! ID: {}", savedPost.getId());
            log.info("Post created at: {}", savedPost.getCreatedAt());

            log.info("=== CREATE POST DEBUG END ===");
            return savedPost;

        } catch (Exception e) {
            log.error("=== CREATE POST ERROR ===");
            log.error("Exception type: {}", e.getClass().getName());
            log.error("Exception message: {}", e.getMessage());
            log.error("Stack trace:", e);
            log.error("=== END ERROR ===");
            throw e;
        }
    }

    public List<Post> getAllPosts() {
        log.debug("Fetching all posts");
        return postRepository.findAllByOrderByCreatedAtDesc();
    }

    public List<Post> getUserPosts(Long userId) {
        log.debug("Fetching posts for user ID: {}", userId);
        return postRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    @Transactional
    public void deletePost(Long postId, String username) {
        log.info("Deleting post ID: {} for user: {}", postId, username);

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> {
                    log.error("Post not found: {}", postId);
                    return new RuntimeException("Post not found");
                });

        if (!post.getUser().getUsername().equals(username)) {
            log.error("User {} not authorized to delete post {}", username, postId);
            throw new RuntimeException("Not authorized to delete this post");
        }

        postRepository.delete(post);
        log.info("Post {} deleted successfully", postId);
    }

    @Transactional
    public Post updatePost(Long postId, String content, String username) {
        log.info("Updating post ID: {} for user: {}", postId, username);

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> {
                    log.error("Post not found: {}", postId);
                    return new RuntimeException("Post not found");
                });

        if (!post.getUser().getUsername().equals(username)) {
            log.error("User {} not authorized to update post {}", username, postId);
            throw new RuntimeException("Not authorized to update this post");
        }

        post.setContent(content);
        Post updatedPost = postRepository.save(post);
        log.info("Post {} updated successfully", postId);

        return updatedPost;
    }
}