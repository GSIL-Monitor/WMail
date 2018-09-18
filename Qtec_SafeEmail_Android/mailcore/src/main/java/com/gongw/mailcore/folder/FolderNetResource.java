package com.gongw.mailcore.folder;

import com.gongw.mailcore.MailFetcher;
import com.gongw.mailcore.NetResource;
import com.gongw.mailcore.account.Account;
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
        MailFetcher fetcher = getFetcher(account);
        List<LocalFolder> localFolders = new ArrayList<>();
        Folder[] folders = fetcher.fetchFolders();
        for(Folder folder : folders){
            LocalFolder localFolder = new LocalFolder();
            localFolder.setAccount(account);
            localFolder.setFullName(folder.getFullName());
            localFolder.setMsgCount(folder.getMessageCount());
            localFolder.setNewMsgCount(folder.getNewMessageCount());
            localFolder.setSeparator(folder.getSeparator());
            localFolder.setType(folder.getType());
            localFolder.setUrl(folder.getURLName().toString());
            localFolders.add(localFolder);
        }
        return localFolders;
    }



}
