package com.blog.cutom_blog.constants;

public enum Environment {
    DEV,
    PROD;

    public boolean isDev() {
        return this == DEV;
    }
}
