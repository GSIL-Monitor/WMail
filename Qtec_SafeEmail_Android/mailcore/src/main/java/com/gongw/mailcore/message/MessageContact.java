package com.gongw.mailcore.message;

import com.gongw.mailcore.contact.Contact;
import org.litepal.crud.LitePalSupport;

/**
 * 邮件的联系人,主要用于关联LocalMessage和Contact
 * Created by gongw on 2018/9/21.
 */
public class MessageContact extends LitePalSupport{

    /**
     * 在邮件中的联系人类型
     */
    public static class Type{
        /**
         * 类型为邮件来源人
         */
        public static final String FROM = "from";
        /**
         * 类型为邮件的收件人
         */
        public static final String TO = "to";
        /**
         * 类型为邮件的抄送人
         */
        public static final String CC = "cc";
        /**
         * 类型为邮件的密送人
         */
        public static final String BCC = "bcc";
        /**
         * 类型为邮件的回复人
         */
        public static final String REPLY = "reply";
        /**
         * 类型为邮件的发件人
         */
        public static final String SENDER = "sender";
    }

    private long id;
    /**
     * 联系人
     */
    private Contact contact;
    /**
     * 邮件
     */
    private LocalMessage localMessage;
    /**
     * 联系人在邮件中的类型
     */
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
