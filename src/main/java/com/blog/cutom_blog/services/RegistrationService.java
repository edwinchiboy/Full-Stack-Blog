package com.blog.cutom_blog.services;

import com.blog.cutom_blog.commons.auth.dtos.AuthResponse;
import com.blog.cutom_blog.commons.comms.MailGunService;
import com.blog.cutom_blog.commons.comms.constants.OtpPurpose;
import com.blog.cutom_blog.commons.comms.dtos.EmailDto;
import com.blog.cutom_blog.commons.comms.dtos.EmailWithoutAttachmentDto;
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
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;


@Slf4j
@Service
public class RegistrationService {
    private final RegistrationRepository repository;
    private final UserService userService;
    private final AppProperties appProperties;
    private final MailGunService mailGunService;

    public RegistrationService(final RegistrationRepository repository, final UserService userService,
                               final AppProperties appProperties, final MailGunService mailGunService) {
        this.repository = repository;
        this.userService = userService;
        this.appProperties = appProperties;
        this.mailGunService = mailGunService;
    }

    public InitiateCustomerRegistrationResponseDto initiateRegistration(@Valid InitiateCustomerRegistrationReqDto initiateRegistrationRequestDto) {
        if (userService.existsByEmail(initiateRegistrationRequestDto.getEmail())) {
            throw new ConflictException("Duplicate username", "A user already exists with the supplied email", HttpErrorCode.EXISTING_EMAIL);
        }

        final Optional<Registration> pendingCustomerRegistrationOpt = repository.getByEmailIgnoreCase(initiateRegistrationRequestDto.getEmail());
        Registration fetchedCustomerRegistration = null;
        if (pendingCustomerRegistrationOpt.isPresent()) {
            fetchedCustomerRegistration = pendingCustomerRegistrationOpt.get();

        } else {
            final Set<RegistrationStep> completedRegistrationSteps = new HashSet<>();
            completedRegistrationSteps.add(RegistrationStep.SUBMIT_EMAIL);
            fetchedCustomerRegistration = this.repository.save(Registration.builder()
                .email(initiateRegistrationRequestDto.getEmail())
                .firstName(initiateRegistrationRequestDto.getFirstName())
                .lastName(initiateRegistrationRequestDto.getLastName())
                .completedRegistrationSteps(completedRegistrationSteps)
                .build());
        }

        final SendOtpResponse sendOtpResponse = mailGunService.sendEmail(EmailDto.builder()
//            .message("Use the confirmation code below to verify your email and proceed to complete your account setup.")
            .from(appProperties.getDefaultFromEmail())
            .to(List.of(fetchedCustomerRegistration.getEmail()))
            .subject()
            .body()
            .build());

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

        return this.communicationServiceClient.sendEmailOtp(EmailOTPRequest.builder()
            .message("Use the confirmation code below to verify your email and proceed to complete your account setup.")
            .from(appProperties.getDefaultFromEmail())
            .to(fetchedCustomerRegistration.getEmail())
            .tokenIdentifier(fetchedCustomerRegistration.getId())
            .otpPurpose(OtpPurpose.SIGNUP)
            .build());
    }

    public void validateOtp(final ValidateOtpRequestDto validateOtpRequestDto) {
        Registration customerRegistration = this.repository.findById(validateOtpRequestDto.getRegistrationId())
            .orElseThrow(() -> new NotFoundException("Unknown registration id"));

        if (!customerRegistration.getCompletedRegistrationSteps().contains(RegistrationStep.VERIFY_EMAIL)) {
            if (!communicationServiceClient.validateEmailOtp(validateOtpRequestDto.getOtp(), customerRegistration
                .getId())) {
                final String errorMessage = "Incorrect Otp";
                throw new ForbiddenException(errorMessage, errorMessage);
            }
            customerRegistration.getCompletedRegistrationSteps().add(RegistrationStep.VERIFY_EMAIL);
            this.repository.save(customerRegistration);
        }

    }

    @Transactional(propagation = Propagation.REQUIRES_NEW, isolation = Isolation.READ_COMMITTED)
    public CompleteSignUpResponseDto completeSignUp(final CompleteSignUpReqDto completeSignUpReqDto) {

        Registration customerRegistration = repository.findById(completeSignUpReqDto.getRegistrationId())
            .orElseThrow(() -> new NotFoundException("Unknown registration id"));

        if (!customerRegistration.getCompletedRegistrationSteps().contains(RegistrationStep.VERIFY_EMAIL)) {
            throw new ForbiddenException("Email verification pending. Please verify your email to complete registration.");
        }

        final AuthResponse authResponse = authServerClient
            .getToken(customerRegistration.getEmail(), completeSignUpReqDto.getPassword(), false);

        authResponse.getPrincipal().setAuthToken(authResponse.getToken());

        final User user = this.userService.createUser(customerRegistration, completeSignUpReqDto.getPassword());

        try {
            repository.delete(customerRegistration);
        } catch (final RuntimeException e) {
            log.error("Error saving referral and deleting registration record.");
        }

        return CompleteSignUpResponseDto.builder()
            .email(customerRegistration.getEmail())
            .registrationId(customerRegistration.getId())
            .authResponse(authResponse)
            .user(user)
            .build();
    }

}
