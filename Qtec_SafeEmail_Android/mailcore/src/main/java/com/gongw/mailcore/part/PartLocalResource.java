package com.gongw.mailcore.part;

import org.litepal.LitePal;
import java.util.List;

/**
 * Created by gongw on 2018/9/14.
 */

public class PartLocalResource {

    private static class InstanceHolder{
        private static PartLocalResource instance = new PartLocalResource();
    }

    private PartLocalResource(){
    }

    public static PartLocalResource singleInstance(){
        return InstanceHolder.instance;
    }

    public int getPartsCountByMsgId(long id){
        return LitePal.where("localmessage_id = ?", String.valueOf(id))
                .count(LocalPart.class);
    }

    public List<LocalPart> getPartsByMsgId(long id){
        return LitePal.where("localmessage_id = ?", String.valueOf(id))
                .find(LocalPart.class);
    }

    public void saveOrUpdateParts(List<LocalPart> localParts){
        for(LocalPart part : localParts){
            saveOrUpdatePart(part);
        }
    }

    public void saveOrUpdatePart(LocalPart localPart){
        localPart.saveOrUpdate("localmessage_id = ? and fileName = ? and contentType = ?", String.valueOf(localPart.getLocalMessage().getId()),
                                                                                                        String.valueOf(localPart.getFileName()),
                                                                                                        localPart.getContentType());
    }

}
