package com.pantheon.redsocial.Hermes.service;

import com.pantheon.redsocial.Hermes.model.Like;
import com.pantheon.redsocial.Hermes.model.Post;
import com.pantheon.redsocial.Hermes.repository.LikeRepository;
import com.pantheon.redsocial.Hermes.repository.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.logging.Logger;

@Service
public class LikeService {

    private static final Logger logger = Logger.getLogger(LikeService.class.getName());

    @Autowired
    private LikeRepository likeRepository;

    @Autowired
    private PostRepository postRepository;

    /**
     * Likes or unlikes a post.
     */
    @Transactional
    public String toggleLike(Long postId) {
        Optional<Post> optionalPost = postRepository.findById(postId);
        if (optionalPost.isEmpty()) {
            throw new IllegalArgumentException("Post not found");
        }

        Post post = optionalPost.get();
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String currentUsername = userDetails.getUsername();

        Optional<Like> existingLike = likeRepository.findByPostAndUserId(post, currentUsername);

        if (existingLike.isPresent()) {
            likeRepository.delete(existingLike.get());
            logger.info("User " + currentUsername + " unliked post ID: " + postId);
            return "Unliked the post";
        } else {
            Like like = new Like();
            like.setPost(post);
            like.setUserId(currentUsername);
            likeRepository.save(like);
            logger.info("User " + currentUsername + " liked post ID: " + postId);
            return "Liked the post";
        }
    }

    /**
     * Counts the number of likes on a post.
     */
    @Transactional(readOnly = true)
    public Long getLikeCount(Long postId) {
        Optional<Post> optionalPost = postRepository.findById(postId);
        if (optionalPost.isEmpty()) {
            throw new IllegalArgumentException("Post not found");
        }
        return likeRepository.countByPost(optionalPost.get());
    }
}
