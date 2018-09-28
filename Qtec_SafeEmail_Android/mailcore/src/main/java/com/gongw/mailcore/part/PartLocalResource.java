package com.gongw.mailcore.part;

import org.litepal.LitePal;
import java.util.List;

/**
 * 本地的邮件Part资源，以数据库的形式保存LocalPart，并提供操作数据库中LocalPart数据的接口
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

    /**
     * 获取指定邮件的所有Part
     * @param localMessage_id 邮件id
     * @return LocalPart集合
     */
    public List<LocalPart> getParts(long localMessage_id){
        return LitePal.where("localmessage_id = ?", String.valueOf(localMessage_id))
                .find(LocalPart.class);
    }

    /**
     * 获取邮件中附件类型的part
     * @param localMessage_id 邮件id
     * @param index 附件类型part的位置序号
     * @return LocalPart
     */
    public LocalPart getAttachPart(long localMessage_id, long index){
        List<LocalPart> parts = LitePal.where("localmessage_id = ? and type = ? and index = ?", String.valueOf(localMessage_id), LocalPart.Type.ATTACHMENT, String.valueOf(index))
                .find(LocalPart.class);
        return parts.size()>0 ? parts.get(0) : null;
    }

    /**
     * 获取HTML/TEXT类型的正文part
     * @param localMessage_id 邮件id
     * @return LocalPart
     */
    public List<LocalPart> getHtmlContentParts(long localMessage_id){
        return LitePal.where("localmessage_id = ? and type = ?", String.valueOf(localMessage_id), LocalPart.Type.HTML_CONTENT)
                .find(LocalPart.class);
    }

    /**
     * 获取TEXT/PLAIN类型的正文part
     * @param localMessage_id 邮件id
     * @return LocalPart
     */
    public List<LocalPart> getTextContentParts(long localMessage_id){
        return LitePal.where("localmessage_id = ? and type = ?", String.valueOf(localMessage_id), LocalPart.Type.TEXT_CONTENT)
                .find(LocalPart.class);
    }

    /**
     * 获取INLINE类型的part
     * @param localMessage_id 邮件id
     * @return LocalPart
     */
    public List<LocalPart> getInLineParts(long localMessage_id){
        return LitePal.where("localmessage_id = ? and type = ?", String.valueOf(localMessage_id), LocalPart.Type.INLINE)
                .find(LocalPart.class);
    }

    /**
     * 批量保存或修改邮件part
     * @param localParts LocalPart集合
     */
    public void saveOrUpdateParts(List<LocalPart> localParts){
        for(LocalPart part : localParts){
            saveOrUpdatePart(part);
        }
    }

    /**
     * 保存或修改part
     * 如果已经存在localmessage_id,fileName,contentType都相同的part，
     * 则做修改操作，否则为保存操作
     * @param localPart LocalPart
     */
    public void saveOrUpdatePart(LocalPart localPart){
        localPart.saveOrUpdate("localmessage_id = ? and fileName = ? and contentType = ?", String.valueOf(localPart.getLocalMessage().getId()),
                                                                                                        String.valueOf(localPart.getFileName()),
                                                                                                        localPart.getContentType());
    }

}
