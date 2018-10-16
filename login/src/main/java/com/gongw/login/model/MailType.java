package com.gongw.login.model;

/**
 * Created by gongw on 2018/7/11.
 */

public class MailType {
    private String text;
    private String icon;

    public MailType(String text, String icon)
    {
        this.text = text;
        this.icon = icon;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }
}
