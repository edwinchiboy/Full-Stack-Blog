package com.blog.cutom_blog.commons.comms.constants;

/**
 * @author chibuike
 * created on 10/06/2021
 */
public enum EmailBodyType {
    TEXT, HTML;

    public boolean isText(){
        return this == TEXT;
    }

    public boolean isHtml(){
        return this == HTML;
    }
}
