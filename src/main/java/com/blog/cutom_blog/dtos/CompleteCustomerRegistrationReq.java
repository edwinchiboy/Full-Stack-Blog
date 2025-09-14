package com.blog.cutom_blog.dtos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Data
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@JsonIgnoreProperties(ignoreUnknown = true)
public class CompleteCustomerRegistrationReq {
    @NotBlank(message = "is required")
    private String customerRegistrationId;
    @NotBlank(message = "is required")
    private String password;

    public CompleteCustomerRegistrationReq(final String customerRegistrationId,
                                           final String password) {
        this.customerRegistrationId = customerRegistrationId.trim();
        this.password = password.trim();
    }

    public void setCustomerRegistrationId(final String customerRegistrationId) {
        this.customerRegistrationId = customerRegistrationId.trim();
    }

    public void setPassword(final String password) {
        this.password = password.trim();
    }
}
