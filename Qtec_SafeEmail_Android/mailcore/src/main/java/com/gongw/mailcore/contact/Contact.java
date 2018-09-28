package com.gongw.mailcore.contact;


import com.gongw.mailcore.message.MessageContact;

import org.litepal.crud.LitePalSupport;

import java.util.List;

/**
 * 联系人类，包含邮箱和名称，一般从邮件中的收件人、发件人、抄送人等域自动生成
 * Created by gongw on 2018/9/11.
 */

public class Contact extends LitePalSupport {

    private long id;
    /**
     * 邮箱地址
     */
    private String email;
    /**
     * 名称
     */
    private String personalName;
    /**
     * 被用于哪些邮件中的联系人
     * 主要用于LitePal创建关联表，此项一般不设置，为空
     */
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
