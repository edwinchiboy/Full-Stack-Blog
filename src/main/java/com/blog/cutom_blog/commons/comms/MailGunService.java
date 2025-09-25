package com.blog.cutom_blog.commons.comms;

import com.mailgun.api.v3.MailgunMessagesApi;
import com.mailgun.model.message.Message;
import com.mailgun.model.message.MessageResponse;

import com.blog.cutom_blog.commons.comms.dtos.EmailDeliveryStatus;
import com.blog.cutom_blog.commons.comms.dtos.EmailWithoutAttachmentDto;
import lombok.Getter;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
public class MailGunService {
    private final MailgunMessagesApi mailgunMessagesApi;


    public MailGunService(
        MailgunMessagesApi mailgunMessagesApi) {
        this.mailgunMessagesApi = mailgunMessagesApi;
    }


    public EmailDeliveryStatus sendEmail(final EmailWithoutAttachmentDto emailDto) {

        if (!emailDto.getCc().isEmpty()) {
            throw new IllegalArgumentException("Mailgun does not support 'cc' emails via this SDK version");
        }

        if (!emailDto.getBcc().isEmpty()) {
            throw new IllegalArgumentException("Mailgun does not support 'bcc' emails via this SDK version");
        }

        Message.MessageBuilder messageBuilder = Message.builder()
            .from(String.format("Zilla Finance <%s>", emailDto.getFrom()))
            .to(emailDto.getTo())
            .subject(emailDto.getSubject());

        if (emailDto.getBody().getType().isText()) {
            messageBuilder.text(emailDto.getBody().getData());
        } else if (emailDto.getBody().getType().isHtml()) {
            messageBuilder.html(emailDto.getBody().getData());
        } else {
            throw new IllegalArgumentException("Unsupported email body type.");
        }

        Message message = messageBuilder.build();

        MessageResponse response = mailgunMessagesApi.sendMessage(mailGunConfig.getDomain(), message);

        if (response == null || response.getId() == null) {
            throw new RuntimeException("Failed to send email through Mailgun");
        }

        return EmailDeliveryStatus.builder()
            .messageId(response.getId())
            .status(EmailDeliveryStatus.Status.SENT)
            .sentAt(Instant.now())
            .build();
    }

}

