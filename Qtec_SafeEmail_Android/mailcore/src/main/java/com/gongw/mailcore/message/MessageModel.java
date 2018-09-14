package com.gongw.mailcore.message;

import com.gongw.mailcore.account.Account;
import com.gongw.mailcore.folder.LocalFolder;
import com.gongw.mailcore.setting.MailSession;
import com.sun.mail.imap.IMAPFolder;
import com.sun.mail.imap.IMAPMessage;
import com.sun.mail.pop3.POP3Folder;
import com.sun.mail.pop3.POP3Message;

import java.util.List;

import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.NoSuchProviderException;
import javax.mail.Store;
import javax.mail.URLName;

/**
 * Created by gongw on 2018/7/17.
 */

public class MessageModel {

    private MessageLocalResource localResource;
    private MessageNetResource netResource;
    private int pageSize = 20;

    private MessageModel(){
        localResource = new MessageLocalResource();
        netResource = new MessageNetResource();
    }

    private static class InstanceHolder{
        private static MessageModel instance = new MessageModel();
    }

    public static MessageModel singleInstance(){
        return InstanceHolder.instance;
    }

    public List<LocalMessage> getMessages(Account account, LocalFolder localFolder, int pageIndex) throws MessagingException {
        int msgCount = localResource.getMsgCountByFolderId(localFolder.getId(), pageSize, pageIndex * pageSize);
        if(msgCount > 0){
            return localResource.getMessagesByFolderId(localFolder.getId(), pageSize, pageIndex * pageSize);
        }
        updateLocalResource(account, localFolder, pageIndex);
        return localResource.getMessagesByFolderId(localFolder.getId(), pageSize, pageIndex * pageSize);
    }

    public void updateLocalResource(Account account, LocalFolder localFolder, int pageIndex) throws MessagingException {
        Store store = MailSession.getDefaultSession().getStore(new URLName(account.getStoreUrl()));
        store.connect();
        Folder folder = store.getFolder(localFolder.getFullName());
        folder.open(Folder.READ_ONLY);
        int msgCount = folder.getMessageCount();
        if(msgCount == 0){
            folder.close();
            return;
        }
        int start = msgCount - (pageIndex + 1) * pageSize;
        start = start < 1 ? 1 : start;
        int end = msgCount - pageIndex * pageSize;
        end = end < 1 ? msgCount : end;
        Message[] messages = folder.getMessages(start, end);
        if(folder instanceof IMAPFolder){
            IMAPFolder imapFolder = (IMAPFolder) folder;
            for(Message message : messages){
                IMAPMessage imapMessage = (IMAPMessage) message;
                String uid = String.valueOf(imapFolder.getUID(message));
                LocalMessage localMessage = new LocalMessage();
                localMessage.setFolder(localFolder);
                localMessage.setUid(uid);
                localResource.saveOrUpdateMessages(localMessage);
            }
        }else if(folder instanceof POP3Folder){
            POP3Folder pop3Folder = (POP3Folder) folder;
            for(Message message : messages){
                POP3Message pop3Message = (POP3Message) message;
                String uid = pop3Folder.getUID(message);
                LocalMessage localMessage = new LocalMessage();
                localMessage.setFolder(localFolder);
                localMessage.setUid(uid);
                localResource.saveOrUpdateMessages(localMessage);
            }
        }
        folder.close();
    }

}
