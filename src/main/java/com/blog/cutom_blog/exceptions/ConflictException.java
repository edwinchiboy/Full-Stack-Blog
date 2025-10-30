package com.blog.cutom_blog.exceptions;

public class ConflictException extends WebApiException {
    private static final long serialVersionUID = 7395803808473928415L;

    public ConflictException(String message) {
        super(message, 409);
    }

    public ConflictException(String message, String prettyMessage) {
        super(message, (Throwable)null, 409, false, prettyMessage, (ErrorCode)null);
    }

    public ConflictException(String message, String prettyMessage, ErrorCode<?> errorCode) {
        super(message, (Throwable)null, 409, false, prettyMessage, errorCode);
    }

    public ConflictException(String message, Throwable cause) {
        super(message, cause, 409);
    }

    public ConflictException(String message, String prettyMessage, Throwable cause) {
        super(message, cause, 409, false, prettyMessage, (ErrorCode)null);
    }

    public ConflictException(ErrorCode<?> errorCode, String prettyMessage) {
        super((String)null, (Throwable)null, 409, false, prettyMessage, errorCode);
    }
}
