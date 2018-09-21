package com.gongw.mailcore.part;

import org.litepal.LitePal;
import java.util.List;

import javax.mail.Part;

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

    public List<LocalPart> getParts(long localMessage_id){
        return LitePal.where("localmessage_id = ?", String.valueOf(localMessage_id))
                .find(LocalPart.class);
    }

    public LocalPart getAttachPart(long localMessage_id, long index){
        List<LocalPart> parts = LitePal.where("localmessage_id = ? and disposition = ? and index = ?", String.valueOf(localMessage_id), Part.ATTACHMENT, String.valueOf(index))
                .find(LocalPart.class);
        return parts.size()>0 ? parts.get(0) : null;
    }

    public List<LocalPart> getContentParts(long localMessage_id){
        return LitePal.where("localmessage_id = ? and disposition = null", String.valueOf(localMessage_id))
                .find(LocalPart.class);
    }

    public List<LocalPart> getInLineParts(long localMessage_id){
        return LitePal.where("localmessage_id = ? and disposition = ?", String.valueOf(localMessage_id), Part.INLINE)
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
