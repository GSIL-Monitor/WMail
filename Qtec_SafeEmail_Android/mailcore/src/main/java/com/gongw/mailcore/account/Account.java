package com.gongw.mailcore.account;



import com.gongw.mailcore.folder.LocalFolder;

import org.litepal.annotation.Column;
import org.litepal.crud.LitePalSupport;
import java.util.List;


/**
 * 账号类，用于保存邮箱账号信息以及登录信息
 * Created by gongw on 2018/8/30.
 */

public class Account extends LitePalSupport {

    private long id;
    /**
     * 邮箱账号
     */
    @Column(unique = true, nullable = false)
    private String email;
    /**
     * 包含获取邮箱信息的imap或pop协议，域名，端口号，用户名，密码，文件名
     * 格式与URLName.toString()一致
     */
    private String storeUrl;
    /**
     * 包含发送邮件的的smtp协议，域名，端口号，用户名，密码，文件名
     * 格式与URLName.toString()一致
     */
    private String transportUrl;
    /**
     * 邮箱密码
     */
    private String password;
    /**
     * 邮箱是否可用
     */
    private boolean isAvailable = true;
    /**
     * 邮箱下包含的文件夹集合
     */
    private List<LocalFolder> folderList;
    /**
     * 是否是加密账号
     */
    private boolean isEncryptAccount;
    /**
     * 邮件签名
     */
    private String signature;
    /**
     * 检查邮件更新的频率
     */
    private int checkFrequency;
    /**
     * 账户名称
     */
    private String localName;

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

    public String getStoreUrl() {
        return storeUrl;
    }

    public void setStoreUrl(String storeUrl) {
        this.storeUrl = storeUrl;
    }

    public String getTransportUrl() {
        return transportUrl;
    }

    public void setTransportUrl(String transportUrl) {
        this.transportUrl = transportUrl;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isAvailable() {
        return isAvailable;
    }

    public void setAvailable(boolean available) {
        isAvailable = available;
    }

    public List<LocalFolder> getFolderList() {
        return folderList;
    }

    public void setFolderList(List<LocalFolder> folderList) {
        this.folderList = folderList;
    }

    public boolean isEncryptAccount() {
        return isEncryptAccount;
    }

    public void setEncryptAccount(boolean encryptAccount) {
        isEncryptAccount = encryptAccount;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public int getCheckFrequency() {
        return checkFrequency;
    }

    public void setCheckFrequency(int checkFrequency) {
        this.checkFrequency = checkFrequency;
    }

    public String getLocalName() {
        return localName;
    }

    public void setLocalName(String localName) {
        this.localName = localName;
    }
}
