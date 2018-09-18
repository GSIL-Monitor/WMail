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
        List<LocalPart> partList = partLocalResource.getPartsByMsgId(localMessage.getId());
        if(partList.size() < 1 ){
            partList = getFreshParts(localMessage);
        }
        for(LocalPart localPart : partList){
            localPart.setLocalMessage(localMessage);
        }
        localMessage.setPartList(partList);
        return partList;
    }

    public List<LocalPart> getFreshParts(LocalMessage localMessage) throws MessagingException, IOException {
        List<LocalPart> localParts = partNetResource.getParts(localMessage);
        partLocalResource.saveOrUpdateParts(localParts);
        return localParts;
    }



}
