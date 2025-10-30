package com.blog.cutom_blog.commons.comms.dtos;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;

/**
 * @author chibuike
 * created on 06/04/2021
 */
@Data
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class EmailDeliveryStatus {

    private String messageId;

    private Boolean submitted;

    private Status status;

    //Gateway response if message sending failed
    private String errorMessage;

    //Time it was submitted by kuid
    private Instant sentAt;

    //Time it got to user's mobile terminal
    private Instant deliveredAt;

    public EmailDeliveryStatus(final String messageId,
                               final Boolean submitted,
                               final Status status,
                               final String errorMessage,
                               final Instant sentAt,
                               final Instant deliveredAt) {
        this.messageId = messageId;
        this.submitted = submitted;
        this.status = status;
        this.errorMessage = errorMessage;
        this.sentAt = sentAt;
        this.deliveredAt = deliveredAt;
    }

    public enum Status{
        SENT, PENDING, DELIVERED, FAILED
    }
}

