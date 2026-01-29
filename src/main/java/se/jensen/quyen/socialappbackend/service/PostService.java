package se.jensen.quyen.socialappbackend.service;

import se.jensen.quyen.socialappbackend.entity.Post;
import se.jensen.quyen.socialappbackend.entity.User;
import se.jensen.quyen.socialappbackend.repository.PostRepository;
import se.jensen.quyen.socialappbackend.repository.UserRepository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@Slf4j
@Service
public class PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;

    public PostService(PostRepository postRepository,
                       UserRepository userRepository) {
        this.postRepository = postRepository;
        this.userRepository = userRepository;
    }

    // ===================== CREATE =====================
    @Transactional
    public Post createPost(String content, String username) {
        log.info("Creating post for user: {}", username);

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> {
                    log.warn("User not found: {}", username);
                    return new ResponseStatusException(NOT_FOUND, "User not found");
                });

        Post post = new Post();
        post.setContent(content);
        post.setUser(user);

        Post savedPost = postRepository.save(post);
        log.info("Post created successfully with id {}", savedPost.getId());

        return savedPost;
    }

    // ===================== READ (FEED) =====================
    public List<Post> getAllPosts() {
        log.debug("Fetching all posts (feed)");
        return postRepository.findAllByOrderByCreatedAtDesc();
    }

    // ===================== READ (WALL) =====================
    public List<Post> getUserPosts(Long userId) {
        log.debug("Fetching posts for user id {}", userId);
        return postRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    // ===================== UPDATE =====================
    @Transactional
    public Post updatePost(Long postId, String content, String username) {
        log.info("Updating post {} by user {}", postId, username);

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> {
                    log.warn("Post not found: {}", postId);
                    return new ResponseStatusException(NOT_FOUND, "Post not found");
                });

        if (!post.getUser().getUsername().equals(username)) {
            log.warn("User {} not authorized to update post {}", username, postId);
            throw new ResponseStatusException(FORBIDDEN, "Not authorized");
        }

        post.setContent(content);
        Post updatedPost = postRepository.save(post);

        log.info("Post {} updated successfully", postId);
        return updatedPost;
    }

    // ===================== DELETE =====================
    @Transactional
    public void deletePost(Long postId, String username) {
        log.info("Deleting post {} by user {}", postId, username);

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> {
                    log.warn("Post not found: {}", postId);
                    return new ResponseStatusException(NOT_FOUND, "Post not found");
                });

        if (!post.getUser().getUsername().equals(username)) {
            log.warn("User {} not authorized to delete post {}", username, postId);
            throw new ResponseStatusException(FORBIDDEN, "Not authorized");
        }

        postRepository.delete(post);
        log.info("Post {} deleted successfully", postId);
    }
}
