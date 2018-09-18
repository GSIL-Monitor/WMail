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
        if(folderList.size() < 1 ){
            folderList = getFreshFolders(account);
        }
        for(LocalFolder localFolder : folderList){
            localFolder.setAccount(account);
        }
        account.setFolderList(folderList);
        return folderList;
    }

    public  List<LocalFolder> getFreshFolders(Account account) throws MessagingException {
        List<LocalFolder> folders = folderNetResource.getAllFolders(account);
        folderLocalResource.saveOrUpdateFolder(folders);
        return folders;
    }



}
