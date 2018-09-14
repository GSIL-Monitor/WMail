package com.gongw.mailcore.part;

import com.gongw.mailcore.account.Account;
import com.gongw.mailcore.message.LocalMessage;
import com.gongw.mailcore.setting.MailSession;

import java.util.List;

import javax.mail.MessagingException;
import javax.mail.Store;

/**
 * Created by gongw on 2018/9/14.
 */

public class PartModel {

    private PartLocalResource partLocalResource;

    private static class InstanceHolder{
        private static PartModel instance = new PartModel();
    }

    private PartModel(){
        partLocalResource = new PartLocalResource();
    }

    public static PartModel singleInstance(){
        return InstanceHolder.instance;
    }

    public List<LocalPart> getAllParts(LocalMessage localMessage) throws MessagingException {
        int partCount = partLocalResource.getPartsCountByMsgId(localMessage.getId());
        if(partCount > 0 ){
            return partLocalResource.getPartsByMsgId(localMessage.getId());
        }
        updatePartLocalResource(localMessage);
        return partLocalResource.getPartsByMsgId(localMessage.getId());
    }

    public void updatePartLocalResource(LocalMessage localMessage) throws MessagingException {
        Store store = MailSession.getDefaultSession().getStore(new URLName(account.getStoreUrl()));
        store.connect();
        Folder[] folders = store.getDefaultFolder().list();
        for(Folder folder : folders){
            LocalFolder localFolder = new LocalFolder();
            localFolder.setAccount(account);
            localFolder.setFullName(folder.getFullName());
            localFolder.setMsgCount(folder.getMessageCount());
            localFolder.setNewMsgCount(folder.getNewMessageCount());
            localFolder.setSeparator(folder.getSeparator());
            localFolder.setType(folder.getType());
            localFolder.setUrl(folder.getURLName().toString());
            folderLocalResource.saveOrUpdateFolder(localFolder);
        }
    }

}
