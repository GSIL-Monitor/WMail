package com.gongw.mailcore.folder;

import com.gongw.mailcore.net.MessageFetcher;
import com.gongw.mailcore.net.NetResource;
import com.gongw.mailcore.account.Account;
import com.sun.mail.imap.IMAPFolder;
import java.util.ArrayList;
import java.util.List;
import javax.mail.Folder;
import javax.mail.MessagingException;

/**
 * Created by gongw on 2018/9/11.
 */

public class FolderNetResource extends NetResource{

    private static class InstanceHolder{
        private static FolderNetResource instance = new FolderNetResource();
    }

    private FolderNetResource(){
    }

    public static FolderNetResource singleInstance(){
        return InstanceHolder.instance;
    }

    /**
     * 获取该邮箱下所有文件夹
     * @param account 邮箱账号信息
     * @return LocalFolder对象列表
     * @throws MessagingException
     */
    public List<LocalFolder> getAllFolders(Account account) throws MessagingException {
        MessageFetcher fetcher = getFetcher(account);
        List<LocalFolder> localFolders = new ArrayList<>();
        Folder[] folders = fetcher.fetchFolders();
        for(Folder folder : folders){
            //TODO:多级文件夹未处理
            if(folder.getType() == Folder.HOLDS_FOLDERS){
                continue;
            }
            LocalFolder localFolder = convertLocalFolder(account, folder);
            localFolders.add(localFolder);
        }
        return localFolders;
    }

    private LocalFolder convertLocalFolder(Account account, Folder folder) throws MessagingException {
        LocalFolder localFolder = new LocalFolder();
        localFolder.setAccount(account);
        String fullName = folder.getFullName();
        localFolder.setFullName(fullName);
        String[] attributesArray = ((IMAPFolder) folder).getAttributes();
        StringBuilder attributes = new StringBuilder();
        for(String attribute : attributesArray){
            attributes.append(attribute);
        }
        if(fullName.toLowerCase().contains(LocalFolder.Type.INBOX) || attributes.toString().toLowerCase().contains(LocalFolder.Type.INBOX)){
            localFolder.setLocalType(LocalFolder.Type.INBOX);
        }else if(fullName.toLowerCase().contains(LocalFolder.Type.SENT) || attributes.toString().toLowerCase().contains(LocalFolder.Type.SENT)){
            localFolder.setLocalType(LocalFolder.Type.SENT);
        }else if(fullName.toLowerCase().contains(LocalFolder.Type.TRASH) || attributes.toString().toLowerCase().contains(LocalFolder.Type.TRASH)) {
            localFolder.setLocalType(LocalFolder.Type.TRASH);
        }else if(fullName.toLowerCase().contains(LocalFolder.Type.DELETED) || attributes.toString().toLowerCase().contains(LocalFolder.Type.DELETED)){
            localFolder.setLocalType(LocalFolder.Type.DELETED);
        }else if(fullName.toLowerCase().contains(LocalFolder.Type.JUNK) || attributes.toString().toLowerCase().contains(LocalFolder.Type.JUNK)){
            localFolder.setLocalType(LocalFolder.Type.JUNK);
        }else if(fullName.toLowerCase().contains(LocalFolder.Type.DRAFT) || attributes.toString().toLowerCase().contains(LocalFolder.Type.DRAFT)){
            localFolder.setLocalType(LocalFolder.Type.DRAFT);
        }
        localFolder.setMsgCount(folder.getMessageCount());
        localFolder.setNewMsgCount(folder.getNewMessageCount());
        localFolder.setSeparator(folder.getSeparator());
        localFolder.setType(folder.getType());
        localFolder.setUrl(folder.getURLName().toString());
        return localFolder;
    }


}
