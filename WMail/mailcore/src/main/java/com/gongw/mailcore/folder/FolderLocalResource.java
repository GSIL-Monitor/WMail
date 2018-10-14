package com.gongw.mailcore.folder;


import com.gongw.mailcore.message.LocalMessage;
import com.gongw.mailcore.part.LocalPart;

import org.litepal.LitePal;
import java.io.File;
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
     * 获取所有的LocalFolder数据
     * @return LocalFolder集合
     */
    public List<LocalFolder> getAllFolders(){
        return LitePal.findAll(LocalFolder.class);
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
     * 根据账号id获取收件箱文件夹
     * @param accountId 账号id
     * @return LocalFolder
     */
    public LocalFolder getInboxFolder(long accountId){
        List<LocalFolder> localFolders = getFoldersByAccountId(accountId);
        for(LocalFolder localFolder : localFolders){
            String localType = localFolder.getLocalType();
            if(LocalFolder.Type.INBOX.equals(localType)){
                return localFolder;
            }
        }
        return null;
    }

    /**
     * 根据账号id和LocalFolder的fullName获取LocalFolder
     * @param accountId 账号id
     * @param fullName 名称
     * @return LocalFolder
     */
    public LocalFolder getFolder(long accountId, String fullName){
        List<LocalFolder> localFolders = LitePal.where("account_id = ? and fullName = ?", String.valueOf(accountId), fullName).find(LocalFolder.class);
        if(localFolders.size() > 0){
            return localFolders.get(0);
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
        LocalFolder localFolder = getFolderById(id);
        if(localFolder == null){
            return;
        }
        //获取文件夹下的邮件数据
        List<LocalMessage> localMessages = LitePal.where("localfolder_id = ?", String.valueOf(localFolder.getId()))
                .find(LocalMessage.class);
        for(LocalMessage localMessage : localMessages){
            //获取邮件相关的附件和正文等PART数据
            List<LocalPart> localParts = LitePal.where("localmessage_id = ?", String.valueOf(localMessage.getId()))
                    .find(LocalPart.class);
            for(LocalPart localPart : localParts){
                //删除缓存的附件和正文
                if(localPart.getDataLocation() == LocalPart.Location.LOCATION_ON_DISK && localPart.getLocalPath() != null){
                    File file = new File(localPart.getLocalPath());
                    if(file.exists()){
                        file.delete();
                    }
                }
                //删除part数据
                localPart.delete();
            }
            //删除邮件数据
            localMessage.delete();
        }
        //删除文件夹数据
        localFolder.delete();
    }

    /**
     * 删除指定Account的所有LocalFolder
     * @param accountId 指定Account的id
     */
    public void deleteFoldersByAccountId(long accountId){
        List<LocalFolder> localFolders = getFoldersByAccountId(accountId);
        if(localFolders == null || localFolders.size() < 1){
            return;
        }
        for(LocalFolder localFolder : localFolders){
            deleteFolderById(localFolder.getId());
        }
    }

    /**
     * 删除数据库中所有的LocalFolder数据
     */
    public void deleteAll(){
        List<LocalFolder> localFolders = getAllFolders();
        if(localFolders == null || localFolders.size() < 1){
            return;
        }
        for(LocalFolder localFolder : localFolders){
            deleteFolderById(localFolder.getId());
        }
    }

}
