package com.pantheon.redsocial.Hermes.service;

import com.pantheon.redsocial.Hermes.model.Post;
import com.pantheon.redsocial.Hermes.payload.PostResponse;
import com.pantheon.redsocial.Hermes.repository.LikeRepository;
import com.pantheon.redsocial.Hermes.repository.PostRepository;
import com.pantheon.redsocial.Hermes.security.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

import static org.springframework.data.domain.PageRequest.*;

@Service
public class PostService {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private LikeRepository likeRepository;


    private static final Logger logger = Logger.getLogger(PostService.class.getName());

    /**
     * Creates a new post.
     */
    @Transactional
    public Post createPost(String content, String imageUrl) {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username = userDetails.getUsername();
        logger.info("Creating post for user: " + username);

        Post post = new Post();
        post.setContent(content);
        post.setImageUrl(imageUrl);
        post.setCreatedBy(username);

        return postRepository.save(post);
    }

    /**
     * Retrieves all posts.
     */
    @Transactional(readOnly = true)
    public Page<PostResponse> getAllPosts(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Post> posts = postRepository.findAll(pageable);

        String currentUsername = getCurrentUsername();

        return posts.map(post -> {
            long likeCount = likeRepository.countLikesByPostId(post.getId());
            return new PostResponse(
                    post.getId(),
                    post.getContent(),
                    post.getImageUrl(),
                    post.getCreatedBy(),
                    post.getCreatedAt(),
                    post.getUpdatedAt(),
                    isPostLikedByUser(post.getId(), currentUsername),
                    likeCount
            );
        });
    }

    /**
     * Retrieves a post by ID.
     */
    public Optional<Post> getPostById(Long postId) {
        return postRepository.findById(postId);
    }


    /**
     * Updates a post (only if user is the creator).
     */
    @Transactional
    public Post updatePost(Long postId, String newContent, String newImageUrl) {
        Optional<Post> optionalPost = postRepository.findById(postId);
        if (optionalPost.isEmpty()) {
            throw new IllegalArgumentException("Post not found");
        }

        Post post = optionalPost.get();
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String currentUsername = userDetails.getUsername();

        if (!post.getCreatedBy().equals(currentUsername)) {
            throw new IllegalArgumentException("You are not the owner of this post");
        }

        post.setContent(newContent);
        post.setImageUrl(newImageUrl);
        post.setUpdatedAt(java.time.LocalDateTime.now());

        Post updatedPost = postRepository.save(post);
        logger.info("Post updated by user: " + currentUsername);
        return updatedPost;
    }

    /**
     * Deletes a post (only if user is the creator).
     */
    @Transactional
    public void deletePost(Long postId) {
        Optional<Post> optionalPost = postRepository.findById(postId);
        if (optionalPost.isEmpty()) {
            throw new IllegalArgumentException("Post not found");
        }

        Post post = optionalPost.get();
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String currentUsername = userDetails.getUsername();

        if (!post.getCreatedBy().equals(currentUsername)) {
            throw new IllegalArgumentException("You are not the owner of this post");
        }

        postRepository.delete(post);
        logger.info("Post deleted by user: " + currentUsername);
    }

    /**
     * Retrieves posts from a specific user including `likedByUser` status.
     */
    @Transactional(readOnly = true)
    public Page<PostResponse> getPostsByUser(String username, int page, int size) {
        Pageable pageable = of(page, size);
        Page<Post> posts = postRepository.findByCreatedBy(username, pageable);

        String currentUsername = getCurrentUsername();

        return posts.map(post -> {
            long likeCount = likeRepository.countLikesByPostId(post.getId());
            return new PostResponse(
                    post.getId(),
                    post.getContent(),
                    post.getImageUrl(),
                    post.getCreatedBy(),
                    post.getCreatedAt(),
                    post.getUpdatedAt(),
                    isPostLikedByUser(post.getId(), currentUsername),
                    likeCount
            );
        });
    }

    @Transactional(readOnly = true)
    public Page<PostResponse> getLikedPosts(String username, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Post> likedPosts = postRepository.findLikedPostsByUsername(username, pageable);

        return likedPosts.map(post -> new PostResponse(
                post.getId(),
                post.getContent(),
                post.getImageUrl(),
                post.getCreatedBy(),
                post.getCreatedAt(),
                post.getUpdatedAt(),
                true, // ✅ Since these are liked posts, always true
                likeRepository.countLikesByPostId(post.getId())
        ));
    }


    /**
     * Helper method to get the currently authenticated username.
     */
    private String getCurrentUsername() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDetails userDetails) {
            return userDetails.getUsername();
        }
        return null; // Return null if user is not authenticated
    }

    /**
     * Checks if the user has liked a specific post.
     */
    private boolean isPostLikedByUser(Long postId, String username) {
        if (username == null) return false;
        return likeRepository.existsByPostIdAndUserId(postId, username);
    }

}
