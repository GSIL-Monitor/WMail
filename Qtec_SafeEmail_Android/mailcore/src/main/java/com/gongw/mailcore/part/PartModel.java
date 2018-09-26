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

    public List<LocalPart> getParts(LocalMessage localMessage, boolean saveToLocal) throws MessagingException, IOException {
        List<LocalPart> partList = partLocalResource.getParts(localMessage.getId());
        if(partList.size() < 1 ){
            refreshParts(localMessage, saveToLocal);
            partList = partLocalResource.getParts(localMessage.getId());
        }
        for(LocalPart localPart : partList){
            localPart.setLocalMessage(localMessage);
        }
        localMessage.setPartList(partList);
        return partList;
    }

    public void refreshParts(LocalMessage localMessage, boolean saveToLocal) throws MessagingException, IOException {
        List<LocalPart> localParts = partNetResource.getAllParts(localMessage, saveToLocal);
        partLocalResource.saveOrUpdateParts(localParts);
    }

    public List<LocalPart> getContentParts(LocalMessage localMessage, boolean saveToLocal) throws IOException, MessagingException {
        List<LocalPart> contentParts = partLocalResource.getHtmlContentParts(localMessage.getId());
        if(contentParts.size() < 1 ){
            refreshContentParts(localMessage, saveToLocal);
            contentParts = partLocalResource.getHtmlContentParts(localMessage.getId());
        }
        for(LocalPart localPart : contentParts){
            localPart.setLocalMessage(localMessage);
        }
        return contentParts;
    }

    public void refreshContentParts(LocalMessage localMessage, boolean saveToLocal) throws IOException, MessagingException {
        List<LocalPart> contentParts = partNetResource.getContentParts(localMessage, saveToLocal);
        partLocalResource.saveOrUpdateParts(contentParts);
    }

    public List<LocalPart> getInlineParts(LocalMessage localMessage, boolean saveToLocal) throws IOException, MessagingException {
        List<LocalPart> inLineParts = partLocalResource.getInLineParts(localMessage.getId());
        if(inLineParts.size() < 1 ){
            refreshInlineParts(localMessage, saveToLocal);
            inLineParts = partLocalResource.getInLineParts(localMessage.getId());
        }
        for(LocalPart localPart : inLineParts){
            localPart.setLocalMessage(localMessage);
        }
        return inLineParts;
    }

    public void refreshInlineParts(LocalMessage localMessage, boolean saveToLocal) throws IOException, MessagingException {
        List<LocalPart> inLineParts = partNetResource.getInlineParts(localMessage, saveToLocal);
        partLocalResource.saveOrUpdateParts(inLineParts);
    }

    public LocalPart getAttachmentPart(LocalMessage localMessage, int index, boolean saveToLocal) throws IOException, MessagingException {
        LocalPart attachmentPart =  partLocalResource.getAttachPart(localMessage.getId(), index);
        if(attachmentPart == null){
            refreshAttachmentPart(localMessage, index, saveToLocal);
            attachmentPart =  partLocalResource.getAttachPart(localMessage.getId(), index);
        }
        attachmentPart.setLocalMessage(localMessage);
        return attachmentPart;
    }

    public void refreshAttachmentPart(LocalMessage localMessage, int index, boolean saveToLocal) throws IOException, MessagingException {
        LocalPart localPart = partNetResource.getAttachmentPartByIndex(localMessage, index, saveToLocal);
        partLocalResource.saveOrUpdatePart(localPart);
    }
}
