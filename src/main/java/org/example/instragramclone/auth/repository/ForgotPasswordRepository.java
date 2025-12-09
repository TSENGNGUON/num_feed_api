package org.example.instragramclone.auth.repository;

import org.example.instragramclone.entities.ForgotPassword;
import org.example.instragramclone.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;

import java.util.Optional;

public interface ForgotPasswordRepository extends JpaRepository<ForgotPassword, Integer> {
        @Query("select fp from ForgotPassword fp where fp.otp = ?1 and fp.user = ?2")
        Optional<ForgotPassword> findByOtpAndUser(Integer otp, User user);

        Optional<ForgotPassword> findByUser(User user);
}
