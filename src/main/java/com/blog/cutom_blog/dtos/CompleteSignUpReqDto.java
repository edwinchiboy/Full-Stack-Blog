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
public class CompleteSignUpReqDto {

    @NotBlank(message = "registrationId is required")
    private String registrationId;

    @NotBlank(message = "password is required")
    private String password;

    public CompleteSignUpReqDto(final String registrationId,
                                final String password) {
        this.registrationId = registrationId;
        this.password = password;
    }
}
