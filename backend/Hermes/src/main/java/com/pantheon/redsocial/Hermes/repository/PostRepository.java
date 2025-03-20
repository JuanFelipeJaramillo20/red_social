package com.pantheon.redsocial.Hermes.repository;

import com.pantheon.redsocial.Hermes.model.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
    List<Post> findByCreatedBy(String username);
    Page<Post> findAll(Pageable pageable);
    Page<Post> findByCreatedBy(String username, Pageable pageable);
    @Query("SELECT p FROM Post p JOIN Like l ON p.id = l.post.id WHERE l.userId = :username")
    Page<Post> findLikedPostsByUsername(@Param("username") String username, Pageable pageable);

}
