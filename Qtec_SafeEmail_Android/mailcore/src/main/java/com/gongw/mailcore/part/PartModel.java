package com.gongw.mailcore.part;

import com.gongw.mailcore.message.LocalMessage;
import java.io.IOException;
import java.util.List;
import javax.mail.MessagingException;

/**
 * 邮件part的业务模型，提供对LocalPart数据的操作接口
 * Created by gongw on 2018/9/14.
 */
public class PartModel {
    /**
     * 邮件part本地数据库资源对象，提供操作本地邮件part资源的接口
     */
    private PartLocalResource partLocalResource;
    /**
     *邮件part网络资源对象，提供操作邮件服务器上的邮件part资源的接口
     */
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

    /**
     * 获取指定邮件的part信息
     * 先从本地资源获取，如果获取到了就直接返回
     * 否则再从网络资源获取，并将获取到的数据更新到本地资源
     * @param localMessage 指定邮件
     * @param saveToLocal 是否将part内容保存到本地
     * @return LocalPart集合
     * @throws MessagingException
     * @throws IOException
     */
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

    /**
     * 从网络资源获取邮件的part，并将获取到的part数据保存到本地数据库
     * @param localMessage 指定邮件
     * @param saveToLocal 是否将part内容保存到本地
     * @throws MessagingException
     * @throws IOException
     */
    public void refreshParts(LocalMessage localMessage, boolean saveToLocal) throws MessagingException, IOException {
        List<LocalPart> localParts = partNetResource.getAllParts(localMessage, saveToLocal);
        partLocalResource.saveOrUpdateParts(localParts);
    }

    /**
     * 获取指定邮件的正文part
     * 先从本地资源获取，如果获取到了就直接返回
     * 否则再从网络资源获取，并将获取到的数据更新到本地资源
     * @param localMessage 指定邮件
     * @param saveToLocal 是否将part内容保存到本地
     * @return 正文part集合
     * @throws IOException
     * @throws MessagingException
     */
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

    /**
     * 从网络资源获取邮件的正文part，并将获取到的part数据保存到本地数据库
     * @param localMessage 指定邮件
     * @param saveToLocal 是否将part内容保存到本地
     * @throws IOException
     * @throws MessagingException
     */
    public void refreshContentParts(LocalMessage localMessage, boolean saveToLocal) throws IOException, MessagingException {
        List<LocalPart> contentParts = partNetResource.getContentParts(localMessage, saveToLocal);
        partLocalResource.saveOrUpdateParts(contentParts);
    }

    /**
     * 获取指定邮件的正文内联引用part
     * 先从本地资源获取，如果获取到了就直接返回
     * 否则再从网络资源获取，并将获取到的数据更新到本地资源
     * @param localMessage 指定邮件
     * @param saveToLocal 是否将part内容保存到本地
     * @return 正文内联引用part集合
     * @throws IOException
     * @throws MessagingException
     */
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

    /**
     * 从网络资源获取邮件的正文内联引用part，并将获取到的part数据保存到本地数据库
     * @param localMessage 指定邮件
     * @param saveToLocal 是否将part内容保存到本地
     * @throws IOException
     * @throws MessagingException
     */
    public void refreshInlineParts(LocalMessage localMessage, boolean saveToLocal) throws IOException, MessagingException {
        List<LocalPart> inLineParts = partNetResource.getInlineParts(localMessage, saveToLocal);
        partLocalResource.saveOrUpdateParts(inLineParts);
    }

    /**
     * 获取指定邮件下指定位置的附件part
     * 先从本地资源获取，如果获取到了就直接返回
     * 否则再从网络资源获取，并将获取到的数据更新到本地资源
     * @param localMessage 指定邮件
     * @param index 附件part的位置序号
     * @param saveToLocal 是否将part内容保存到本地
     * @return 附件part
     * @throws IOException
     * @throws MessagingException
     */
    public LocalPart getAttachmentPart(LocalMessage localMessage, int index, boolean saveToLocal) throws IOException, MessagingException {
        LocalPart attachmentPart =  partLocalResource.getAttachPart(localMessage.getId(), index);
        if(attachmentPart == null){
            refreshAttachmentPart(localMessage, index, saveToLocal);
            attachmentPart =  partLocalResource.getAttachPart(localMessage.getId(), index);
        }
        attachmentPart.setLocalMessage(localMessage);
        return attachmentPart;
    }

    /**
     * 从网络资源获取指定邮件下指定位置的附件part，并将获取到的part数据保存到本地数据库
     * @param localMessage 指定邮件
     * @param index 附件part的位置序号
     * @param saveToLocal 是否将part内容保存到本地
     * @throws IOException
     * @throws MessagingException
     */
    public void refreshAttachmentPart(LocalMessage localMessage, int index, boolean saveToLocal) throws IOException, MessagingException {
        LocalPart localPart = partNetResource.getAttachmentPartByIndex(localMessage, index, saveToLocal);
        partLocalResource.saveOrUpdatePart(localPart);
    }
}
