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


    public InitiateRegistrationReqDto(final String email,
                                      final String firstName,
                                      final String lastName) {
        this.email = email.trim().toLowerCase();
        this.firstName = firstName.trim().toLowerCase();
        this.lastName = lastName.trim().toLowerCase();
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

}
