package com.gongw.mailcore.net;

import com.sun.mail.imap.IMAPFolder;
import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.MethodNotSupportedException;
import javax.mail.NoSuchProviderException;
import javax.mail.Store;
import javax.mail.URLName;

/**
 * Created by gongw on 2018/8/30.
 */

public class MessageFetcher {
    private Store store;

    public MessageFetcher(String storeUrl) throws MessagingException {
        setStoreUrlName(storeUrl);
    }

    public String getStoreUrl(){
        return store.getURLName().toString();
    }

    /**
     * 设置URLName
     * @param storeUrl 用于保存protocol,host,port,username,password等信息
     * @throws NoSuchProviderException
     */
    public void setStoreUrlName(String storeUrl) throws MessagingException {
        URLName urlName = new URLName(storeUrl);
        this.store = MailSession.getDefaultSession().getStore(urlName);
        connect();
    }

    /**
     * 检查是否已建立连接，如果没有则自动连接
     * @throws MessagingException
     */
    public void connect() throws MessagingException {
        if(!store.isConnected()){
            store.connect();
        }
    }

    /**
     * 检查是否已建立连接，如果有则调用close断开
     * @throws MessagingException
     */
    public void close() throws MessagingException {
        if(store.isConnected()){
            store.close();
        }
    }
    
    /**
     * 获取所有文件夹信息
     * @return 返回Folder数组
     * @throws MessagingException
     */
    public Folder[] fetchFolders() throws MessagingException {
        connect();
        return store.getDefaultFolder().list();
    }

    /**
     * 以指定mode打开指定folder
     * @param fullName 要打开的folder的fullName
     * @param mode 打开方式 Folder.READ_ONLY ，Folder.READ_WRITE
     * @return 打开的folder
     * @throws MessagingException
     */
    public Folder openFolder(String fullName, int mode) throws MessagingException {
        connect();
        Folder folder = store.getFolder(fullName);
        if(!folder.isOpen() || folder.getMode() != mode){
            folder.open(mode);
        }
        return folder;
    }

    /**
     * 关闭指定的folder
     * @param fullName 要关闭的folder的fullName
     * @throws MessagingException
     */
    public void closeFolder(String fullName) throws MessagingException {
        connect();
        Folder folder = store.getFolder(fullName);
        if(folder.isOpen()){
            folder.close();
        }
    }

    /**
     * 获取指定位置的message
     * @param folderName message所在文件夹
     * @param start 起始位置，最小为1
     * @param end 结束位置
     * @return 符合要求的message数组
     * @throws MessagingException
     */
    public Message[] fetchMessages(String folderName, int start, int end) throws MessagingException {
        Folder folder = openFolder(folderName, Folder.READ_ONLY);
        int count = folder.getMessageCount();
        if(start < 1){
            start = 1;
        }
        if(end > count){
            end = count;
        }
        return folder.getMessages(start, end);
    }

    /**
     * 获取指定uid的message
     * @param folderName message所在文件夹
     * @param uid message的uid
     * @return 符合的message
     * @throws MessagingException
     */
    public Message fetchMessage(String folderName, Long uid) throws MessagingException {
        Folder folder = openFolder(folderName, Folder.READ_ONLY);
        if(folder instanceof IMAPFolder){
            IMAPFolder imapFolder = (IMAPFolder) folder;
            return imapFolder.getMessageByUID(uid);
        }
        throw new MethodNotSupportedException("getMessageByUID");
    }

    /**
     * 向指定文件夹添加message
     * @param messages 要添加的message数组
     * @param folderName 目标文件夹
     * @throws MessagingException
     */
    public void appendMessages(String folderName, Message[] messages) throws MessagingException {
        Folder folder = openFolder(folderName, Folder.READ_WRITE);
        folder.appendMessages(messages);
    }

    /**
     * 移动message到指定文件夹，移动操作=复制+删除
     * @param folderName 移动前所在文件夹
     * @param uids 需要移动的message uid数组
     * @param destFolderName 目标文件夹
     * @throws MessagingException
     */
    public void moveMessages(String folderName, long[] uids, String destFolderName) throws MessagingException {
        Folder destFolder = openFolder(destFolderName, Folder.READ_WRITE);
        Folder folder = openFolder(folderName, Folder.READ_WRITE);

        if(folder instanceof IMAPFolder && destFolder instanceof IMAPFolder){
            IMAPFolder imapFolder = (IMAPFolder) folder;
            Message[] msgArray = imapFolder.getMessagesByUID(uids);
            imapFolder.moveMessages(msgArray, destFolder);
        }else{
            throw new MethodNotSupportedException("getMessageByUID, moveMessages");
        }
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
        Folder folder = openFolder(folderName, Folder.READ_WRITE);

        if(folder instanceof IMAPFolder){
            IMAPFolder imapFolder = (IMAPFolder) folder;
            Message[] msgArray = imapFolder.getMessagesByUID(uids);
            imapFolder.setFlags(msgArray, flags, set);
            imapFolder.expunge();
        }
    }

}
