package com.blog.cutom_blog.exceptions;

public class NotAcceptableException extends WebApiException {
    private static final long serialVersionUID = 7395803808473928415L;

    public NotAcceptableException(String message) {
        super(message, 406);
    }

    public NotAcceptableException(String message, String prettyMessage) {
        super(message, (Throwable)null, 406, false, prettyMessage, (ErrorCode)null);
    }

    public NotAcceptableException(String message, String prettyMessage, ErrorCode<?> errorCode) {
        super(message, (Throwable)null, 406, false, prettyMessage, errorCode);
    }

    public NotAcceptableException(String message, Throwable cause) {
        super(message, cause, 406);
    }

    public NotAcceptableException(ErrorCode<?> errorCode, String prettyMessage) {
        super((String)null, (Throwable)null, 406, false, prettyMessage, errorCode);
    }
}
