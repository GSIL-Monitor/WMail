package com.gongw.mailcore.folder;

import com.gongw.mailcore.account.Account;
import com.gongw.mailcore.message.LocalMessage;

import org.litepal.annotation.Column;
import org.litepal.crud.LitePalSupport;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by gongw on 2018/7/22.
 */

public class LocalFolder extends LitePalSupport {

    private long id;
    @Column(nullable = false)
    private Account account;

    @Column(unique = true, nullable = false)
    private String url;

    @Column(nullable = false)
    private String fullName;

    private int type;

    private char separator;

    private int newMsgCount;

    private int msgCount;

    private List<LocalMessage> localMessageList = new ArrayList<>();

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public char getSeparator() {
        return separator;
    }

    public void setSeparator(char separator) {
        this.separator = separator;
    }

    public int getNewMsgCount() {
        return newMsgCount;
    }

    public void setNewMsgCount(int newMsgCount) {
        this.newMsgCount = newMsgCount;
    }

    public int getMsgCount() {
        return msgCount;
    }

    public void setMsgCount(int msgCount) {
        this.msgCount = msgCount;
    }
}
