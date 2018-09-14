package com.gongw.mailcore.folder;


import org.litepal.LitePal;
import java.util.List;

/**
 * Created by gongw on 2018/9/11.
 */

public class FolderLocalResource {

    public List<LocalFolder> getFoldersByAccountId(long accountId){
        return LitePal.where("account_id = ?", String.valueOf(accountId)).find(LocalFolder.class);
    }

    public int getFolderCountByAccountId(long accountId){
        return LitePal.where("account_id = ?", String.valueOf(accountId)).count(LocalFolder.class);
    }

    public LocalFolder getFolderById(long id){
        return LitePal.find(LocalFolder.class, id);
    }

    public void saveOrUpdateFolder(List<LocalFolder> folderList){
        for(LocalFolder localFolder : folderList){
            saveOrUpdateFolder(localFolder);
        }
    }

    public void saveOrUpdateFolder(LocalFolder localFolder){
        localFolder.saveOrUpdate("url = ?", localFolder.getUrl());
    }

    public void deleteFolderById(long id){
        LitePal.delete(LocalFolder.class, id);
    }

    public void deleteFoldersByAccountId(long accountId){
        LitePal.deleteAll(LocalFolder.class, "account_id = ?", String.valueOf(accountId));
    }

    public void deleteAll(){
        LitePal.deleteAll(LocalFolder.class);
    }

}
