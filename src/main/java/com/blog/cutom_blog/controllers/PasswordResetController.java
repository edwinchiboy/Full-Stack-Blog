package com.blog.cutom_blog.controllers;

import com.blog.cutom_blog.commons.comms.dtos.SendOtpResponse;
import com.blog.cutom_blog.dtos.ApiResponse;
import com.blog.cutom_blog.dtos.PasswordResetRequestDto;
import com.blog.cutom_blog.dtos.ResetPasswordDto;
import com.blog.cutom_blog.dtos.ValidateResetOtpDto;
import com.blog.cutom_blog.services.PasswordResetService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/password-reset")
public class PasswordResetController {

    private final PasswordResetService passwordResetService;

    public PasswordResetController(PasswordResetService passwordResetService) {
        this.passwordResetService = passwordResetService;
    }

    @PostMapping("/initiate")
    public ResponseEntity<ApiResponse<?>> initiatePasswordReset(@Valid @RequestBody PasswordResetRequestDto request) {
        SendOtpResponse response = passwordResetService.initiatePasswordReset(request.getEmail());

        return ResponseEntity.status(HttpStatus.OK).body(
            ApiResponse.builder()
                .data(response)
                .message("Password reset OTP sent to your email")
                .build()
        );
    }

    @PostMapping("/validate-otp")
    public ResponseEntity<ApiResponse<?>> validateOtp(@Valid @RequestBody ValidateResetOtpDto request) {
        passwordResetService.validateResetOtp(request.getEmail(), request.getOtp());

        return ResponseEntity.status(HttpStatus.OK).body(
            ApiResponse.builder()
                .message("OTP validated successfully")
                .build()
        );
    }

    @PostMapping("/reset")
    public ResponseEntity<ApiResponse<?>> resetPassword(@Valid @RequestBody ResetPasswordDto request) {
        passwordResetService.resetPassword(request.getEmail(), request.getOtp(), request.getNewPassword());

        return ResponseEntity.status(HttpStatus.OK).body(
            ApiResponse.builder()
                .message("Password reset successful. Please login with your new password.")
                .build()
        );
    }

    @PostMapping("/resend-otp")
    public ResponseEntity<ApiResponse<?>> resendOtp(@Valid @RequestBody PasswordResetRequestDto request) {
        SendOtpResponse response = passwordResetService.resendResetOtp(request.getEmail());

        return ResponseEntity.status(HttpStatus.OK).body(
            ApiResponse.builder()
                .data(response)
                .message("OTP resent successfully")
                .build()
        );
    }
}