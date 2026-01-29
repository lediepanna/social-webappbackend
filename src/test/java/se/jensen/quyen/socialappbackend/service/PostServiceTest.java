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
import org.springframework.web.server.ResponseStatusException;

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

    // ✅ TEST 1 – CREATE
    @Test
    void testCreatePost_Success() {
        User user = new User("testuser", "password", "USER");
        user.setId(1L);

        when(userRepository.findByUsername("testuser"))
                .thenReturn(Optional.of(user));

        when(postRepository.save(any(Post.class)))
                .thenAnswer(invocation -> {
                    Post post = invocation.getArgument(0);
                    post.setId(1L);
                    post.setCreatedAt(LocalDateTime.now());
                    return post;
                });

        Post result = postService.createPost("Hello World!", "testuser");

        assertNotNull(result);
        assertEquals("Hello World!", result.getContent());
        assertEquals(user, result.getUser());
        verify(postRepository).save(any(Post.class));
    }

    // ✅ TEST 2 – FEED
    @Test
    void testGetAllPosts() {
        User user = new User("testuser", "password", "USER");
        Post post1 = new Post("Post 1", user);
        Post post2 = new Post("Post 2", user);

        when(postRepository.findAllByOrderByCreatedAtDesc())
                .thenReturn(Arrays.asList(post1, post2));

        List<Post> result = postService.getAllPosts();

        assertEquals(2, result.size());
        verify(postRepository).findAllByOrderByCreatedAtDesc();
    }

    // ✅ TEST 3 – DELETE (NOT OWNER)
    @Test
    void testDeletePost_NotOwner_ShouldThrow() {
        User owner = new User("owner", "password", "USER");
        User otherUser = new User("other", "password", "USER");

        Post post = new Post();
        post.setId(1L);
        post.setUser(owner);

        when(postRepository.findById(1L))
                .thenReturn(Optional.of(post));

        assertThrows(ResponseStatusException.class, () ->
                postService.deletePost(1L, otherUser.getUsername())
        );

        verify(postRepository, never()).delete(any());
    }
}
