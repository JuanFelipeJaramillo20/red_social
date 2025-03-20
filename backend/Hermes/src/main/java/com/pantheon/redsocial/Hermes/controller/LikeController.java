package com.pantheon.redsocial.Hermes.controller;

import com.pantheon.redsocial.Hermes.service.LikeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/likes")
@Tag(name = "Like Management", description = "Endpoints for liking/unliking posts")
public class LikeController {

    @Autowired
    private LikeService likeService;

    @Operation(summary = "Like or Unlike a post", description = "Allows authenticated users to like or unlike a post. A user can like a post only once.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully liked/unliked the post"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - User must be logged in"),
            @ApiResponse(responseCode = "404", description = "Post not found")
    })
    @PostMapping("/{postId}")
    public ResponseEntity<?> toggleLike(
            @PathVariable Long postId,
            @AuthenticationPrincipal UserDetails userDetails) {
        String result = likeService.toggleLike(postId);
        return ResponseEntity.ok(result);
    }

    @Operation(summary = "Get total likes for a post", description = "Returns the total number of likes for a given post.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved like count"),
            @ApiResponse(responseCode = "404", description = "Post not found")
    })
    @GetMapping("/{postId}/count")
    public ResponseEntity<Long> getLikeCount(@PathVariable Long postId) {
        return ResponseEntity.ok(likeService.getLikeCount(postId));
    }
}
