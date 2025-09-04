package com.blog.cutom_blog.services;

import com.blog.cutom_blog.models.Registration;
import com.blog.cutom_blog.repositories.RegistrationRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
public class RegistrationService {
    private final RegistrationRepository repository;
    private final UserService userService;

    public RegistrationService(final RegistrationRepository repository, final UserService userService) {
        this.repository = repository;
        this.userService = userService;
    }


    public Registration initiateRegistration(final InitiateRegistrationReqDto param) {
        if (this.userService.existsByEmail(param.getEmail())) {
            throw new ConflictException(
                "Duplicate wire account email",
                "An account already exists with this email. Kindly contact support if you did not create an account."
            );
        }

        if (this.authServerClient.existsUserByEmail(param.getEmail())) {
            throw new ConflictException(
                "An auth server user already exists with supplied email",
                "An account already exists with this email. Kindly contact support if you did not create an account."
            );
        }

        String normalisedPhoneNumber = null;

        if (!StringUtils.isBlank(param.getPhoneNumber())) {
            normalisedPhoneNumber = PhoneNumberUtils.formatAsInternational(
                    param.getPhoneNumber(), AppConstants.DEFAULT_PHONE_NUMBER_REGION
                )
                .orElseGet(param::getPhoneNumber);

            if (!PhoneNumberUtils.isValid(normalisedPhoneNumber, AppConstants.DEFAULT_PHONE_NUMBER_REGION)) {
                throw new BadRequestException("Phone number is not a valid number.");
            }
        }


        final WireAccountRegistration wireAccountRegistration = this.repository.getByEmail(param.getEmail())
            .orElse(
                WireAccountRegistration.builder()
                    .email(param.getEmail())
                    .build()
            );

        wireAccountRegistration.setFirstName(param.getFirstName());
        wireAccountRegistration.setLastName(param.getLastName());
        wireAccountRegistration.setEmail(param.getEmail());
        wireAccountRegistration.setPhoneNumber(normalisedPhoneNumber);
        wireAccountRegistration.setBusinessName(param.getBusinessName());
        wireAccountRegistration.setOwnershipType(WireAccountOwnershipType.BUSINESS);
        wireAccountRegistration.setPaymentNeed(param.getPaymentNeed());
        wireAccountRegistration.setWireAccountRegistrationStatus(WireAccountRegistrationStatus.INITIATED);

        if (StringUtils.isBlank(wireAccountRegistration.getActivationCode())) {
            wireAccountRegistration.setActivationCode(UUID.randomUUID().toString());
        }

        if (!StringUtils.isBlank(param.getReferrerCode()) && wireAccount.isPresent()) {
            wireAccountRegistration.setReferrerType(ReferrerType.WB);
            wireAccountRegistration.setReferrerTag(wireAccount.get().getTag());
            if (wireAccountRegistration.getSubjectName() == null) {
                wireAccountRegistration.setSubjectName(param.getBusinessName());
            }
            if (wireAccountRegistration.getWireAccountType() == null) {
                wireAccountRegistration.setWireAccountType(WireAccountType.REGULAR);
            }
        }

        final WireAccountRegistration savedWireAccountRegistration = this.repository.save(wireAccountRegistration);

        if (sendOtp) {
            try {
                this.sendEmailOtp(savedWireAccountRegistration.getId());
            } catch (final Exception e) {
                log.error("Error sending wire account registration OTP ", e);
                this.errorLoggingService.captureException(e);
            }
        }
        try {
            this.communicationServiceClient.sendToSlack(SlackMessageRequest.builder()
                .channel(this.appProperties.getWireRegistrationRequestSlackChannel())
                .text(SlackMessagingUtil.newWireRegistration(savedWireAccountRegistration))
                .build());
        } catch (final Exception e) {
            log.error("Error sending wire account registration request to Slack", e);
            this.errorLoggingService.captureException(e);
        }
        return savedWireAccountRegistration;
    }


    public WireAccount wireAccountRegistrationByAdmin(final WireAccountRegistrationByAdminDto param) {

        if (this.wireUserService.existsByEmail(param.getEmail())) {
            throw new ConflictException(
                "Duplicate wire account email",
                "An account already exists with this email. Kindly contact support if you did not create an account."
            );
        }

        if (this.authServerClient.existsUserByEmail(param.getEmail())) {
            throw new ConflictException(
                "An auth server user already exists with supplied email",
                "An account already exists with this email. Kindly contact support if you did not create an account."
            );
        }

        String normalisedPhoneNumber = null;

        if (!StringUtils.isBlank(param.getPhoneNumber())) {
            normalisedPhoneNumber = PhoneNumberUtils.formatAsInternational(
                    param.getPhoneNumber(), AppConstants.DEFAULT_PHONE_NUMBER_REGION
                )
                .orElseGet(param::getPhoneNumber);

            if (!PhoneNumberUtils.isValid(normalisedPhoneNumber, AppConstants.DEFAULT_PHONE_NUMBER_REGION)) {
                throw new BadRequestException("Phone number is not a valid number.");
            }
        }

        SignUpUserByAdminResponseDto signUpUserByAdminResponseDto = authServerClient.signUpUserByAdmin(RegistrationRequestByAdminDto.builder()
            .email(param.getEmail())
            .authenticateWith(AuthenticateWith.EMAIL)
            .firstName(param.getFirstName())
            .lastName(param.getLastName())
            .build());

        final AuthResponse authResponse = authServerClient
            .getToken(param.getEmail(), signUpUserByAdminResponseDto.getDummyPassword(), false);

        final WireAccount wireAccount = this.wireAccountService.createWireAccount(CreateWireAccountParam.builder()
            .name(param.getBusinessName())
            .ownershipType(WireAccountOwnershipType.BUSINESS)
            .pricingUserGroup(UserGroup.Default)
            .creatorAuthToken(authResponse.getToken())
            .phoneNumber(normalisedPhoneNumber)
            .forcedBrokerAccessEligibilityStatus(ForcedBrokerAccessEligibilityStatus.NEUTRAL)
            .build());

        final Optional<ResourceTypeDto> resourceTypeOpt = this.authServerClient.getResourceTypeByName(
            ResourceType.wire_account.name());
        if (resourceTypeOpt.isPresent()) {
            final ResourceTypeDto resourceType = resourceTypeOpt.get();

            final Optional<BriefResourceRoleDto> resourceRoleOpt = this.authServerClient.getResourceRoleByNameAndResourceTypeId(
                WireDefaultRoles.owner.name(), resourceType.getId());
            if (resourceRoleOpt.isEmpty()) {
                log.warn("Could not get admin resource role.");
            } else {
                final BriefResourceRoleDto resourceRole = resourceRoleOpt.get();

                this.authServerClient.createUserResourceRoles(PrincipalResourceRoleRequestDto.builder()
                    .principal(signUpUserByAdminResponseDto.getUserId())
                    .resourceId(wireAccount.getId())
                    .resourceRoleId(resourceRole.getId())
                    .resourceTypeId(resourceType.getId())
                    .build());
            }
        } else {
            log.warn("Could not get admin resource type.");
        }


        this.wireUserService.createWireUser(CreateWireUserParam.builder()
            .firstName(param.getFirstName())
            .lastName(param.getLastName())
            .phoneNumber(normalisedPhoneNumber)
            .email(param.getEmail())
            .wireAccountId(wireAccount.getId())
            .principalId(signUpUserByAdminResponseDto.getUserId())
            .build());

        addCreatorAccountToEmailPreference(wireAccount.getId(), param.getEmail());


        try {
            this.communicationServiceClient.sendToSlack(SlackMessageRequest.builder()
                .channel(this.appProperties.getWireRegistrationRequestSlackChannel())
                .text(SlackMessagingUtil.newWireRegistration(WireAccountRegistration
                    .builder()
                    .id("Not applicable")
                    .firstName(param.getFirstName())
                    .lastName(param.getLastName())
                    .email(param.getEmail())
                    .phoneNumber(normalisedPhoneNumber)
                    .businessName(param.getBusinessName())
                    .paymentNeed(param.getPaymentNeed())
                    .build()))
                .build());
        } catch (final Exception e) {
            log.error("Error sending wire account registration request to Slack", e);
            this.errorLoggingService.captureException(e);
        }

        try {
            this.wireEmailService.sendYourAccountAlmostReadyEmail(param.getBusinessName(), param.getEmail(), signUpUserByAdminResponseDto.getPasswordResetId());
        } catch (Exception e) {
            log.error("Error welcoming user through Email ", e);
            this.errorLoggingService.captureException(e);
        }
        return wireAccount;

    }

    public SendOtpResponse sendEmailOtp(final String wireAccountRegistrationId) {
        final WireAccountRegistration wireAccountRegistration = this.getOne(wireAccountRegistrationId)
            .orElseThrow(() -> new NotFoundException("Unknown partner id", "Invalid partner id"));

        return this.communicationServiceClient.sendEmailOtp(EmailOTPRequest.builder()
            .from(appProperties.getDefaultFromEmail())
            .to(wireAccountRegistration.getEmail())
            .tokenIdentifier(wireAccountRegistration.getId())
            .otpPurpose(OtpPurpose.SIGNUP)
            .build());
    }

    public void validateEmail(final ValidateOtpParam param) {
        final WireAccountRegistration wireAccountRegistration = this.getOne(param.getWireAccountRegistrationId())
            .orElseThrow(() -> new NotFoundException("Unknown partner id", "Invalid partner id"));
        if (!this.communicationServiceClient.validateEmailOtp(param.getOtp(), wireAccountRegistration.getId())) {
            final String errorMessage = "Incorrect otp";
            throw new ForbiddenException(errorMessage, errorMessage);
        }

        wireAccountRegistration.setEmailVerified(true);
        this.save(wireAccountRegistration);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public AuthResponse completeWireAccountRegistration(final CompleteWireAccountRegistrationWithActivationCodeReq param) {
        final WireAccountRegistration wireAccountRegistration = this.repository.getByActivationCode(param.getActivationCode())
            .orElseThrow(() -> new UnprocessableEntityException("Invalid activation code", "Invalid activation code"));

        if (!wireAccountRegistration.isEmailVerified()) {
            wireAccountRegistration.setEmailVerified(true);
            this.save(wireAccountRegistration);
        }


        return this.completeWireAccountRegistration(CompleteWireAccountRegistrationReq.builder()
            .wireAccountRegistrationId(wireAccountRegistration.getId())
            .password(param.getPassword())
            .build());
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public AuthResponse completeWireAccountRegistration(final CompleteWireAccountRegistrationReq param) {
        final WireAccountRegistration wireAccountRegistration = this.getOne(param.getWireAccountRegistrationId())
            .orElseThrow(() -> new NotFoundException("Unknown partner id", "Invalid partner id"));

        if (!wireAccountRegistration.isEmailVerified()) {
            final String message = "Please verify your email before completing this step.";
            throw new PreConditionFailedException(message, message);
        }

        UserDto userDto = this.authServerClient.getUserByEmail(wireAccountRegistration.getEmail())
            .orElseGet(() -> authServerClient.initRegistration(RegistrationRequestDto.builder()
                .email(wireAccountRegistration.getEmail())
                .authenticateWith(AuthenticateWith.EMAIL)
                .password(param.getPassword())
                .build())
            );

        AuthResponse authResponse = authServerClient
            .getToken(wireAccountRegistration.getEmail(), param.getPassword(), false);

        if (StringUtils.isBlank(userDto.getLastName())) {
            userDto = authServerClient.updatePersonalInfo(UpdatePersonalInfoRequestDto.builder()
                .firstName(wireAccountRegistration.getFirstName())
                .lastName(wireAccountRegistration.getLastName())
                .userId(userDto.getId())
                .build(), authResponse.getToken());
        }

        final WireAccount wireAccount = this.wireAccountService.createWireAccount(CreateWireAccountParam.builder()
            .name(wireAccountRegistration.getBusinessName())
            .ownershipType(WireAccountOwnershipType.BUSINESS)
            .pricingUserGroup(UserGroup.Default)
            .creatorAuthToken(authResponse.getToken())
            .phoneNumber(wireAccountRegistration.getPhoneNumber())
            .forcedBrokerAccessEligibilityStatus(wireAccountRegistration.getWireAccountType() != null && wireAccountRegistration.getWireAccountType().isBroker() ? ForcedBrokerAccessEligibilityStatus.ON : ForcedBrokerAccessEligibilityStatus.NEUTRAL)
            .build());

        final Optional<ResourceTypeDto> resourceTypeOpt = this.authServerClient.getResourceTypeByName(
            ResourceType.wire_account.name());
        if (resourceTypeOpt.isPresent()) {
            final ResourceTypeDto resourceType = resourceTypeOpt.get();

            final Optional<BriefResourceRoleDto> resourceRoleOpt = this.authServerClient.getResourceRoleByNameAndResourceTypeId(
                WireDefaultRoles.owner.name(), resourceType.getId());
            if (resourceRoleOpt.isEmpty()) {
                log.warn("Could not get admin resource role.");
            } else {
                final BriefResourceRoleDto resourceRole = resourceRoleOpt.get();

                this.authServerClient.createUserResourceRoles(PrincipalResourceRoleRequestDto.builder()
                    .principal(userDto.getId())
                    .resourceId(wireAccount.getId())
                    .resourceRoleId(resourceRole.getId())
                    .resourceTypeId(resourceType.getId())
                    .build());
            }
        } else {
            log.warn("Could not get admin resource type.");
        }


        this.wireUserService.createWireUser(CreateWireUserParam.builder()
            .firstName(wireAccountRegistration.getFirstName())
            .lastName(wireAccountRegistration.getLastName())
            .phoneNumber(wireAccountRegistration.getPhoneNumber())
            .email(wireAccountRegistration.getEmail())
            .wireAccountId(wireAccount.getId())
            .principalId(userDto.getId())
            .build());

        addCreatorAccountToEmailPreference(wireAccount.getId(), wireAccountRegistration.getEmail());

        if ((wireAccountRegistration.getReferrerType() != null) && (!StringUtils.isBlank(wireAccountRegistration.getReferrerTag()))) {
            try {
                final Referrer referrer = referralService.parseReferralCode(String.format("%s-%s", wireAccountRegistration.getReferrerType().name(), wireAccountRegistration.getReferrerTag()));
                this.referralService.createReferral(CreateReferralParam.builder()
                    .referrerId(referrer.getId())
                    .referrerType(referrer.getType())
                    .subjectId(wireAccount.getId())
                    .subjectName(wireAccountRegistration.getSubjectName())
                    .subjectType(ReferralSubjectType.WIRE_ACCOUNT)
                    .commissionFlag(referrer.getCommissionFlag())
                    .build());
            } catch (final Exception e) {
                log.error("Error creating referral record for referrer code: {}", String.format("%s-%s", wireAccountRegistration.getReferrerType().name(), wireAccountRegistration.getReferrerTag()), e);
            }
        }
        if (wireAccountRegistration.getWireAccountType() != null && wireAccountRegistration.getWireAccountType().isBroker()) {
            wireAccountService.updateBrokerAccessStatus(
                UpdateBrokerAccessStatusReqDto
                    .builder()
                    .brokerAccessStatus(BrokerAccessStatus.REQUESTED)
                    .wireAccountId(wireAccount.getId())
                    .build()
            );
        }

        try {
            authServerClient.enableTwoFactorAuthChannel(EnableTwoFaChannelStatusUpdateRequest.builder()
                .twoFactorAuthChannel(TwoFactorAuthChannel.EMAIL)
                .makeDefault(true)
                .build(), authResponse.getToken());
        } catch (Exception e) {
            log.error("Error setting email as default two FA channel ", e);
            this.errorLoggingService.captureException(e);
        }

        try {
            this.wireEmailService.sendBusinessWelcome(wireAccountRegistration.getBusinessName(), wireAccountRegistration.getEmail());
        } catch (Exception e) {
            log.error("Error welcoming user through Email ", e);
            this.errorLoggingService.captureException(e);
        }

        wireAccountRegistration.setWireAccountRegistrationStatus(WireAccountRegistrationStatus.COMPLETED);


        this.repository.save(wireAccountRegistration);

        authResponse = authServerClient.getToken(wireAccountRegistration.getEmail(), param.getPassword(), false);

        authResponse.setAccountStatus(WireAccountStatus.ACTIVE);

        return authResponse;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public WireAccount activateForMerchant(final String merchantId,
                                           @Nullable final String phoneNumber) {
        final Merchant merchant = merchantService.getOne(merchantId)
            .orElseThrow(() -> new UnprocessableEntityException("Attempt to activate invalid merchant on Zilla Wire", "Invalid merchant details"));
        final Optional<WireAccount> wireAccountOpt = this.wireAccountService.findByCreatorId(merchant.getCreatorId());
        if (wireAccountOpt.isPresent()) {
            return wireAccountOpt.get();
        }

        MerchantUser merchantUser = merchantUserService.getOne(merchant.getCreatorId())
            .orElseThrow(() -> new BnplServiceException(
                String.format("Merchant User record not found for the creator of merchant (id: %s)", merchant.getId()),
                true,
                AppConstants.DEFAULT_PRETTY_ERROR_MESSAGE
            ));

        final Optional<ResourceTypeDto> resourceTypeOpt = this.authServerClient.getResourceTypeByName(
            ResourceType.zilla_admin.name());
        if (resourceTypeOpt.isPresent()) {
            final ResourceTypeDto resourceType = resourceTypeOpt.get();

            final Optional<BriefResourceRoleDto> resourceRoleOpt = this.authServerClient.getResourceRoleByNameAndResourceTypeId(
                WireDefaultRoles.owner.name(), resourceType.getId());
            if (resourceRoleOpt.isEmpty()) {
                log.warn("Could not get admin resource role.");
            } else {
                final BriefResourceRoleDto resourceRole = resourceRoleOpt.get();

                this.authServerClient.createUserResourceRoles(PrincipalResourceRoleRequestDto.builder()
                    .principal(merchantUser.getId())
                    .resourceId(AuthConstants.ADMIN_RESOURCE_ID)
                    .resourceRoleId(resourceRole.getId())
                    .resourceTypeId(resourceType.getId())
                    .build());
            }
        } else {
            log.warn("Could not get admin resource type.");
        }

        if (StringUtils.isBlank(merchantUser.getPhoneNumber()) && StringUtils.isBlank(phoneNumber)) {
            throw new BadRequestException("Merchant phone number is required", "Merchant phone number is required");
        }

        final AuthResponse authResponse = authServerClient.getToken(merchantUser.getEmail());

        final WireAccount wireAccount = this.wireAccountService.createWireAccount(CreateWireAccountParam.builder()
            .name(merchant.getName())
            .ownershipType(WireAccountOwnershipType.BUSINESS)
            .pricingUserGroup(UserGroup.Default)
            .creatorAuthToken(authResponse.getToken())
            .forcedBrokerAccessEligibilityStatus(ForcedBrokerAccessEligibilityStatus.NEUTRAL)
            .build());

        if (StringUtils.isBlank(merchantUser.getPhoneNumber())) {
            final String formattedPhone = PhoneNumberUtils.formatAsInternational(phoneNumber, AppConstants.DEFAULT_PHONE_NUMBER_REGION)
                .orElseThrow(() -> new BadRequestException("Invalid phone number", "Invalid phone number"));
            merchantUser = this.merchantUserService.updatePhoneNumber(merchantUser.getId(), formattedPhone);
        }

        this.wireUserService.createWireUser(CreateWireUserParam.builder()
            .firstName(merchantUser.getFirstName())
            .lastName(merchantUser.getLastName())
            .phoneNumber(merchantUser.getPhoneNumber())
            .email(merchantUser.getEmail())
            .wireAccountId(wireAccount.getId())
            .principalId(merchantUser.getId())
            .build());

        return wireAccount;
    }

    public Optional<WireAccountRegistration> getByEmail(String email) {
        return this.repository.getByEmail(email);
    }

    public void deleteById(String id) {
        this.repository.deleteById(id);
    }

    public void addCreatorAccountToEmailPreference(String wireAccountId, String email) {
        for (PreferenceEmailType preferenceEmailType : PreferenceEmailType.values()) {
            wireContactEmailPreferenceService.activateEmail(WireContactEmailPreferenceRequestDto.builder()
                .wireAccountId(wireAccountId)
                .email(email)
                .preferenceEmailType(preferenceEmailType)
                .build());
        }

    }

    public Page<WireAccountRegistration> getAllByCreatedAtBetweenWithInitiatedFirst(final Instant from,
                                                                                    final Instant to,
                                                                                    final Pageable pageable) {
        return repository.getAllByCreatedAtBetweenWithInitiatedFirst(from, to, pageable);
    }

}
