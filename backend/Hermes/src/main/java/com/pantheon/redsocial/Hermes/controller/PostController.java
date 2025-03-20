package com.pantheon.redsocial.Hermes.controller;

import com.pantheon.redsocial.Hermes.model.Post;
import com.pantheon.redsocial.Hermes.payload.PostResponse;
import com.pantheon.redsocial.Hermes.service.PostService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/posts")
@Tag(name = "Post Management", description = "Endpoints for managing posts")
public class PostController {

    @Autowired
    private PostService postService;

    @Operation(summary = "Create a new post", description = "Allows an authenticated user to create a new post with text content and an optional image.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Post created successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - User must be logged in")
    })
    @PostMapping("/create")
    public ResponseEntity<Post> createPost(@RequestBody Post postRequest) {
        return ResponseEntity.ok(postService.createPost(postRequest.getContent(), postRequest.getImageUrl()));
    }

    @Operation(summary = "Retrieve all posts", description = "Fetches all posts with pagination support.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved posts")
    })
    @GetMapping("/all")
    public ResponseEntity<Page<PostResponse>> getAllPosts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(postService.getAllPosts(page, size));
    }

    @Operation(summary = "Retrieve a post by ID", description = "Fetches a single post by its unique ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Post retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Post not found")
    })
    @GetMapping("/{postId}")
    public ResponseEntity<Optional<Post>> getPostById(@PathVariable Long postId) {
        return ResponseEntity.ok(postService.getPostById(postId));
    }

    @Operation(summary = "Retrieve posts by a specific user", description = "Fetches all posts created by a specific user with pagination.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Posts retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @GetMapping("/user/{username}")
    public ResponseEntity<Page<PostResponse>> getPostsByUser(
            @PathVariable String username,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(postService.getPostsByUser(username, page, size));
    }

    @Operation(summary = "Update an existing post", description = "Allows the original creator of a post to update its content.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Post updated successfully"),
            @ApiResponse(responseCode = "403", description = "Forbidden - Only the post owner can update"),
            @ApiResponse(responseCode = "404", description = "Post not found")
    })
    @PutMapping("/{postId}")
    public ResponseEntity<?> updatePost(
            @PathVariable Long postId,
            @RequestBody Post postRequest,
            @AuthenticationPrincipal UserDetails userDetails) {

        Post updatedPost = postService.updatePost(postId, postRequest.getContent(), postRequest.getImageUrl());
        return ResponseEntity.ok(updatedPost);
    }

    @Operation(summary = "Delete a post", description = "Allows the original creator of a post to delete it.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Post deleted successfully"),
            @ApiResponse(responseCode = "403", description = "Forbidden - Only the post owner can delete"),
            @ApiResponse(responseCode = "404", description = "Post not found")
    })
    @DeleteMapping("/{postId}")
    public ResponseEntity<?> deletePost(
            @PathVariable Long postId,
            @AuthenticationPrincipal UserDetails userDetails) {

        postService.deletePost(postId);
        return ResponseEntity.ok().body("Post deleted successfully");
    }

    @Operation(summary = "Retrieve liked posts", description = "Fetches all posts liked by the authenticated user.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Liked posts retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - User must be logged in")
    })
    @GetMapping("/liked")
    public ResponseEntity<Page<PostResponse>> getLikedPosts(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        String username = userDetails.getUsername();
        return ResponseEntity.ok(postService.getLikedPosts(username, page, size));
    }

}
