package com.blog.cutom_blog.commons.auth.dtos;


import com.blog.cutom_blog.commons.auth.constants.AccountStatus;
import com.blog.cutom_blog.commons.auth.constants.PrincipalType;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Principal {

    private String id;

    private String username;

    private AccountStatus accountStatus;

    private PrincipalType principalType;

    private String authToken;

    public Principal(final String id,
                     final String username,
                     final AccountStatus accountStatus,
                     final PrincipalType principalType, final String authToken) {
        this.id = id;
        this.username = username;
        this.accountStatus = accountStatus;
        this.principalType = principalType;
        this.authToken = authToken;
    }

    public boolean isUser(){
        return principalType == PrincipalType.USER_ACCOUNT;
    }

}
