package com.gongw.mailcore.setting;


import java.util.Properties;
import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;

/**
 * Created by gongw on 2018/8/30.
 */

public class MailSession {

    private Session session;

    private MailSession(){
        Properties properties = System.getProperties();
        session = Session.getDefaultInstance(properties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return super.getPasswordAuthentication();
            }
        });
        session.setDebug(false);
    }

    private static class SessionHolder{
        private static MailSession instance = new MailSession();
    }

    public static Session getDefaultSession(){
        return SessionHolder.instance.session;
    }


}
