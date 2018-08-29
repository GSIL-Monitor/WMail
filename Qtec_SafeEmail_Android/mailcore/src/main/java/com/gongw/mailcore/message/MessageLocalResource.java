package com.gongw.mailcore.message;

import android.content.ContentValues;

import org.litepal.LitePal;

import java.util.List;

/**
 * Created by gongw on 2018/7/17.
 */

public class MessageLocalResource {



    public void insertMessage(LocalMessage localMessage){
        localMessage.save();
    }

    public void insertMessages(List<LocalMessage> localMessageList){
        LitePal.saveAll(localMessageList);
    }

    public void deleteMessage(long id){
        LitePal.delete(LocalMessage.class, id);
    }

    public void deleteMessages(String... conditions){
        LitePal.deleteAll(LocalMessage.class, conditions);
    }

    public void updateMessage(ContentValues contentValues, long id){
        LitePal.update(LocalMessage.class, contentValues, id);
    }

    public void updateMessages(ContentValues contentValues, String... conditions){
        LitePal.updateAll(LocalMessage.class, contentValues, conditions);
    }

}
