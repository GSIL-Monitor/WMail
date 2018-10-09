package com.gongw.mailcore.folder;

import com.gongw.mailcore.account.Account;
import com.gongw.mailcore.message.LocalMessage;
import org.litepal.annotation.Column;
import org.litepal.crud.LitePalSupport;
import java.util.List;

import javax.mail.Folder;


/**
 * 邮箱的文件夹类，如收件箱、已发送、草稿箱、已删除、垃圾邮件等文件夹
 * Created by gongw on 2018/7/22.
 */

public class LocalFolder extends LitePalSupport {

    /**
     * 用来指定文件夹的显示类别
     */
    public static class Type {
        /**
         * 表示该文件夹为收件箱
         */
        public static final String INBOX = "inbox";
        /**
         * 表示该文件夹为已发送
         */
        public static final String SENT = "sent";
        /**
         * 表示该文件夹为草稿箱
         */
        public static final String DRAFT = "draft";
        /**
         * 表示该文件夹为已删除
         */
        public static final String TRASH = "trash";
        /**
         * 表示该文件夹为已删除
         */
        public static final String DELETED = "deleted";
        /**
         * 表示该文件夹为垃圾邮件
         */
        public static final String JUNK = "junk";
        /**
         * 表示该文件夹为加密邮件
         */
        public static final String ENCRYPT = "encrypt";
        /**
         * 表示该文件夹为加星邮件
         */
        public static final String STAR = "star";
        /**
         * 表示该文件夹为其它邮件
         */
        public static final String OTHERS = "others";
    }

    private long id;
    /**
     * 文件夹属于哪个邮箱账号
     */
    @Column(nullable = false)
    private Account account;
    /**
     * 文件夹在邮箱服务器上的url地址
     */
    @Column(unique = true, nullable = false)
    private String url;
    /**
     * 文件夹在邮箱服务器上的名称
     */
    @Column(nullable = false)
    private String fullName;
    /**
     * 文件夹在本地的名称
     */
    private String localName;
    /**
     * 对应LocalFolder.Type中的静态变量
     */
    private String localType;
    /**
     * 文件夹的类型，有Folder.HOLDS_FOLDERS 和 Folder.HOLDS_MESSAGES
     */
    private int type;
    /**
     * 文件夹url中的分隔符
     */
    private char separator;
    /**
     * 文件夹中的新邮件数量
     */
    private int newMsgCount;
    /**
     * 文件夹中的邮件数量
     */
    private int msgCount;
    /**
     * 文件夹中的邮件集合
     */
    private List<LocalMessage> messageList;

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

    public List<LocalMessage> getMessageList() {
        return messageList;
    }

    public void setMessageList(List<LocalMessage> messageList) {
        this.messageList = messageList;
    }

    public String getLocalType() {
        return localType;
    }

    public void setLocalType(String localType) {
        this.localType = localType;
    }

    public String getLocalName() {
        return localName;
    }

    public void setLocalName(String localName) {
        this.localName = localName;
    }
}
