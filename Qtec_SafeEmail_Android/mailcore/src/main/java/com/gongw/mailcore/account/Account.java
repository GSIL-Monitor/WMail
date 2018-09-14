package com.gongw.mailcore.account;


import com.gongw.mailcore.folder.LocalFolder;
import org.litepal.annotation.Column;
import org.litepal.crud.LitePalSupport;
import java.util.ArrayList;
import java.util.List;

import javax.mail.URLName;


/**
 * Created by gongw on 2018/8/30.
 */

public class Account extends LitePalSupport{

    private long id;
    @Column(unique = true, nullable = false)
    private String email;
    private String storeUrl;
    private String transportUrl;
    private String password;
    private String userName;
    private boolean isAvailable = true;

    private List<LocalFolder> localFolderList = new ArrayList<>();

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

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public boolean isAvailable() {
        return isAvailable;
    }

    public void setAvailable(boolean available) {
        isAvailable = available;
    }

    public List<LocalFolder> getLocalFolderList() {
        return localFolderList;
    }

    public void setLocalFolderList(List<LocalFolder> localFolderList) {
        this.localFolderList = localFolderList;
    }
}
