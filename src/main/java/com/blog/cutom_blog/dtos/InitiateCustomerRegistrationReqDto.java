package com.blog.cutom_blog.dtos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@JsonIgnoreProperties(ignoreUnknown = true)
public class InitiateCustomerRegistrationReqDto {
    @NotBlank(message = "is required")
    private String firstName;
    @NotBlank(message = "is required")
    private String lastName;


    @NotBlank(message = "is required")
    private String email;

    public InitiateCustomerRegistrationReqDto(final String firstName, final String lastName, final String email) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
    }




}
