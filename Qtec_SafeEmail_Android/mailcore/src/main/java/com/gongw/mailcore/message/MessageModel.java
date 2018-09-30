package com.gongw.mailcore.message;

import com.gongw.mailcore.account.Account;
import com.gongw.mailcore.folder.FolderModel;
import com.gongw.mailcore.folder.LocalFolder;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.mail.Flags;
import javax.mail.MessagingException;

/**
 * 邮件业务模型，提供对LocalMessage数据的操作接口
 * Created by gongw on 2018/7/17.
 */
public class MessageModel {
    /**
     * 邮件本地数据库资源对象，提供操作本地邮件资源的接口
     */
    private MessageLocalResource localResource;
    /**
     * 邮件网络资源对象，提供操作邮件服务器上的邮件资源的接口
     */
    private MessageNetResource netResource;
    /**
     * 分页获取邮件时，每页的邮件数量
     */
    private int pageSize = 20;

    private MessageModel(){
        localResource = MessageLocalResource.singleInstance();
        netResource = MessageNetResource.singleInstance();
    }

    private static class InstanceHolder{
        private static MessageModel instance = new MessageModel();
    }

    public static MessageModel singleInstance(){
        return InstanceHolder.instance;
    }

    /**
     * 按页获取指定文件夹下的邮件
     * 先从本地数据库获取，如果获取到则直接返回
     * 如果本地未获取到，则从网络获取，并将获取到的邮件保存到本地
     * @param localFolder 指定的文件夹LocalFolder
     * @param pageIndex 需要获取的页序号
     * @return LocalMessage集合
     * @throws MessagingException
     * @throws IOException
     */
    public List<LocalMessage> getMessagesByPage(LocalFolder localFolder, int pageIndex) throws MessagingException, IOException {
        List<LocalMessage> messageList = localResource.getMessagesByFolderId(localFolder.getId(), pageSize, pageIndex * pageSize);
        if(messageList.size() < 1){
            refreshMessages(localFolder, pageIndex);
            messageList = localResource.getMessagesByFolderId(localFolder.getId(), pageSize, pageIndex * pageSize);
        }
        for(LocalMessage localMessage : messageList){
            localMessage.setFolder(localFolder);
        }
        localFolder.setMessageList(messageList);
        return messageList;
    }

    /**
     * 按页获取从网络获取指定文件夹下的邮件，并将获取到的邮件保存到本地
     * @param localFolder 指定的文件夹
     * @param pageIndex 指定的页序号
     * @throws MessagingException
     * @throws IOException
     */
    public void refreshMessages(LocalFolder localFolder, int pageIndex) throws MessagingException, IOException {
        FolderModel.singleInstance().refreshFolders(localFolder.getAccount());
        List<LocalFolder> localFolders = FolderModel.singleInstance().getFolders(localFolder.getAccount());
        for(LocalFolder folder : localFolders){
            if(folder.getUrl().equals(localFolder.getUrl())){
                localFolder = folder;
                break;
            }
        }
        int msgCount = localFolder.getMsgCount();
        if(msgCount == 0){
            return;
        }
        int start = msgCount - (pageIndex + 1) * pageSize + 1;
        start = start < 1 ? 1 : start;
        int end = start + pageSize - 1;
        end = end < 1 ? msgCount : end;
        List<LocalMessage> localMessages = netResource.fetchMessages(localFolder, start, end);
        localResource.saveOrUpdateMessages(localMessages);
    }

    /**
     * 批量修改邮件标记
     * @param localMessages 需要修改的
     * @param flag 需要修改的flag，一般为Flags.Flag下的
     * @param set falg的目标值
     * @throws MessagingException
     */
    public void flagMessages(List<LocalMessage> localMessages, Flags.Flag flag, boolean set) throws MessagingException {
        netResource.flagMessages(localMessages, flag, set);
        localResource.flagMessage(localMessages, flag, set);
    }

    /**
     * 批量删除指定邮件
     * @param localMessages 需要删除的邮件
     * @param eager 是否彻底删除
     *              true：不论在哪个文件夹都执行删除操作
     *              false：如果在已删除或垃圾邮件文件夹就执行删除操作，否则执行移动操作，将邮件从当前文件夹，移动到已删除文件夹
     * @throws MessagingException
     */
    public void deleteMessages(List<LocalMessage> localMessages, boolean eager) throws MessagingException {
        //如果是彻底删除，则不论在哪个文件夹都直接删除
        if(eager){
            netResource.deleteMessages(localMessages);
            localResource.deleteMessages(localMessages);
        }else {
            //按Folder Url划分,区分不同账号不同文件夹的邮件
            Map<String, List<LocalMessage>> messageMap = new HashMap<>();
            for(LocalMessage localMessage : localMessages){
                String folderUrl = localMessage.getFolder().getUrl();
                if(!messageMap.containsKey(folderUrl)){
                    List<LocalMessage> messages = new ArrayList<>();
                    messageMap.put(folderUrl, messages);
                }
                messageMap.get(folderUrl).add(localMessage);
            }
            for(Iterator<List<LocalMessage>> iterator = messageMap.values().iterator(); iterator.hasNext();) {
                List<LocalMessage> messages = iterator.next();
                Account account = messages.get(0).getFolder().getAccount();
                String localFolderType = messages.get(0).getFolder().getLocalType();
                //如果不是已删除，垃圾箱的邮件则将邮件移动到已删除文件夹
                if(!LocalFolder.Type.DELETED.equals(localFolderType) && !LocalFolder.Type.TRASH.equals(localFolderType) && !LocalFolder.Type.JUNK.equals(localFolderType)){
                    LocalFolder destFolder = FolderModel.singleInstance().getTrashOrDeletedFolder(account);
                    moveMessages(messages, destFolder);
                }else{
                    deleteMessages(messages, true);
                }
            }
        }
    }

    /**
     * 批量移动邮件到指定文件夹
     * @param localMessages 邮件集合
     * @param destFolder 目标文件夹
     */
    public void moveMessages(List<LocalMessage> localMessages, LocalFolder destFolder) throws MessagingException {
        netResource.moveMessages(localMessages, destFolder);
        localResource.moveMessages(localMessages, destFolder);
    }

    /**
     * 添加邮件到指定文件夹
     * @param destFolder 目标文件夹
     * @param message 需要添加的邮件
     */
    public void appendMessages(LocalFolder destFolder, LocalMessage message) throws MessagingException, UnsupportedEncodingException {
        netResource.appendMessage(destFolder, new MessageBuilder().build(message));
        localResource.appendMessage(destFolder, message);
    }

    /**
     * 发送邮件
     * @param localMessage 邮件
     * @throws UnsupportedEncodingException
     * @throws MessagingException
     */
    public void sendMessage(LocalMessage localMessage) throws UnsupportedEncodingException, MessagingException {
        netResource.sendMessage(localMessage);
    }
}
