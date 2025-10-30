package com.blog.cutom_blog.commons.comms.dtos;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Data
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class EmailDto implements EmailWithoutAttachmentDto {

    private String from;
    private String subject;
    private List<String> to;
    private List<String> cc;
    private List<String> bcc;
    private EmailBody body;

    @Builder
    public EmailDto(final String from,
                    final String subject,
                    final List<String> to,
                    final List<String> cc,
                    final List<String> bcc,
                    final EmailBody body) {
        this.from = Objects.requireNonNull(from, "'from' field cannot be null");
        this.subject = Objects.requireNonNull(subject, "'subject' field cannot be null");
        this.to = to == null ? Collections.emptyList() : to;
        this.cc = cc == null ? Collections.emptyList() : cc;
        this.bcc = bcc == null ? Collections.emptyList() : bcc;
        this.body = Objects.requireNonNull(body, "email body cannot be null");
    }
}