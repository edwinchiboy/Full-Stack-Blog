package com.blog.cutom_blog.services;

import com.blog.cutom_blog.commons.comms.MailGunService;
import com.blog.cutom_blog.commons.comms.constants.EmailBodyType;
import com.blog.cutom_blog.commons.comms.dtos.EmailBody;
import com.blog.cutom_blog.commons.comms.dtos.EmailDeliveryStatus;
import com.blog.cutom_blog.commons.comms.dtos.EmailDto;
import com.blog.cutom_blog.commons.comms.dtos.SendOtpResponse;
import com.blog.cutom_blog.config.AppProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
public class OtpService {

    private static final int OTP_LENGTH = 6;
    private static final int OTP_EXPIRY_MINUTES = 10;
    private final SecureRandom random = new SecureRandom();

    // In-memory storage for OTPs (for production, use Redis or database)
    private final Map<String, OtpData> otpStorage = new ConcurrentHashMap<>();

    private final MailGunService mailGunService;
    private final AppProperties appProperties;

    public OtpService(MailGunService mailGunService, AppProperties appProperties) {
        this.mailGunService = mailGunService;
        this.appProperties = appProperties;
    }

    public SendOtpResponse generateAndSendOtp(String email, String tokenIdentifier) {
        String otp = generateOtp();
        Instant issuedAt = Instant.now();
        Instant expireAt = issuedAt.plus(OTP_EXPIRY_MINUTES, ChronoUnit.MINUTES);

        // Store OTP
        otpStorage.put(tokenIdentifier, new OtpData(otp, expireAt));

        // Send email
        String htmlBody = buildOtpEmailHtml(otp, OTP_EXPIRY_MINUTES);

        try {
            EmailDeliveryStatus status = mailGunService.sendEmail(EmailDto.builder()
                .from(appProperties.getEmailConfig().getDefaultFromEmail())
                .to(List.of(email))
                .subject("Verify Your Email - Crypto Blog")
                .body(new EmailBody(htmlBody, EmailBodyType.HTML))
                .build());

            log.info("OTP sent successfully to {} with messageId: {}", email, status.getMessageId());
        } catch (Exception e) {
            log.error("Failed to send OTP email to {}", email, e);
            throw new RuntimeException("Failed to send verification email. Please try again.");
        }

        return SendOtpResponse.builder()
            .issuedAt(issuedAt)
            .expireAt(expireAt)
            .durationToExpireMinutes(OTP_EXPIRY_MINUTES)
            .durationToExpireSeconds(OTP_EXPIRY_MINUTES * 60)
            .build();
    }

    public boolean validateOtp(String otp, String tokenIdentifier) {
        OtpData otpData = otpStorage.get(tokenIdentifier);

        if (otpData == null) {
            log.warn("OTP validation failed: No OTP found for identifier {}", tokenIdentifier);
            return false;
        }

        if (Instant.now().isAfter(otpData.expireAt)) {
            log.warn("OTP validation failed: OTP expired for identifier {}", tokenIdentifier);
            otpStorage.remove(tokenIdentifier);
            return false;
        }

        boolean isValid = otpData.otp.equals(otp);

        if (isValid) {
            otpStorage.remove(tokenIdentifier); // Remove OTP after successful validation
        }

        return isValid;
    }

    private String generateOtp() {
        int otp = random.nextInt(900000) + 100000; // Generate 6-digit OTP
        return String.valueOf(otp);
    }

    private String buildOtpEmailHtml(String otp, int expiryMinutes) {
        return String.format("""
            <!DOCTYPE html>
            <html>
            <head>
                <style>
                    body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }
                    .container { max-width: 600px; margin: 0 auto; padding: 20px; }
                    .header { background: linear-gradient(135deg, #667eea 0%%, #764ba2 100%%); color: white; padding: 30px; text-align: center; border-radius: 10px 10px 0 0; }
                    .content { background: #f9f9f9; padding: 30px; border-radius: 0 0 10px 10px; }
                    .otp-box { background: white; border: 2px solid #667eea; padding: 20px; text-align: center; font-size: 32px; font-weight: bold; letter-spacing: 8px; margin: 20px 0; border-radius: 5px; }
                    .footer { text-align: center; margin-top: 20px; color: #666; font-size: 12px; }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="header">
                        <h1>🔐 Email Verification</h1>
                    </div>
                    <div class="content">
                        <p>Welcome to Crypto Blog!</p>
                        <p>Use the verification code below to complete your registration:</p>
                        <div class="otp-box">%s</div>
                        <p><strong>This code will expire in %d minutes.</strong></p>
                        <p>If you didn't request this code, please ignore this email.</p>
                    </div>
                    <div class="footer">
                        <p>© 2025 Crypto Blog. All rights reserved.</p>
                    </div>
                </div>
            </body>
            </html>
            """, otp, expiryMinutes);
    }

    private static class OtpData {
        String otp;
        Instant expireAt;

        OtpData(String otp, Instant expireAt) {
            this.otp = otp;
            this.expireAt = expireAt;
        }
    }
}