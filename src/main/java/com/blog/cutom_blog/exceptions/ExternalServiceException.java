package com.blog.cutom_blog.exceptions;

public class ExternalServiceException extends WebApiException {
    public ExternalServiceException(String message, String prettyMessage) {
        super(message, (Throwable)null, 500, false, prettyMessage, (ErrorCode)null);
    }

    public ExternalServiceException(String message, String prettyMessage, ErrorCode<?> errorCode) {
        super(message, (Throwable)null, 500, false, prettyMessage, errorCode);
    }

    public ExternalServiceException(String message, Throwable cause) {
        super(message, cause, 500, false);
    }

    public ExternalServiceException(String message, String prettyMessage, Throwable cause) {
        super(message, cause, 500, false, prettyMessage, (ErrorCode)null);
    }

    public ExternalServiceException(ErrorCode<?> errorCode, String prettyMessage) {
        super((String)null, (Throwable)null, 500, false, prettyMessage, errorCode);
    }
}
