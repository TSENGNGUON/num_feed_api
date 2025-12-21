package org.example.instragramclone.auth.repository;

import org.example.instragramclone.entities.ForgotPassword;
import org.example.instragramclone.user.dto.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.UUID;

public interface ForgotPasswordRepository extends JpaRepository<ForgotPassword, UUID> {
        @Query("select fp from ForgotPassword fp where fp.otp = ?1 and fp.user = ?2")
        Optional<ForgotPassword> findByOtpAndUser(Integer otp, User user);

        Optional<ForgotPassword> findByUser(User user);
}
