package org.example.instragramclone.auth.controller;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.example.instragramclone.auth.dto.ChangePassword;
import org.example.instragramclone.auth.dto.MailBody;
import org.example.instragramclone.auth.dto.VerifyMailRequest;
import org.example.instragramclone.auth.repository.ForgotPasswordRepository;
import org.example.instragramclone.auth.repository.UserRepository;
import org.example.instragramclone.common.dto.response.ApiResponse;
import org.example.instragramclone.entities.ForgotPassword;
import org.example.instragramclone.service.EmailService;
import org.example.instragramclone.user.User;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.Date;
import java.util.Objects;
import java.util.Random;

@RestController
@RequestMapping("/forgotPassword")
@RequiredArgsConstructor
public class ForgotPasswordController {
    private final UserRepository userRepository;
    private final EmailService emailService;
    private final ForgotPasswordRepository forgotPasswordRepository;
    private final PasswordEncoder passwordEncoder;

    @PostMapping("/verifyMail")
    @Transactional
    public ResponseEntity<ApiResponse<String>> verifyEmail(@RequestBody VerifyMailRequest request){
        String email = request.getEmail();
        User user = userRepository.findByEmail(email).orElseThrow(() ->
                new UsernameNotFoundException("Please provide a valid email!"));
        int otp = otpGenerator();
        MailBody mailBody = MailBody.builder()
                .to(email)
                .text("This is a OTP for your Forgot Password Request : " + otp)
                .subject("OTP for Forgot Password request")
                .build();

        ForgotPassword fp = forgotPasswordRepository.findByUser(user).orElse(null);
        Date expiry = new Date(System.currentTimeMillis() + 2 * 60 * 1000);
        if (fp == null) {
            fp = ForgotPassword.builder()
                    .otp(otp)
                    .expirationTime(expiry)
                    .user(user)
                    .build();
        } else {
            fp.setOtp(otp);
            fp.setExpirationTime(expiry);
        }

        emailService.sendSimpleMessage(mailBody);
        forgotPasswordRepository.save(fp);

        ApiResponse<String> response =  ApiResponse.<String>builder()
                .success(true)
                .message("OTP sent successfully to " + email)
                .data("Email send for verification!")
                .build();
        return ResponseEntity.ok(response);
    }


    @PostMapping("/verifyOtp/{otp}/{email}")
    public ResponseEntity<ApiResponse<String>> verifyOtp(@PathVariable Integer otp,@PathVariable String email){
        // Find user by email
        User user = userRepository.findByEmail(email).orElseThrow(() ->
                new UsernameNotFoundException("Please provide a valid email!"));

        // Find OTP record for user
        ForgotPassword fp = forgotPasswordRepository.findByOtpAndUser(otp, user)
                .orElseThrow(() -> new RuntimeException("Invalid OTP for Email : " + email));

        if (fp.getExpirationTime().before(Date.from(Instant.now()))){
            forgotPasswordRepository.deleteById(fp.getFpid());
            ApiResponse<String> response = ApiResponse.<String>builder()
                    .success(false)
                    .message("OTP has expired!")
                    .data(null)
                    .build();

           return new ResponseEntity<>(response, HttpStatus.EXPECTATION_FAILED);
        }
        ApiResponse<String> response = ApiResponse.<String>builder()
                .success(true)
                .message("OTP verified successfully")
                .data("You can now reset your password")
                .build();

        return ResponseEntity.ok(response);
    }

    @PostMapping("/changePassword/{email}")
    public ResponseEntity<ApiResponse<String>> changePasswordHandler(@RequestBody ChangePassword changePassword, @PathVariable String email) {
        if (!Objects.equals(changePassword.password(), changePassword.repeatPassword())) {
            ApiResponse<String> response = ApiResponse.<String>builder()
                    .success(false)
                    .message("Please enter the password again!")
                    .data(null)
                    .build();
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        String encodedPassword = passwordEncoder.encode(changePassword.password());
        userRepository.updatePassword(email, encodedPassword);
        ApiResponse<String> response = ApiResponse.<String>builder()
                .success(true)
                .message("Password has been change")
                .data(null)
                .build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }



    private Integer otpGenerator() {
        Random random = new Random();
        return random.nextInt(100_000, 999_999);
    }

}
