package org.example.instragramclone.post.repository;

import org.example.instragramclone.post.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface PostRepository extends JpaRepository<Post, UUID> {
    Page<Post> findAllByOrderByCreatedAtDesc(Pageable pageable);
    
    @Query("SELECT p FROM Post p WHERE p.author.id = :userId ORDER BY p.createdAt DESC")
    Page<Post> findByAuthorIdOrderByCreatedAtDesc(@Param("userId") UUID userId, Pageable pageable);
    
    Optional<Post> findByIdAndAuthorId(UUID id, UUID authorId);
}
