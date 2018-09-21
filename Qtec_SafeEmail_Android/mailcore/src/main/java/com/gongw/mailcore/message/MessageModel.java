package com.gongw.mailcore.message;

import com.gongw.mailcore.folder.FolderModel;
import com.gongw.mailcore.folder.LocalFolder;

import java.io.IOException;
import java.util.List;

import javax.mail.Address;
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


}
