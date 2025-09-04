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
public class CompleteRegistrationReq {

    @NotBlank(message = "is required")
    private String wireAccountRegistrationId;

    @NotBlank(message = "is required")
    private String password;

    public CompleteRegistrationReq(final String wireAccountRegistrationId,
                                   final String password) {
        this.wireAccountRegistrationId = wireAccountRegistrationId.trim();
        this.password = password.trim();
    }

    public void setWireAccountRegistrationId(final String wireAccountRegistrationId) {
        this.wireAccountRegistrationId = wireAccountRegistrationId.trim();
    }

    public void setPassword(final String password) {
        this.password = password.trim();
    }
}
