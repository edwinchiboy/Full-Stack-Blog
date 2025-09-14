package com.blog.cutom_blog.dtos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;



@Data
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ValidateOtpRequestDto {
    @NotBlank(message = "registrationId is required")
    private String registrationId;

    @NotBlank(message = "otp is required")
    private String otp;

    public ValidateOtpRequestDto(final String registrationId,
                                 final String otp) {
        this.registrationId = registrationId;
        this.otp = otp;
    }
}
