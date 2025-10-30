package com.blog.cutom_blog.services;

import com.blog.cutom_blog.commons.comms.dtos.SendOtpResponse;
import com.blog.cutom_blog.config.AppProperties;
import com.blog.cutom_blog.constants.RegistrationStep;
import com.blog.cutom_blog.dtos.*;
import com.blog.cutom_blog.exceptions.ConflictException;
import com.blog.cutom_blog.exceptions.ForbiddenException;
import com.blog.cutom_blog.exceptions.NotFoundException;
import com.blog.cutom_blog.models.Registration;
import com.blog.cutom_blog.models.User;
import com.blog.cutom_blog.repositories.RegistrationRepository;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;


@Slf4j
@Service
public class RegistrationService {
    private final RegistrationRepository repository;
    private final UserService userService;
    private final AppProperties appProperties;
    private final OtpService otpService;

    public RegistrationService(final RegistrationRepository repository, final UserService userService,
                               final AppProperties appProperties, final OtpService otpService) {
        this.repository = repository;
        this.userService = userService;
        this.appProperties = appProperties;
        this.otpService = otpService;
    }

    public InitiateCustomerRegistrationResponseDto initiateRegistration(@Valid InitiateCustomerRegistrationReqDto initiateRegistrationRequestDto) {
        if (userService.existsByEmail(initiateRegistrationRequestDto.getEmail())) {
            throw new ConflictException("Duplicate email", "A user already exists with the supplied email");
        }

        final Optional<Registration> pendingCustomerRegistrationOpt = repository.getByEmailIgnoreCase(initiateRegistrationRequestDto.getEmail());
        Registration fetchedCustomerRegistration;
        if (pendingCustomerRegistrationOpt.isPresent()) {
            fetchedCustomerRegistration = pendingCustomerRegistrationOpt.get();
        } else {
            final Set<RegistrationStep> completedRegistrationSteps = new HashSet<>();
            completedRegistrationSteps.add(RegistrationStep.SUBMIT_EMAIL);

            // Auto-verify email in development mode
            if (appProperties.isSkipEmailVerification()) {
                completedRegistrationSteps.add(RegistrationStep.VERIFY_EMAIL);
                log.info("Email verification skipped for development mode: {}", initiateRegistrationRequestDto.getEmail());
            }

            fetchedCustomerRegistration = this.repository.save(Registration.builder()
                .email(initiateRegistrationRequestDto.getEmail())
                .firstName(initiateRegistrationRequestDto.getFirstName())
                .lastName(initiateRegistrationRequestDto.getLastName())
                .completedRegistrationSteps(completedRegistrationSteps)
                .build());
        }

        SendOtpResponse sendOtpResponse;

        // Skip sending OTP email in development mode
        if (appProperties.isSkipEmailVerification()) {
            log.info("Skipping OTP email send for development mode");
            sendOtpResponse = SendOtpResponse.builder()
                .durationToExpireMinutes(10)
                .durationToExpireSeconds(600)
                .expireAt(java.time.Instant.now().plusSeconds(600))
                .issuedAt(java.time.Instant.now())
                .build();
        } else {
            sendOtpResponse = otpService.generateAndSendOtp(
                fetchedCustomerRegistration.getEmail(),
                fetchedCustomerRegistration.getId()
            );
        }

        return InitiateCustomerRegistrationResponseDto.builder()
            .registrationId(fetchedCustomerRegistration.getId())
            .durationToExpireMinutes(sendOtpResponse.getDurationToExpireMinutes())
            .durationToExpireSeconds(sendOtpResponse.getDurationToExpireSeconds())
            .expireAt(sendOtpResponse.getExpireAt())
            .issuedAt(sendOtpResponse.getIssuedAt())
            .build();
    }

    public SendOtpResponse sendEmailOtp(final String registrationId) {
        Registration fetchedCustomerRegistration = this.repository.findById(registrationId)
            .orElseThrow(() -> new NotFoundException("Unknown registration id"));

        return otpService.generateAndSendOtp(
            fetchedCustomerRegistration.getEmail(),
            fetchedCustomerRegistration.getId()
        );
    }

    public void validateOtp(final ValidateOtpRequestDto validateOtpRequestDto) {
        Registration customerRegistration = this.repository.findById(validateOtpRequestDto.getRegistrationId())
            .orElseThrow(() -> new NotFoundException("Unknown registration id"));

        if (!customerRegistration.getCompletedRegistrationSteps().contains(RegistrationStep.VERIFY_EMAIL)) {
            if (!otpService.validateOtp(validateOtpRequestDto.getOtp(), customerRegistration.getId())) {
                final String errorMessage = "Incorrect OTP";
                throw new ForbiddenException(errorMessage, errorMessage);
            }
            customerRegistration.getCompletedRegistrationSteps().add(RegistrationStep.VERIFY_EMAIL);
            this.repository.save(customerRegistration);
        }
    }

    @Transactional
    public CompleteSignUpResponseDto completeSignUp(final CompleteSignUpReqDto completeSignUpReqDto) {

        Registration customerRegistration = repository.findById(completeSignUpReqDto.getRegistrationId())
            .orElseThrow(() -> new NotFoundException("Unknown registration id"));

        if (!customerRegistration.getCompletedRegistrationSteps().contains(RegistrationStep.VERIFY_EMAIL)) {
            throw new ForbiddenException("Email verification pending. Please verify your email to complete registration.");
        }

        final User user = this.userService.createReaderUser(customerRegistration, completeSignUpReqDto.getPassword());

        // Delete registration record after user creation
        repository.delete(customerRegistration);
        repository.flush(); // Ensure deletion is committed

        return CompleteSignUpResponseDto.builder()
            .email(user.getEmail())
            .userId(user.getId())
            .message("Registration completed successfully. Please login to continue.")
            .build();
    }

    @Transactional
    public CompleteSignUpResponseDto completeAdminSignUp(final CompleteSignUpReqDto completeSignUpReqDto) {

        Registration customerRegistration = repository.findById(completeSignUpReqDto.getRegistrationId())
            .orElseThrow(() -> new NotFoundException("Unknown registration id"));

        if (!customerRegistration.getCompletedRegistrationSteps().contains(RegistrationStep.VERIFY_EMAIL)) {
            throw new ForbiddenException("Email verification pending. Please verify your email to complete registration.");
        }

        final User user = this.userService.createAdminUserFromRegistration(customerRegistration, completeSignUpReqDto.getPassword());

        // Delete registration record after user creation
        repository.delete(customerRegistration);
        repository.flush(); // Ensure deletion is committed

        return CompleteSignUpResponseDto.builder()
            .email(user.getEmail())
            .userId(user.getId())
            .message("Admin registration completed successfully. Please login to continue.")
            .build();
    }

}
