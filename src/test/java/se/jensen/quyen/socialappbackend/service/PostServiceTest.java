package se.jensen.quyen.socialappbackend.service;

import se.jensen.quyen.socialappbackend.entity.Post;
import se.jensen.quyen.socialappbackend.entity.User;
import se.jensen.quyen.socialappbackend.repository.PostRepository;
import se.jensen.quyen.socialappbackend.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PostServiceTest {
    @Mock
    private PostRepository postRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private PostService postService;

    @Test
    void testCreatePost_Success() {
        // Arrange
        User user = new User("testuser", "password", "USER");
        user.setId(1L);

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(postRepository.save(any(Post.class))).thenAnswer(invocation -> {
            Post post = invocation.getArgument(0);
            post.setId(1L);
            post.setCreatedAt(LocalDateTime.now());
            return post;
        });

        // Act
        Post result = postService.createPost("Hello World!", "testuser");

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Hello World!", result.getContent());
        assertEquals(user, result.getUser());
        verify(postRepository, times(1)).save(any(Post.class));
    }

    @Test
    void testGetAllPosts() {
        // Arrange
        User user = new User("testuser", "password", "USER");
        Post post1 = new Post("Post 1", user);
        Post post2 = new Post("Post 2", user);
        List<Post> posts = Arrays.asList(post1, post2);

        when(postRepository.findAllByOrderByCreatedAtDesc()).thenReturn(posts);

        // Act
        List<Post> result = postService.getAllPosts();

        // Assert
        assertEquals(2, result.size());
        verify(postRepository, times(1)).findAllByOrderByCreatedAtDesc();
    }
}