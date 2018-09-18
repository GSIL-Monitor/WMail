package com.gongw.mailcore.message;

import org.litepal.LitePal;
import java.util.List;

/**
 * Created by gongw on 2018/7/17.
 */

public class MessageLocalResource {

    private static class InstanceHolder{
        private static MessageLocalResource instance = new MessageLocalResource();
    }

    private MessageLocalResource(){
    }

    public static MessageLocalResource singleInstance(){
        return InstanceHolder.instance;
    }

    public List<LocalMessage> getMessagesByFolderId(long folderId, int limit, int offset){
        return LitePal.where("localfolder_id = ?", String.valueOf(folderId))
                .limit(limit)
                .offset(offset)
                .order("receiveDate desc")
                .find(LocalMessage.class);
    }

    public int getMsgCountByFolderId(long folderId, int limit, int offset){
        return LitePal.where("localfolder_id = ?", String.valueOf(folderId))
                .limit(limit)
                .offset(offset)
                .count(LocalMessage.class);
    }

    public void saveOrUpdateMessages(List<LocalMessage> localMessages){
        for(LocalMessage localMessage : localMessages){
            saveOrUpdateMessage(localMessage);
        }
    }

    public void saveOrUpdateMessage(LocalMessage localMessage){
        localMessage.saveOrUpdate("localfolder_id = ? and uid = ?", String.valueOf(localMessage.getFolder().getId()), localMessage.getUid());
    }

    public void deleteMessageById(long id){
        LitePal.delete(LocalMessage.class, id);
    }

    public void deleteMessagesByFolderId(long folderId){
        LitePal.deleteAll(LocalMessage.class, "localfolder_id = ?", String.valueOf(folderId));
    }

    public void deleteAllMessages(){
        LitePal.deleteAll(LocalMessage.class);
    }

}
