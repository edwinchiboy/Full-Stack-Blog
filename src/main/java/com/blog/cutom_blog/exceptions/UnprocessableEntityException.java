package com.blog.cutom_blog.exceptions;

public class UnprocessableEntityException extends WebApiException {
    private static final long serialVersionUID = 7395803808473928415L;
    private static final int statusCode = 422;

    public UnprocessableEntityException(String message) {
        super(message, 422);
    }

    public UnprocessableEntityException(String message, String prettyMessage) {
        super(message, (Throwable)null, 422, false, prettyMessage, (ErrorCode)null);
    }

    public UnprocessableEntityException(String message, String prettyMessage, ErrorCode<?> errorCode) {
        super(message, (Throwable)null, 422, false, prettyMessage, errorCode);
    }

    public UnprocessableEntityException(String message, String prettyMessage, Throwable cause) {
        super(message, cause, 422, false, prettyMessage, (ErrorCode)null);
    }

    public UnprocessableEntityException(String message, Throwable cause) {
        super(message, cause, 422);
    }

    public UnprocessableEntityException(ErrorCode<?> errorCode, String prettyMessage) {
        super((String)null, (Throwable)null, 422, false, prettyMessage, errorCode);
    }
}
