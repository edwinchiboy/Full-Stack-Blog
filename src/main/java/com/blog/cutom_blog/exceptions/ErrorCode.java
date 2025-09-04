package com.blog.cutom_blog.exceptions;

public interface ErrorCode<T extends Enum<T>> {
    String name();
}
