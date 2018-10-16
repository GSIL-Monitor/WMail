package com.gongw.mailcore.setting;

/**
 * Created by gongw on 2018/7/16.
 */

public enum  PortType {
    IMAP(143, 993),
    SMTP(587, 465),
    //        SMTP(25, 465),
    WebDAV(80, 443),
    POP3(110, 995);

    public final int defaultPort;

    public final int defaultTLSPort;

    private PortType(int defaultPort, int defaultTLSPort) {
        this.defaultPort = defaultPort;
        this.defaultTLSPort = defaultTLSPort;
    }
}
