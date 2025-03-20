package com.pantheon.redsocial.Hermes.repository;

import com.pantheon.redsocial.Hermes.model.Like;
import com.pantheon.redsocial.Hermes.model.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LikeRepository extends JpaRepository<Like, Long> {
    Optional<Like> findByPostAndUserId(Post post, String userId);
    Long countByPost(Post post);
    boolean existsByPostIdAndUserId(Long postId, String userId);
    @Query("SELECT COUNT(l) FROM Like l WHERE l.post.id = :postId")
    long countLikesByPostId(@Param("postId") Long postId);
}
