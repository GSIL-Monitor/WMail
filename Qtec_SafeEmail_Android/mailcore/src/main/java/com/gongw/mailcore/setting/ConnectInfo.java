package com.gongw.mailcore.setting;

/**
 * Created by gongw on 2018/7/16.
 */

public class ConnectInfo {

    /**
     * 邮箱账号
     */
    private String email;
    /**
     * 邮件密码
     */
    private String pwd;
    /**
     * 服务器地址
     */
    private String serverHost;
    /**
     * 端口号
     */
    private int  serverPort;
    /**
     * 服务器类型:正常，加密TSL，SSL
     */
    private ConnectType connectType;


    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPwd() {
        return pwd;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }

    public String getServerHost() {
        return serverHost;
    }

    public void setServerHost(String serverHost) {
        this.serverHost = serverHost;
    }

    public int getServerPort() {
        return serverPort;
    }

    public void setServerPort(int serverPort) {
        this.serverPort = serverPort;
    }

    public ConnectType getConnectType() {
        return connectType;
    }

    public void setConnectType(ConnectType connectType) {
        this.connectType = connectType;
    }
}
