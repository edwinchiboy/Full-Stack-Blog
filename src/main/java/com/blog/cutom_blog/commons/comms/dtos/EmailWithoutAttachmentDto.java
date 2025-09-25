package com.blog.cutom_blog.commons.comms.dtos;

import java.util.List;

/**
 * @author chibuike
 * created on 10/06/2021
 */
public interface EmailWithoutAttachmentDto {

    public String getFrom();

    public String getSubject();

    public List<String> getTo() ;

    public List<String> getCc();

    public List<String> getBcc();

    public EmailBody getBody();

}
