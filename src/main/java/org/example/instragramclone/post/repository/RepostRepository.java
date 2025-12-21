package org.example.instragramclone.post.repository;

import org.example.instragramclone.post.entity.Repost;
import org.example.instragramclone.user.dto.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface RepostRepository extends JpaRepository<Repost, UUID> {
    Optional<Repost> findByUserAndPost(User user, org.example.instragramclone.post.entity.Post post);
    
    boolean existsByUserAndPost(User user, org.example.instragramclone.post.entity.Post post);
    
    @Query("SELECT COUNT(r) FROM Repost r WHERE r.post.id = :postId")
    Long countByPostId(@Param("postId") UUID postId);
}

