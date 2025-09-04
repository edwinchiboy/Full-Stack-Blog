package com.blog.cutom_blog.exceptions;

public class ServiceUnavailableException extends WebApiException {
    private static final long serialVersionUID = 7395803808473928415L;

    public ServiceUnavailableException(String message) {
        super(message, 503);
    }

    public ServiceUnavailableException(String message, String prettyMessage) {
        super(message, (Throwable)null, 503, false, prettyMessage, (ErrorCode)null);
    }

    public ServiceUnavailableException(String message, String prettyMessage, ErrorCode<?> errorCode) {
        super(message, (Throwable)null, 503, false, prettyMessage, errorCode);
    }

    public ServiceUnavailableException(String message, Throwable cause) {
        super(message, cause, 503);
    }

    public ServiceUnavailableException(ErrorCode<?> errorCode, String prettyMessage) {
        super((String)null, (Throwable)null, 503, false, prettyMessage, errorCode);
    }
}
