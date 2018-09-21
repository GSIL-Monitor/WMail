package com.gongw.mailcore.contact;


import com.gongw.mailcore.message.MessageContact;

import org.litepal.crud.LitePalSupport;

import java.util.List;

/**
 * Created by gongw on 2018/9/11.
 */

public class Contact extends LitePalSupport {

    private long id;

    private String email;

    private String personalName;

    private List<MessageContact> messageContacts;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPersonalName() {
        return personalName;
    }

    public void setPersonalName(String personalName) {
        this.personalName = personalName;
    }

    public List<MessageContact> getMessageContacts() {
        return messageContacts;
    }

    public void setMessageContacts(List<MessageContact> messageContacts) {
        this.messageContacts = messageContacts;
    }
}
