package com.blog.cutom_blog.dtos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@JsonIgnoreProperties(ignoreUnknown = true)
public class InitiateRegistrationReqDto {

    @NotBlank(message = "is required")
    private String email;

    @NotBlank(message = "is required")
    private String firstName;

    @NotBlank(message = "is required")
    private String lastName;

    public InitiateRegistrationReqDto(final String email,
                                      final String firstName,
                                      final String lastName) {
        this.email = email.trim().toLowerCase();
        this.firstName = firstName.trim().toLowerCase();
        this.lastName = lastName.trim().toLowerCase();
    }
}
