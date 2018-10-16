package com.gongw.mailcore.setting;

import java.util.InputMismatchException;

/**
 * Created by gongw on 2018/7/16.
 */

public enum ServerType {

    IMAP("imap."),SMTP("smtp."),POP3("pop3.");

    private String value;

    ServerType(String value){
        this.value = value;
    }

    public String getValue(){
        return value;
    }

    public static ServerType getType(String host){
        for (ServerType serverType : values()){
            if (host.startsWith(serverType.value)){
                return serverType;
            }
        }
        throw new InputMismatchException("no server type found: "+host);
    }

}
