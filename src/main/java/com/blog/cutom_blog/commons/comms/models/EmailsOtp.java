package com.blog.cutom_blog.commons.comms.models;

public class EmailsOtp {
    final  String tokenIdentifier;
    private  boolean isActive;
    public EmailsOtp(final String tokenIdentifier, final boolean isActive) {
        this.tokenIdentifier = tokenIdentifier;
        this.isActive = isActive;
    }
}
