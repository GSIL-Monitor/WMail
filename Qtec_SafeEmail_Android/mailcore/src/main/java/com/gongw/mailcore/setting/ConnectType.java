package com.gongw.mailcore.setting;

import com.libmailcore.ConnectionType;

/**
 * Created by gongw on 2018/7/16.
 */

public enum ConnectType {

    SSL(ConnectionType.ConnectionTypeTLS),
    TLS(ConnectionType.ConnectionTypeStartTLS),
    CLEAR(ConnectionType.ConnectionTypeClear);

    private int value;

    ConnectType(int value){
        this.value = value;
    }

    public int getValue(){
        return value;
    }

    public static ConnectType getType(int type){
        for (ConnectType connectType : values()){
            if (connectType.value == type){
                return connectType;
            }
        }
        throw new RuntimeException("no connect type found："+type);
    }

}
