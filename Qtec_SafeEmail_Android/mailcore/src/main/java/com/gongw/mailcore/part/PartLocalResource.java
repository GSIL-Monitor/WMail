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
        List<LocalPart> parts = LitePal.where("localmessage_id = ? and type = ? and index = ?", String.valueOf(localMessage_id), LocalPart.Type.ATTACHMENT, String.valueOf(index))
                .find(LocalPart.class);
        return parts.size()>0 ? parts.get(0) : null;
    }

    public List<LocalPart> getHtmlContentParts(long localMessage_id){
        return LitePal.where("localmessage_id = ? and type = ?", String.valueOf(localMessage_id), LocalPart.Type.HTML_CONTENT)
                .find(LocalPart.class);
    }

    public List<LocalPart> getTextContentParts(long localMessage_id){
        return LitePal.where("localmessage_id = ? and type = ?", String.valueOf(localMessage_id), LocalPart.Type.TEXT_CONTENT)
                .find(LocalPart.class);
    }

    public List<LocalPart> getInLineParts(long localMessage_id){
        return LitePal.where("localmessage_id = ? and type = ?", String.valueOf(localMessage_id), LocalPart.Type.INLINE)
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
