package com.blog.cutom_blog.commons.auth.constants;

public enum PrincipalType {

    USER_ACCOUNT,
    SERVICE_ACCOUNT,
    APPLICATION_DOMAIN;

    public boolean isUserAccount(){
        return this == USER_ACCOUNT;
    }

    public boolean isServiceAccount(){
        return this == SERVICE_ACCOUNT;
    }

    public boolean isApplicationDomain(){
        return this == APPLICATION_DOMAIN;
    }


}
