package com.blog.cutom_blog.exceptions;

public class BadRequestException extends WebApiException {
    private static final long serialVersionUID = 7395803808473928415L;

    public BadRequestException(String message) {
        super(message, 400);
    }

    public BadRequestException(String message, String prettyMessage) {
        super(message, (Throwable)null, 400, false, prettyMessage, (ErrorCode)null);
    }

    public BadRequestException(String message, String prettyMessage, ErrorCode<?> errorCode) {
        super(message, (Throwable)null, 400, false, prettyMessage, errorCode);
    }

    public BadRequestException(String message, String prettyMessage, Throwable cause) {
        super(message, cause, 400, false, prettyMessage, (ErrorCode)null);
    }

    public BadRequestException(String message, Throwable cause) {
        super(message, cause, 400);
    }

    public BadRequestException(ErrorCode<?> errorCode, String prettyMessage) {
        super((String)null, (Throwable)null, 400, false, prettyMessage, errorCode);
    }
}
