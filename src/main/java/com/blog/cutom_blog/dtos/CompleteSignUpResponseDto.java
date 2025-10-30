package com.blog.cutom_blog.dtos;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@JsonIgnoreProperties
@NoArgsConstructor
public class CompleteSignUpResponseDto {

    private String userId;
    private String email;
    private String message;

    public CompleteSignUpResponseDto(final String userId,
                                     final String email,
                                     final String message) {
        this.userId = userId;
        this.email = email;
        this.message = message;
    }
}
