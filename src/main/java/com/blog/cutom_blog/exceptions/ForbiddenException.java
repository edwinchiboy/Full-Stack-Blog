package com.blog.cutom_blog.exceptions;

public class ForbiddenException extends WebApiException {
    private static final long serialVersionUID = 7395803808473928415L;

    public ForbiddenException() {
        super("You are not allowed to perform that operation.", 403);
    }

    public ForbiddenException(String message) {
        super(message, 403);
    }

    public ForbiddenException(String message, String prettyMessage) {
        super(message, (Throwable)null, 403, false, prettyMessage, (ErrorCode)null);
    }

    public ForbiddenException(String message, String prettyMessage, ErrorCode<?> errorCode) {
        super(message, (Throwable)null, 403, false, prettyMessage, errorCode);
    }

    public ForbiddenException(String message, Throwable cause) {
        super(message, cause, 403);
    }

    public ForbiddenException(ErrorCode<?> errorCode, String prettyMessage) {
        super((String)null, (Throwable)null, 403, false, prettyMessage, errorCode);
    }
}