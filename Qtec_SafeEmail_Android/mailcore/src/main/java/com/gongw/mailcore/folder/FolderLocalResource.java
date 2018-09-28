package com.gongw.mailcore.folder;


import org.litepal.LitePal;
import java.util.List;

/**
 * 本地的邮箱文件夹资源，以数据库的形式保存LocalFolder，并提供操作数据库中LocalFolder数据的接口
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

    /**
     * 根据账号id获取LocalFolder
     * @param accountId 账号id
     * @return LocalFolder集合
     */
    public List<LocalFolder> getFoldersByAccountId(long accountId){
        return LitePal.where("account_id = ?", String.valueOf(accountId))
                .find(LocalFolder.class);
    }

    /**
     * 根据账号id和LocalFolder的fullName获取LocalFolder
     * @param accountId 账号id
     * @param fullName 文件夹的fullName
     * @return LocalFolder集合
     */
    public List<LocalFolder> getFolders(long accountId, String fullName){
        return LitePal.where("account_id = ? and fullName = ?", String.valueOf(accountId), fullName)
                .find(LocalFolder.class);
    }

    /**
     * 根据LocalFolder的id获取LocalFolder
     * @param id id
     * @return 符合id的LocalFolder
     */
    public LocalFolder getFolderById(long id){
        return LitePal.find(LocalFolder.class, id);
    }

    /**
     * 根据账号id获取已删除文件夹（LcaolType为TRASH或DELETED的LocalFolder）
     * @param accountId 账号id
     * @return LocalFolder
     */
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

    /**
     * 批量保存或修改LocalFolder到数据库
     * @param folderList LocalFolder集合
     */
    public void saveOrUpdateFolder(List<LocalFolder> folderList){
        for(LocalFolder localFolder : folderList){
            saveOrUpdateFolder(localFolder);
        }
    }

    /**
     * 保存或修改LocalFolder，如果url已存在则为修改操作，否则为保存操作
     * @param localFolder 要保存或修改的LocalFolder
     */
    public void saveOrUpdateFolder(LocalFolder localFolder){
        List<LocalFolder> localFolders = LitePal.where("url = ?", localFolder.getUrl())
                                                .find(LocalFolder.class);
        if(localFolders.size() < 1){
            localFolder.save();
        }else{
            localFolder.update(localFolders.get(0).getId());
        }
    }

    /**
     * 删除指定id的LocalFolder
     * @param id id
     */
    public void deleteFolderById(long id){
        LitePal.delete(LocalFolder.class, id);
    }

    /**
     * 删除指定Account的所有LocalFolder
     * @param accountId 指定Account的id
     */
    public void deleteFoldersByAccountId(long accountId){
        LitePal.deleteAll(LocalFolder.class, "account_id = ?", String.valueOf(accountId));
    }

    /**
     * 删除数据库中所有的LocalFolder数据
     */
    public void deleteAll(){
        LitePal.deleteAll(LocalFolder.class);
    }

}
