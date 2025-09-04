package com.blog.cutom_blog.controllers;

import com.zilla.bnpl_service.config.auth.AuthorizationService;
import com.zilla.bnpl_service.core.admin.authorization.constants.Authorities;
import com.zilla.bnpl_service.core.commons.clients.auth_server.dtos.AuthResponse;
import com.zilla.bnpl_service.core.commons.dtos.PaginatedFetchParams;
import com.zilla.bnpl_service.core.wire.account.models.WireAccount;
import com.zilla.bnpl_service.core.wire.registration.dtos.*;
import com.zilla.bnpl_service.core.wire.registration.model.WireAccountRegistration;
import com.zilla.bnpl_service.core.wire.registration.services.WireAccountRegistrationService;
import com.zilla.bnpl_service.utils.Utils;
import com.zilla.commons.dtos.ApiResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/v1/wire-account-registration")
public class WireAccountRegistrationController {

    private final AuthorizationService authorizationService;
    private final WireAccountRegistrationService wireAccountRegistrationService;

    public WireAccountRegistrationController(final AuthorizationService authorizationService,
                                             final WireAccountRegistrationService wireAccountRegistrationService) {
        this.authorizationService = authorizationService;
        this.wireAccountRegistrationService = wireAccountRegistrationService;
    }


    @PostMapping
    public ResponseEntity<ApiResponse<?>> initiateWireRegistration(@Valid @RequestBody final InitiateWireAccountRegistrationDto body){

        final WireAccountRegistration wireAccountRegistration = this.wireAccountRegistrationService.initiateWireAccountRegistration(body, true);
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.builder()
                .data(WireAccountRegistrationResponseDto.builder()
                    .id(wireAccountRegistration.getId())
                    .firstName(wireAccountRegistration.getFirstName())
                    .lastName(wireAccountRegistration.getLastName())
                    .email(wireAccountRegistration.getEmail())
                    .phoneNumber(wireAccountRegistration.getPhoneNumber())
                    .businessName(wireAccountRegistration.getBusinessName())
                    .build())
                .build());
    }
    @PostMapping("/admin")
    public ResponseEntity<ApiResponse<?>> wireRegistrationByAdmin(@Valid @RequestBody final WireAccountRegistrationByAdminDto body){

        WireAccount response  =  this.wireAccountRegistrationService.wireAccountRegistrationByAdmin(body);

        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.builder()
                .message("Success")
                .data(response)
                .build());
    }

    @PutMapping("/{wireAccountRegistrationId}/send-email-otp")
    public ResponseEntity<ApiResponse<?>> sendEmailOtp(@PathVariable final String wireAccountRegistrationId){
        final var res = this.wireAccountRegistrationService.sendEmailOtp(wireAccountRegistrationId);
        return ResponseEntity.status(HttpStatus.OK)
            .body(ApiResponse.builder()
                .data(res)
                .build());
    }

    @PutMapping("/validate-email")
    public ResponseEntity<ApiResponse<?>> validateEmailOtp(@Valid @RequestBody final ValidateOtpParam body){
        this.wireAccountRegistrationService.validateEmail(body);
        return ResponseEntity.status(HttpStatus.OK)
            .body(ApiResponse.builder()
                .message("Success")
                .build());
    }

    @PutMapping("/complete-registration")
    public ResponseEntity<ApiResponse<?>> completeWireAccountSignup(@Valid @RequestBody final CompleteWireAccountRegistrationReq body){
        final AuthResponse authResponse = this.wireAccountRegistrationService.completeWireAccountRegistration(body);
        return ResponseEntity.status(HttpStatus.OK)
            .body(ApiResponse.builder()
                .data(authResponse)
                .build());
    }

    @PutMapping("/complete-registration-with-activation-code")
    public ResponseEntity<ApiResponse<?>> completeWireAccountSignup(@Valid @RequestBody final CompleteWireAccountRegistrationWithActivationCodeReq body){
        final AuthResponse authResponse = this.wireAccountRegistrationService.completeWireAccountRegistration(body);
        return ResponseEntity.status(HttpStatus.OK)
            .body(ApiResponse.builder()
                .data(authResponse)
                .build());
    }

    @PutMapping("/activate-merchant/merchant-id/{merchantId}")
    public ResponseEntity<ApiResponse<?>> activateWireForMerchant(@Valid @PathVariable final String merchantId,
                                                                  @RequestParam(required = false) final String phoneNumber){

        this.authorizationService.enforceAllPermissions(Authorities.WIRE_REGISTRATION_ACTIVATE_MERCHANT);

        final WireAccount wireAccount = this.wireAccountRegistrationService.activateForMerchant(merchantId, phoneNumber);
        return ResponseEntity.status(HttpStatus.OK)
            .body(ApiResponse.builder()
                .data(wireAccount)
                .build());
    }

    @GetMapping
    public ResponseEntity<ApiResponse<?>> getAll(final PaginatedFetchParams queryParams){

        this.authorizationService.enforceAllPermissions(Authorities.WIRE_REGISTRATION_VIEW);

        final Pageable pageable = Utils.defaultPageRequest(queryParams.getPage(), queryParams.getPageSize());
        final Page<WireAccountRegistration> partnerRegistrations = this.wireAccountRegistrationService
            .getAllByCreatedAtBetweenWithInitiatedFirst(queryParams.getFrom(), queryParams.getTo(), pageable);
        final ApiResponse<?> responseData = ApiResponse.builder().data(partnerRegistrations.getContent())
            .build()
            .addPaginationMeta(partnerRegistrations.getNumber(), partnerRegistrations.getSize(),
                partnerRegistrations.getTotalPages(), partnerRegistrations.getTotalElements());
        return ResponseEntity.ok(responseData);
    }
}
