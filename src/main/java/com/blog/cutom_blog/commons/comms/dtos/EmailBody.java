package com.blog.cutom_blog.commons.comms.dtos;

import com.blog.cutom_blog.commons.comms.constants.EmailBodyType;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class EmailBody {

    private String data;
    private EmailBodyType type;

    public EmailBody(final String data,
                     final EmailBodyType type) {
        this.data = data;
        this.type = type;
    }
}
