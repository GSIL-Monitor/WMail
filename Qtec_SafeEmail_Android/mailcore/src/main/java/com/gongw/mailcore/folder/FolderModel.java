package com.gongw.mailcore.folder;

import com.gongw.mailcore.account.Account;
import java.util.List;
import javax.mail.MessagingException;

/**
 * 邮箱文件夹业务模型，提供对LocalFolder数据的操作接口
 * Created by gongw on 2018/9/11.
 */

public class FolderModel {
    /**
     * 文件夹本地数据库资源对象，提供操作本地文件夹资源的接口
     */
    private FolderLocalResource folderLocalResource;
    /**
     * 文件夹网络资源对象，提供操作邮件服务器上的文件夹资源的接口
     */
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

    /**
     * 获取指定Account下的所有LocalFolder
     * 先从本地数据库获取，如果本地数据库有就直接返回
     * 如果本地数据库没有，则从网络获取，并将获取到的数据保存到本地数据库
     * @param account 指定的Account
     * @return LocalFolder集合
     * @throws MessagingException
     */
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

    /**
     * 从网络获取指定Account下的所有LocalFolder，
     * 并将获取到的数据保存到本地数据库
     * @param account 指定的Account
     * @throws MessagingException
     */
    public  void refreshFolders(Account account) throws MessagingException {
        List<LocalFolder> folders = folderNetResource.getAllFolders(account);
        folderLocalResource.saveOrUpdateFolder(folders);
    }

    /**
     * 获取指定Account下的已删除文件夹
     * 先从本地数据库获取，如果有符合条件的就直接返回
     * 如果本地没有找到，则从网络获取，并将获取到的数据保存到本地数据库
     * @param account
     * @return
     * @throws MessagingException
     */
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
