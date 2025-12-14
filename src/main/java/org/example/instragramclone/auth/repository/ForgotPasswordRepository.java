package org.example.instragramclone.auth.repository;

import org.example.instragramclone.entities.ForgotPassword;
import org.example.instragramclone.user.dto.UserDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface ForgotPasswordRepository extends JpaRepository<ForgotPassword, Integer> {
        @Query("select fp from ForgotPassword fp where fp.otp = ?1 and fp.userDto = ?2")
        Optional<ForgotPassword> findByOtpAndUser(Integer otp, UserDto userDto);

        Optional<ForgotPassword> findByUserDto(UserDto userDto);
}
