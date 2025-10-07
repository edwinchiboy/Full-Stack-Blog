package com.blog.cutom_blog.services;

import com.blog.cutom_blog.commons.comms.dtos.SendOtpResponse;
import com.blog.cutom_blog.config.AppProperties;
import com.blog.cutom_blog.exceptions.ForbiddenException;
import com.blog.cutom_blog.exceptions.NotFoundException;
import com.blog.cutom_blog.models.User;
import com.blog.cutom_blog.repositories.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class PasswordResetService {

    private final UserRepository userRepository;
    private final OtpService otpService;
    private final PasswordEncoder passwordEncoder;
    private final AppProperties appProperties;

    public PasswordResetService(UserRepository userRepository,
                                OtpService otpService,
                                PasswordEncoder passwordEncoder,
                                AppProperties appProperties) {
        this.userRepository = userRepository;
        this.otpService = otpService;
        this.passwordEncoder = passwordEncoder;
        this.appProperties = appProperties;
    }

    public SendOtpResponse initiatePasswordReset(String email) {
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new NotFoundException("No user found with this email"));

        // Generate and send OTP
        SendOtpResponse otpResponse = otpService.generateAndSendOtp(email, user.getId());

        log.info("Password reset initiated for user: {}", email);
        return otpResponse;
    }

    public void validateResetOtp(String email, String otp) {
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new NotFoundException("No user found with this email"));

        boolean isValid = otpService.validateOtp(otp, user.getId());

        if (!isValid) {
            throw new ForbiddenException("Invalid or expired OTP", "Invalid or expired OTP");
        }

        log.info("OTP validated successfully for user: {}", email);
    }

    public void resetPassword(String email, String otp, String newPassword) {
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new NotFoundException("No user found with this email"));

        // Validate OTP
        boolean isValid = otpService.validateOtp(otp, user.getId());

        if (!isValid) {
            throw new ForbiddenException("Invalid or expired OTP", "Invalid or expired OTP");
        }

        // Update password
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        log.info("Password reset successful for user: {}", email);
    }

    public SendOtpResponse resendResetOtp(String email) {
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new NotFoundException("No user found with this email"));

        SendOtpResponse otpResponse = otpService.generateAndSendOtp(email, user.getId());

        log.info("Password reset OTP resent for user: {}", email);
        return otpResponse;
    }
}