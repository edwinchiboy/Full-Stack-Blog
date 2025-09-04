package com.blog.cutom_blog.dtos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.EqualsAndHashCode;


@Data
@Builder
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ValidateOtpParam {

    @NotBlank(message = "is required")
    private String wireAccountRegistrationId;

    @NotBlank(message = "is required")
    private String otp;

    public ValidateOtpParam(final String wireAccountRegistrationId,
                            final String otp) {
        this.wireAccountRegistrationId = wireAccountRegistrationId.trim();
        this.otp = otp.trim();
    }

    public void setWireAccountRegistrationId(final String wireAccountRegistrationId) {
        this.wireAccountRegistrationId = wireAccountRegistrationId.trim();
    }

    public void setOtp(final String otp) {
        this.otp = otp.trim();
    }
}
