package com.blog.cutom_blog.commons.comms.dtos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@JsonIgnoreProperties(ignoreUnknown = true)
public class SendOtpResponse {

    private long durationToExpireMinutes;
    private long durationToExpireSeconds;
    private Instant expireAt;
    private Instant issuedAt;

    public SendOtpResponse(final long durationToExpireMinutes,
                           final long durationToExpireSeconds,
                           final Instant expireAt,
                           final Instant issuedAt) {
        this.durationToExpireMinutes = durationToExpireMinutes;
        this.durationToExpireSeconds = durationToExpireSeconds;
        this.expireAt = expireAt;
        this.issuedAt = issuedAt;
    }
}

