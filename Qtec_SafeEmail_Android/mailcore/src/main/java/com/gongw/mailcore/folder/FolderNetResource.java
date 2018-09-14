package com.gongw.mailcore.folder;

import com.gongw.mailcore.account.Account;
import com.gongw.mailcore.IMAPFetcher;
import java.util.ArrayList;
import java.util.List;
import javax.mail.Folder;
import javax.mail.MessagingException;
import javax.mail.URLName;

/**
 * Created by gongw on 2018/9/11.
 */

public class FolderNetResource {

    public List<LocalFolder> getAllFolders(Account account) throws MessagingException {
        IMAPFetcher imapFetcher = new IMAPFetcher(new URLName(account.getStoreUrl()));
        Folder[] folders = imapFetcher.fetchFolders();
        List<LocalFolder> localFolders = new ArrayList<>();
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
