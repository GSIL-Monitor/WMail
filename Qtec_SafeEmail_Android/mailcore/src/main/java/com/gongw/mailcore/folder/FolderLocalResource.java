package com.gongw.mailcore.folder;


import org.litepal.LitePal;
import java.util.List;

/**
 * Created by gongw on 2018/9/11.
 */

public class FolderLocalResource {

    private static class InstanceHolder{
        private static FolderLocalResource instance = new FolderLocalResource();
    }

    private FolderLocalResource(){
    }

    public static FolderLocalResource singleInstance(){
        return InstanceHolder.instance;
    }

    public List<LocalFolder> getFoldersByAccountId(long accountId){
        return LitePal.where("account_id = ?", String.valueOf(accountId))
                .find(LocalFolder.class);
    }

    public List<LocalFolder> getFolders(long accountId, String fullName){
        return LitePal.where("account_id = ? and fullName = ?", String.valueOf(accountId), fullName)
                .find(LocalFolder.class);
    }

    public int getFolderCountByAccountId(long accountId){
        return LitePal.where("account_id = ?", String.valueOf(accountId)).count(LocalFolder.class);
    }

    public LocalFolder getFolderById(long id){
        return LitePal.find(LocalFolder.class, id);
    }

    public LocalFolder getTrashOrDeletedFolder(long accountId){
        List<LocalFolder> localFolders = getFoldersByAccountId(accountId);
        for(LocalFolder localFolder : localFolders){
            String localType = localFolder.getLocalType();
            if(LocalFolder.Type.TRASH.equals(localType) || LocalFolder.Type.DELETED.equals(localType)){
                return localFolder;
            }
        }
        return null;
    }

    public void saveOrUpdateFolder(List<LocalFolder> folderList){
        for(LocalFolder localFolder : folderList){
            saveOrUpdateFolder(localFolder);
        }
    }

    public void saveOrUpdateFolder(LocalFolder localFolder){
        List<LocalFolder> localFolders = LitePal.where("url = ?", localFolder.getUrl())
                                                .find(LocalFolder.class);
        if(localFolders.size() < 1){
            localFolder.save();
        }else{
            localFolder.update(localFolders.get(0).getId());
        }
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
