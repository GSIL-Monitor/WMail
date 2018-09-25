package com.gongw.mailcore.message;

import com.gongw.mailcore.contact.Contact;

import org.litepal.annotation.Column;
import org.litepal.crud.LitePalSupport;

/**
 * Created by gongw on 2018/9/21.
 */

public class MessageContact extends LitePalSupport{

    public static class Type{
        public static final String FROM = "from";
        public static final String TO = "to";
        public static final String CC = "cc";
        public static final String BCC = "bcc";
        public static final String REPLY = "reply";
        public static final String SENDER = "sender";
    }

    private long id;

    private Contact contact;

    private LocalMessage localMessage;

    private String type;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Contact getContact() {
        return contact;
    }

    public void setContact(Contact contact) {
        this.contact = contact;
    }

    public LocalMessage getLocalMessage() {
        return localMessage;
    }

    public void setLocalMessage(LocalMessage localMessage) {
        this.localMessage = localMessage;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
