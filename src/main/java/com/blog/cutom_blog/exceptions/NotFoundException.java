package com.blog.cutom_blog.exceptions;

public class NotFoundException extends WebApiException {
    private static final long serialVersionUID = 7395803808473928415L;

    public NotFoundException(String message) {
        super(message, 404);
    }

    public NotFoundException(String message, String prettyMessage) {
        super(message, (Throwable)null, 404, false, prettyMessage, (ErrorCode)null);
    }

    public NotFoundException(String message, String prettyMessage, ErrorCode<?> errorCode) {
        super(message, (Throwable)null, 404, false, prettyMessage, errorCode);
    }

    public NotFoundException(String message, Throwable cause) {
        super(message, cause, 404);
    }

    public NotFoundException(ErrorCode<?> errorCode, String prettyMessage) {
        super((String)null, (Throwable)null, 404, false, prettyMessage, errorCode);
    }
}
