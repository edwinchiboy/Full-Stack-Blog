package com.blog.cutom_blog.dtos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.NotBlank;
import lombok.*;


@Data
@Builder
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@JsonIgnoreProperties(ignoreUnknown = true)
public class InitiateRegistrationReqDto {

    @NotBlank(message = "is required")
    private String email;

    @NotBlank(message = "is required")
    private String firstName;

    @NotBlank(message = "is required")
    private String lastName;

    @NotBlank(message = "is required")
    private String businessName;

    private String phoneNumber;

    private String referrerCode;
    @NotBlank(message = "is required")
    private String paymentNeed;

    public InitiateRegistrationReqDto(final String email,
                                      final String firstName,
                                      final String lastName,
                                      final String businessName,
                                      final String phoneNumber,
                                      final String referrerCode, final String paymentNeed) {
        this.email = email.trim().toLowerCase();
        this.firstName = firstName.trim().toLowerCase();
        this.lastName = lastName.trim().toLowerCase();
        this.businessName = businessName.trim();
        this.phoneNumber = phoneNumber != null ? phoneNumber.trim() : null;
        this.referrerCode = referrerCode != null ? referrerCode.trim() : null;
        this.paymentNeed = paymentNeed;
    }

    public void setBusinessName(final String businessName) {
        this.businessName = businessName.trim();
    }

    public void setPhoneNumber(final String phoneNumber) {
        this.phoneNumber = phoneNumber.trim();
    }

    public void setEmail(final String email) {
        this.email = email.trim().toLowerCase();
    }

    public void setFirstName(final String firstName) {
        this.firstName = firstName.trim().toLowerCase();
    }

    public void setLastName(final String lastName) {
        this.lastName = lastName.trim().toLowerCase();
    }

    public void setReferrerCode(final String referrerCode) {
        this.referrerCode = referrerCode.trim();
    }

    public void setPaymentNeed(final String paymentNeed) {
        this.paymentNeed = paymentNeed.trim();
    }

}
