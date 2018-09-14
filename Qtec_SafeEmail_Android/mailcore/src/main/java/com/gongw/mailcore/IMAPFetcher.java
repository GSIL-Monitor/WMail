package com.gongw.mailcore;

import com.gongw.mailcore.setting.MailSession;
import com.sun.mail.imap.IMAPFolder;
import com.sun.mail.imap.IMAPMessage;
import com.sun.mail.imap.IMAPStore;
import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.NoSuchProviderException;
import javax.mail.URLName;

/**
 * Created by gongw on 2018/8/30.
 */

public class IMAPFetcher {
    private IMAPStore store;

    public IMAPFetcher(URLName urlName) throws NoSuchProviderException {
        setStoreUrlName(urlName);
    }

    /**
     * 设置URLName
     * @param urlName 用于保存protocol,host,port,username,password等信息
     * @throws NoSuchProviderException
     */
    public void setStoreUrlName(URLName urlName) throws NoSuchProviderException {
        this.store = (IMAPStore) MailSession.getDefaultSession().getStore(urlName);
    }

    /**
     * 检查是否已建立连接，如果没有则自动连接
     * @throws MessagingException
     */
    public void checkConnect() throws MessagingException {
        if(!store.isConnected()){
            store.connect();
        }
    }
    
    /**
     * 获取所有文件夹信息
     * @return 返回Folder数组
     * @throws MessagingException
     */
    public IMAPFolder[] fetchFolders() throws MessagingException {
        checkConnect();
        return (IMAPFolder[]) store.getDefaultFolder().list();
    }

    /**
     * 获取指定位置的message
     * @param folderName 目标文件夹
     * @param start 起始位置，最小为1
     * @param end 结束位置
     * @return 符合要求的message，只包含ENVELOP信息
     * @throws MessagingException
     */
    public IMAPMessage[] fetchMessages(String folderName, int start, int end) throws MessagingException {
        checkConnect();
        IMAPFolder folder = (IMAPFolder) store.getFolder(folderName);
        folder.open(Folder.READ_ONLY);
        int count = folder.getMessageCount();
        if(end > count){
            end = count;
        }
        IMAPMessage[] messages = (IMAPMessage[]) folder.getMessages(start, end);
        folder.close(true);
        return messages;
    }


    public IMAPMessage fetchMessage(String folderName, Long uid) throws MessagingException {
        checkConnect();
        IMAPFolder folder = (IMAPFolder) store.getFolder(folderName);
        folder.open(Folder.READ_ONLY);
        IMAPMessage message = (IMAPMessage) folder.getMessageByUID(uid);
        folder.close(true);
        return message;
    }

//    public Message fetMessageAttach(Message message, String contentMD5) throws MessagingException {
//        String contentType = message.getContentType();
//
//
//    }

    /**
     * 向指定文件夹添加message
     * @param messages 要添加的message数组
     * @param folderName 目标文件夹
     * @throws MessagingException
     */
    public void appendMessages(String folderName, Message[] messages) throws MessagingException {
        checkConnect();
        IMAPFolder folder = (IMAPFolder) store.getFolder(folderName);
        folder.open(Folder.READ_WRITE);
        folder.appendMessages(messages);
        folder.close(true);
    }

    /**
     * 移动message到指定文件夹，移动操作=复制+删除
     * @param folderName 移动前所在文件夹
     * @param uids 需要移动的message uid数组
     * @param destFolderName 目标文件夹
     * @throws MessagingException
     */
    public void moveMessages(String folderName, long[] uids, String destFolderName) throws MessagingException {
        checkConnect();
        Folder destFolder = store.getFolder(destFolderName);
        destFolder.open(Folder.READ_WRITE);
        IMAPFolder folder = (IMAPFolder) store.getFolder(folderName);
        folder.open(Folder.READ_WRITE);

        Message[] msgArray = folder.getMessagesByUID(uids);
        folder.moveMessages(msgArray, destFolder);
        folder.close(true);
        destFolder.close(true);
    }

    /**
     * 删除指定message
     * @param folderName 需要删除的message所在文件夹
     * @param uids 需要删除的message uid数组
     * @throws MessagingException
     */
    public void deleteMessages(String folderName, long[] uids) throws MessagingException {
       flagMessages(folderName, uids, new Flags(Flags.Flag.DELETED), true);
    }

    /**
     * 修改message的Flag
     * @param folderName 需要修改的message所在文件夹
     * @param uids 需要修改的message uid数组
     * @param flags 需要修改的Flag
     * @param set Flag修改的目标值
     * @throws MessagingException
     */
    public void flagMessages(String folderName, long[] uids, Flags flags, boolean set) throws MessagingException {
        checkConnect();
        IMAPFolder folder = (IMAPFolder) store.getFolder(folderName);
        folder.open(Folder.READ_WRITE);
        Message[] msgArray = folder.getMessagesByUID(uids);
        folder.setFlags(msgArray, flags, set);
        folder.expunge();
        folder.close(true);
    }

}
