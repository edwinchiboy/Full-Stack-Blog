package com.blog.cutom_blog.dtos;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RegistrationResponseDto {

    private String id;
    private String firstName;
    private String lastName;

    private String phoneNumber;

    private String email;

    private String businessName;

    public RegistrationResponseDto(final String id,
                                   final String firstName,
                                   final String lastName,
                                   final String phoneNumber,
                                   final String email,
                                   final String businessName) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.businessName = businessName;
    }
}
