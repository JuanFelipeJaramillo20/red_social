package com.pantheon.redsocial.Hermes.payload;

import java.time.LocalDateTime;

public class PostResponse {
    private Long id;
    private String content;
    private String imageUrl;
    private String createdBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private long likeCount; // ✅ Add this
    private boolean likedByUser; // ✅ New field

    public PostResponse(Long id, String content, String imageUrl, String createdBy,
                        LocalDateTime createdAt, LocalDateTime updatedAt, boolean likedByUser, Long likeCount) {
        this.id = id;
        this.content = content;
        this.imageUrl = imageUrl;
        this.createdBy = createdBy;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.likedByUser = likedByUser;
        this.likeCount = likeCount;
    }

    public long getLikeCount() {
        return likeCount;
    }

    public Long getId() { return id; }
    public String getContent() { return content; }
    public String getImageUrl() { return imageUrl; }
    public String getCreatedBy() { return createdBy; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public boolean isLikedByUser() { return likedByUser; }

    public void setLikedByUser(boolean likedByUser) { this.likedByUser = likedByUser; }
}
