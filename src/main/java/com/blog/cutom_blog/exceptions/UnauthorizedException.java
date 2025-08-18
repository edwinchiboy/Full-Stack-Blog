package com.blog.cutom_blog.exceptions;

public class UnauthorizedException extends WebApiException {
    private static final long serialVersionUID = 7395803808473928415L;

    public UnauthorizedException(String message) {
        super(message, 401);
    }

    public UnauthorizedException(String message, String prettyMessage) {
        super(message, (Throwable)null, 401, false, prettyMessage, (ErrorCode)null);
    }

    public UnauthorizedException(String message, String prettyMessage, ErrorCode<?> errorCode) {
        super(message, (Throwable)null, 401, false, prettyMessage, errorCode);
    }

    public UnauthorizedException(String message, Throwable cause) {
        super(message, cause, 401);
    }

    public UnauthorizedException(ErrorCode<?> errorCode, String prettyMessage) {
        super((String)null, (Throwable)null, 401, false, prettyMessage, errorCode);
    }
}
