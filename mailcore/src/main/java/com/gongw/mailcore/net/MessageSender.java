package com.gongw.mailcore.net;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Transport;
import javax.mail.URLName;

/**
 * 用于发送邮件的类
 * Created by lenovo on 2018/8/30.
 */

public class MessageSender {

    private Transport transport;

    public MessageSender(String transportUrl) throws MessagingException {
        setTransportUrl(transportUrl);
    }

    public String getTransportUrl(){
        return transport.getURLName().toString();
    }

    /**
     * 这是smtp连接url，并尝试连接
     * @param transportUrl 用于连接smtp服务的url
     * @throws MessagingException
     */
    public void setTransportUrl(String transportUrl) throws MessagingException {
        URLName urlName = new URLName(transportUrl);
        this.transport = MailSession.getDefaultSession().getTransport(urlName);
        connect();
    }

    /**
     * 连接smtp服务
     * @throws MessagingException
     */
    public void connect() throws MessagingException {
        if(!transport.isConnected()){
            transport.connect();
        }
    }

    /**
     * 断开smtp服务连接
     * @throws MessagingException
     */
    public void close() throws MessagingException {
        if(transport.isConnected()){
            transport.close();
        }
    }

    /**
     * 发送一封邮件
     * @param message 需要发送的邮件
     * @throws MessagingException
     */
    public void sendMsg(Message message) throws MessagingException {
        transport.sendMessage(message, message.getAllRecipients());
    }



}
