package com.gongw.mailcore.folder;

import com.gongw.mailcore.account.Account;
import java.util.List;
import javax.mail.MessagingException;

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
        folderLocalResource = FolderLocalResource.singleInstance();
        folderNetResource = FolderNetResource.singleInstance();
    }

    public static FolderModel singleInstance(){
        return InstanceHolder.instance;
    }

    public List<LocalFolder> getFolders(Account account) throws MessagingException {
        List<LocalFolder> folderList = folderLocalResource.getFoldersByAccountId(account.getId());
        if(folderList.size() < 1){
            refreshFolders(account);
            folderList = folderLocalResource.getFoldersByAccountId(account.getId());
        }
        for(LocalFolder localFolder : folderList){
            localFolder.setAccount(account);
        }
        account.setFolderList(folderList);
        return folderList;
    }

    public  void refreshFolders(Account account) throws MessagingException {
        List<LocalFolder> folders = folderNetResource.getAllFolders(account);
        folderLocalResource.saveOrUpdateFolder(folders);
    }

    public LocalFolder getTrashOrDeletedFolder(Account account) throws MessagingException {
        LocalFolder localFolder = folderLocalResource.singleInstance().getTrashOrDeletedFolder(account.getId());
        if(localFolder == null){
            refreshFolders(account);
            localFolder = folderLocalResource.singleInstance().getTrashOrDeletedFolder(account.getId());
        }
        if(localFolder != null){
            localFolder.setAccount(account);
        }
        return localFolder;
    }

}
