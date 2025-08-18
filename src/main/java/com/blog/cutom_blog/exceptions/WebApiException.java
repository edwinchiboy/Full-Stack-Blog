package com.blog.cutom_blog.exceptions;



public class WebApiException extends RuntimeException {
    private final int httpStatus;
    private final boolean fatal;
    private final String prettyMessage;
    private final ErrorCode<?> errorCode;

    public WebApiException(String message, int httpStatus) {
        super(message);
        this.httpStatus = httpStatus;
        this.fatal = false;
        this.prettyMessage = null;
        this.errorCode = null;
    }

    public WebApiException(String message, int httpStatus, boolean fatal) {
        super(message);
        this.httpStatus = httpStatus;
        this.fatal = fatal;
        this.prettyMessage = null;
        this.errorCode = null;
    }

    public WebApiException(String message, Throwable cause, int httpStatus) {
        super(message, cause);
        this.httpStatus = httpStatus;
        this.fatal = false;
        this.prettyMessage = null;
        this.errorCode = null;
    }

    public WebApiException(String message, Throwable cause, int httpStatus, boolean fatal) {
        super(message, cause);
        this.httpStatus = httpStatus;
        this.fatal = fatal;
        this.prettyMessage = null;
        this.errorCode = null;
    }

    public WebApiException(String message, Throwable cause, int httpStatus, boolean fatal, String prettyMessage, ErrorCode<?> errorCode) {
        super(message, cause);
        this.httpStatus = httpStatus;
        this.fatal = fatal;
        this.prettyMessage = prettyMessage;
        this.errorCode = errorCode;
    }

    public int getHttpStatus() {
        return this.httpStatus;
    }

    public boolean isFatal() {
        return this.fatal;
    }

    public ErrorCode<?> getErrorCode() {
        return this.errorCode;
    }

    public String getPrettyMessage() {
        return this.prettyMessage;
    }
}

