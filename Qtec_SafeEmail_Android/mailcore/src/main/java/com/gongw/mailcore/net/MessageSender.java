package com.gongw.mailcore.net;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Transport;
import javax.mail.URLName;

/**
 * Created by lenovo on 2018/8/30.
 */

public class MessageSender {

    private Transport transport;

    public MessageSender(String transportUrl) throws MessagingException {
        setTransportUrl(transportUrl);
    }

    public void setTransportUrl(String transportUrl) throws MessagingException {
        URLName urlName = new URLName(transportUrl);
        this.transport = MailSession.getDefaultSession().getTransport(urlName);
        connect();
    }

    public void connect() throws MessagingException {
        if(!transport.isConnected()){
            transport.connect();
        }
    }

    public void close() throws MessagingException {
        if(transport.isConnected()){
            transport.close();
        }
    }

    public void sendMsg(Message message) throws MessagingException {
        transport.sendMessage(message, message.getAllRecipients());
    }



}
