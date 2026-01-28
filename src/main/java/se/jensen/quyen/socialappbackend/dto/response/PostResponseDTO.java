package se.jensen.quyen.socialappbackend.dto.response;

import java.time.LocalDateTime;

public class PostResponseDTO {
    private Long id;
    private String content;
    private LocalDateTime createdAt;
    private String username;

    public PostResponseDTO(Long id, String content, LocalDateTime createdAt, String username) {
        this.id = id;
        this.content = content;
        this.createdAt = createdAt;
        this.username = username;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
}