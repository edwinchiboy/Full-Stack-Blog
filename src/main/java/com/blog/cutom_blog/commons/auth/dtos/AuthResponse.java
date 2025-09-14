package com.blog.cutom_blog.commons.auth.dtos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;



@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AuthResponse {

    private String token;
    private Instant tokenExpireAt;
    private long tokenExpiryMinutes;
    private Principal principal;

    public AuthResponse(final String token,
                        final Instant tokenExpireAt,
                        final long tokenExpiryMinutes,
                        final Principal principal) {
        this.token = token;
        this.tokenExpireAt = tokenExpireAt;
        this.tokenExpiryMinutes = tokenExpiryMinutes;
        this.principal = principal;
    }
}
