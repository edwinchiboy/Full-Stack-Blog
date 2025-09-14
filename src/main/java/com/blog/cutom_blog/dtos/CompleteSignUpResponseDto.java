package com.blog.cutom_blog.dtos;


import com.blog.cutom_blog.commons.auth.dtos.AuthResponse;
import com.blog.cutom_blog.models.User;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@JsonIgnoreProperties
@NoArgsConstructor
public class CompleteSignUpResponseDto {

    private String registrationId;
    private String email;
    private AuthResponse authResponse;
    private User user;

    public CompleteSignUpResponseDto(final String registrationId,
                                     final String email,
                                     final AuthResponse authResponse,
                                     final User user) {
        this.registrationId = registrationId;
        this.email = email;
        this.authResponse = authResponse;
        this.user = user;
    }
}
