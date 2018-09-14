package com.gongw.mailcore.folder;

import com.gongw.mailcore.account.Account;
import com.gongw.mailcore.setting.MailSession;
import java.util.List;
import javax.mail.Folder;
import javax.mail.MessagingException;
import javax.mail.Store;
import javax.mail.URLName;

/**
 * Created by gongw on 2018/9/11.
 */

public class FolderModel {

    private FolderLocalResource folderLocalResource;
    private FolderNetResource folderNetResource;

    private static class InstanceHolder{
        private static FolderModel instance = new FolderModel();
    }

    private FolderModel(){
        folderLocalResource = new FolderLocalResource();
        folderNetResource = new FolderNetResource();
    }

    public static FolderModel singleInstance(){
        return InstanceHolder.instance;
    }

    public List<LocalFolder> getAllFolders(Account account) throws MessagingException {
        int folderCount = folderLocalResource.getFolderCountByAccountId(account.getId());
        if(folderCount > 0 ){
            return folderLocalResource.getFoldersByAccountId(account.getId());
        }
        updateFolderLocalResource(account);
        return folderLocalResource.getFoldersByAccountId(account.getId());
    }

    public void updateFolderLocalResource(Account account) throws MessagingException {
        Store store = MailSession.getDefaultSession().getStore(new URLName(account.getStoreUrl()));
        store.connect();
        Folder[] folders = store.getDefaultFolder().list();
        for(Folder folder : folders){
            LocalFolder localFolder = new LocalFolder();
            localFolder.setAccount(account);
            localFolder.setFullName(folder.getFullName());
            localFolder.setMsgCount(folder.getMessageCount());
            localFolder.setNewMsgCount(folder.getNewMessageCount());
            localFolder.setSeparator(folder.getSeparator());
            localFolder.setType(folder.getType());
            localFolder.setUrl(folder.getURLName().toString());
            folderLocalResource.saveOrUpdateFolder(localFolder);
        }
    }



}
