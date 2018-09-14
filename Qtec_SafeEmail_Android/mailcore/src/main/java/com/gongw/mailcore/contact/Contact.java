package com.gongw.mailcore.contact;

import org.litepal.crud.LitePalSupport;

/**
 * Created by gongw on 2018/9/11.
 */

public class Contact extends LitePalSupport {

    private String email;

    private String personalName;

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
}
