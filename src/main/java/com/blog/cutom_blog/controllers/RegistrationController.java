package com.blog.cutom_blog.controllers;



import com.blog.cutom_blog.commons.comms.dtos.SendOtpResponse;
import com.blog.cutom_blog.dtos.*;
import com.blog.cutom_blog.services.RegistrationService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;



@RestController
@RequestMapping("/v1/registration")
public class RegistrationController {
    private final RegistrationService registrationService;

    public RegistrationController(final RegistrationService registrationService) {
        this.registrationService = registrationService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<?>> initiateRegistration(@Valid @RequestBody InitiateCustomerRegistrationReqDto initiateCustomerRegistrationReqDto) {

        final InitiateCustomerRegistrationResponseDto initiateRegistration = registrationService.initiateRegistration(initiateCustomerRegistrationReqDto);

        return ResponseEntity.status(HttpStatus.OK)
            .body(
                ApiResponse.builder()
                    .data(initiateRegistration)
                    .message("Success")
                    .build()
            );
    }
    @PutMapping("/{registrationId}/resend-email-otp")
    public ResponseEntity<ApiResponse<?>> resendEmailOtp(@PathVariable final String registrationId){

        final SendOtpResponse response = this.registrationService.sendEmailOtp(registrationId);

        return ResponseEntity.status(HttpStatus.OK)
            .body(ApiResponse.builder()
                .data(response)
                .build());
    }


    @PutMapping("/validate-otp")
    public ResponseEntity<ApiResponse<?>> validateOtp(final @RequestBody ValidateOtpRequestDto validateOtpRequestDto){

         registrationService.validateOtp(validateOtpRequestDto);

        return ResponseEntity.status(HttpStatus.OK).body(
            ApiResponse.builder()
                .message("Success")
                .build()
        );
    }

    @PutMapping("/complete-sign-up")
    public ResponseEntity<ApiResponse<?>> completeSignUp(final @RequestBody CompleteSignUpReqDto completeSignUpReqDto){

        final CompleteSignUpResponseDto submitPasswordResponseDto = registrationService
            .completeSignUp(completeSignUpReqDto);

        return ResponseEntity.status(HttpStatus.OK).body(
            ApiResponse.builder()
                .data(submitPasswordResponseDto)
                .message("Success")
                .build()
        );
    }

    @PutMapping("/complete-admin-sign-up")
    public ResponseEntity<ApiResponse<?>> completeAdminSignUp(final @RequestBody CompleteSignUpReqDto completeSignUpReqDto){

        final CompleteSignUpResponseDto submitPasswordResponseDto = registrationService
            .completeAdminSignUp(completeSignUpReqDto);

        return ResponseEntity.status(HttpStatus.OK).body(
            ApiResponse.builder()
                .data(submitPasswordResponseDto)
                .message("Success")
                .build()
        );
    }

}
