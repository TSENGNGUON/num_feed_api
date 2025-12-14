package org.example.instragramclone.user.repository;

import jakarta.transaction.Transactional;
import org.example.instragramclone.user.dto.UserDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface UserRepository extends JpaRepository<UserDto, Integer> {
    Optional<UserDto> findByUsername(String username);
    Optional<UserDto> findByEmail(String email);

    @Transactional
    @Modifying
    @Query("update UserDto u set u.password = ?2 where u.email = ?1")
    void updatePassword(String email, String password);
}
