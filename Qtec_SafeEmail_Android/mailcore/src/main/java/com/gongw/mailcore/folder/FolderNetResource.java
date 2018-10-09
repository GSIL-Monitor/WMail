package com.gongw.mailcore.folder;

import com.gongw.mailcore.Mail;
import com.gongw.mailcore.R;
import com.gongw.mailcore.net.NetResource;
import com.gongw.mailcore.account.Account;
import com.sun.mail.imap.IMAPFolder;
import java.util.ArrayList;
import java.util.List;
import javax.mail.Folder;
import javax.mail.MessagingException;
import javax.mail.Store;

/**
 * 网络上的邮箱文件夹资源类，提供操作网络上的Folder数据的接口
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
     * 获取指定Account下所有文件夹
     * @param account 邮箱账号信息Account
     * @return LocalFolder对象列表
     * @throws MessagingException
     */
    public List<LocalFolder> getAllFolders(Account account) throws MessagingException {
        Store store = getStore(account);
        Folder[] folders = store.getDefaultFolder().list();
        List<LocalFolder> localFolders = new ArrayList<>();
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

    /**
     * 将Folder对象转换为LocalFolder对象
     * @param account 邮箱账号信息Account
     * @param folder 从网络上获取的文件夹类
     * @return LocalFolder
     * @throws MessagingException
     */
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
            localFolder.setLocalName(Mail.getAppContext().getString(R.string.inbox));
        }else if(fullName.toLowerCase().contains(LocalFolder.Type.SENT) || attributes.toString().toLowerCase().contains(LocalFolder.Type.SENT)){
            localFolder.setLocalType(LocalFolder.Type.SENT);
            localFolder.setLocalName(Mail.getAppContext().getString(R.string.sent_mails));
        }else if(fullName.toLowerCase().contains(LocalFolder.Type.TRASH) || attributes.toString().toLowerCase().contains(LocalFolder.Type.TRASH)) {
            localFolder.setLocalType(LocalFolder.Type.TRASH);
            localFolder.setLocalName(Mail.getAppContext().getString(R.string.deleted_mails));
        }else if(fullName.toLowerCase().contains(LocalFolder.Type.DELETED) || attributes.toString().toLowerCase().contains(LocalFolder.Type.DELETED)){
            localFolder.setLocalType(LocalFolder.Type.DELETED);
            localFolder.setLocalName(Mail.getAppContext().getString(R.string.deleted_mails));
        }else if(fullName.toLowerCase().contains(LocalFolder.Type.JUNK) || attributes.toString().toLowerCase().contains(LocalFolder.Type.JUNK)){
            localFolder.setLocalType(LocalFolder.Type.JUNK);
            localFolder.setLocalName(Mail.getAppContext().getString(R.string.junk_mails));
        }else if(fullName.toLowerCase().contains(LocalFolder.Type.DRAFT) || attributes.toString().toLowerCase().contains(LocalFolder.Type.DRAFT)){
            localFolder.setLocalType(LocalFolder.Type.DRAFT);
            localFolder.setLocalName(Mail.getAppContext().getString(R.string.draft_mails));
        }
        localFolder.setMsgCount(folder.getMessageCount());
        localFolder.setNewMsgCount(folder.getNewMessageCount());
        localFolder.setSeparator(folder.getSeparator());
        localFolder.setType(folder.getType());
        localFolder.setUrl(folder.getURLName().toString());
        return localFolder;
    }


}
