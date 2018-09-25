package com.gongw.mailcore.part;

import com.gongw.mailcore.message.LocalMessage;
import java.io.IOException;
import java.util.List;
import javax.mail.MessagingException;

/**
 * Created by gongw on 2018/9/14.
 */

public class PartModel {

    private PartLocalResource partLocalResource;
    private PartNetResource partNetResource;


    private static class InstanceHolder{
        private static PartModel instance = new PartModel();
    }

    private PartModel(){
        partLocalResource = PartLocalResource.singleInstance();
        partNetResource = PartNetResource.singleInstance();
    }

    public static PartModel singleInstance(){
        return InstanceHolder.instance;
    }

    public List<LocalPart> getParts(LocalMessage localMessage) throws MessagingException, IOException {
        List<LocalPart> partList = partLocalResource.getParts(localMessage.getId());
        if(partList.size() < 1 ){
            refreshParts(localMessage);
            partList = partLocalResource.getParts(localMessage.getId());
        }
        for(LocalPart localPart : partList){
            localPart.setLocalMessage(localMessage);
        }
        localMessage.setPartList(partList);
        return partList;
    }

    public void refreshParts(LocalMessage localMessage) throws MessagingException, IOException {
        List<LocalPart> localParts = partNetResource.getAllParts(localMessage);
        partLocalResource.saveOrUpdateParts(localParts);
    }

    public List<LocalPart> getContentParts(LocalMessage localMessage) throws IOException, MessagingException {
        List<LocalPart> contentParts = partLocalResource.getHtmlContentParts(localMessage.getId());
        if(contentParts.size() < 1 ){
            refreshContentParts(localMessage);
            contentParts = partLocalResource.getHtmlContentParts(localMessage.getId());
        }
        for(LocalPart localPart : contentParts){
            localPart.setLocalMessage(localMessage);
        }
        return contentParts;
    }

    public void refreshContentParts(LocalMessage localMessage) throws IOException, MessagingException {
        List<LocalPart> contentParts = partNetResource.getContentParts(localMessage);
        partLocalResource.saveOrUpdateParts(contentParts);
    }

    public List<LocalPart> getInlineParts(LocalMessage localMessage) throws IOException, MessagingException {
        List<LocalPart> inLineParts = partLocalResource.getInLineParts(localMessage.getId());
        if(inLineParts.size() < 1 ){
            refreshInlineParts(localMessage);
            inLineParts = partLocalResource.getInLineParts(localMessage.getId());
        }
        for(LocalPart localPart : inLineParts){
            localPart.setLocalMessage(localMessage);
        }
        return inLineParts;
    }

    public void refreshInlineParts(LocalMessage localMessage) throws IOException, MessagingException {
        List<LocalPart> inLineParts = partNetResource.getInlineParts(localMessage);
        partLocalResource.saveOrUpdateParts(inLineParts);
    }

    public LocalPart getAttachmentPart(LocalMessage localMessage, int index) throws IOException, MessagingException {
        LocalPart attachmentPart =  partLocalResource.getAttachPart(localMessage.getId(), index);
        if(attachmentPart == null){
            refreshAttachmentPart(localMessage, index);
            attachmentPart =  partLocalResource.getAttachPart(localMessage.getId(), index);
        }
        attachmentPart.setLocalMessage(localMessage);
        return attachmentPart;
    }

    public void refreshAttachmentPart(LocalMessage localMessage, int index) throws IOException, MessagingException {
        LocalPart localPart = partNetResource.getAttachmentPartByIndex(localMessage, index);
        partLocalResource.saveOrUpdatePart(localPart);
    }

}
