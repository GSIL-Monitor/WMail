package com.gongw.mailcore.message;

import com.gongw.mailcore.account.Account;
import com.gongw.mailcore.folder.FolderModel;
import com.gongw.mailcore.folder.LocalFolder;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.mail.Flags;
import javax.mail.MessagingException;

/**
 * Created by gongw on 2018/7/17.
 */

public class MessageModel {

    private MessageLocalResource localResource;
    private MessageNetResource netResource;
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

    public List<LocalMessage> getMessages(LocalFolder localFolder, int pageIndex) throws MessagingException, IOException {
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

    public void flagMessages(List<LocalMessage> localMessages, Flags.Flag flag, boolean set) throws MessagingException {
        netResource.flagMessages(localMessages, flag, set);
        localResource.flagMessage(localMessages, flag, set);
    }

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
                    netResource.moveMessages(messages, destFolder);
                    localResource.moveMessages(messages, destFolder);
                }else{
                    netResource.deleteMessages(messages);
                    localResource.deleteMessages(messages);
                }
            }
        }
    }

}
