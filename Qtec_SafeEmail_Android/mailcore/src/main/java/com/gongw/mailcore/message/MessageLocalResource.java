package com.gongw.mailcore.message;


import com.gongw.mailcore.contact.Contact;
import com.gongw.mailcore.contact.ContactModel;
import com.gongw.mailcore.folder.LocalFolder;
import org.litepal.LitePal;
import java.util.ArrayList;
import java.util.List;
import javax.mail.Flags;

/**
 * 本地的邮件资源，以数据库的形式保存LocalMessage，并提供操作数据库中LocalMessage数据的接口
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

    /**
     * 获取指定id的邮件
     * @param id 指定的id
     * @return LocalMessage
     */
    public LocalMessage getMessageById(long id){
        LocalMessage message = LitePal.find(LocalMessage.class, id);
        fillMessageContacts(message);
        return message;
    }

    /**
     * 获取指定文件夹下的邮件
     * @param folderId 文件夹id
     * @param limit 获取的邮件数量
     * @param offset 从指定位置开始获取
     * @return LocalMessage集合
     */
    public List<LocalMessage> getMessagesByFolderId(long folderId, int limit, int offset){
        List<LocalMessage> messages = LitePal.where("localfolder_id = ?", String.valueOf(folderId))
                .limit(limit)
                .offset(offset)
                .order("receiveDate desc")
                .find(LocalMessage.class);

        for(LocalMessage localMessage : messages){
            fillMessageContacts(localMessage);
        }
        return messages;
    }

    /**
     * 批量保存或修改邮件到数据库
     * @param localMessages LocalMessage集合
     */
    public void saveOrUpdateMessages(List<LocalMessage> localMessages){
        for(LocalMessage localMessage : localMessages){
            saveOrUpdateMessage(localMessage);
        }
    }

    /**
     * 保存或修改邮件
     * 如果邮件的文件夹id和uid都已存在，则为修改操作，否则保存
     * 保存邮件时，将邮件中包含的联系人，以及联系人在邮件中的类型信息也保存到对应表
     * @param localMessage LocalMessage
     */
    public void saveOrUpdateMessage(LocalMessage localMessage){
        List<LocalMessage> localMessages = LitePal.where("localfolder_id = ? and uid = ?", String.valueOf(localMessage.getFolder().getId()), String.valueOf(localMessage.getUid()))
                .find(LocalMessage.class);
        if(localMessages.size() < 1){
            localMessage.save();
        }else{
            localMessage.update(localMessages.get(0).getId());
            localMessage.setId(localMessages.get(0).getId());
        }

        List<MessageContact> allMessageContacts = new ArrayList<>();
        if(localMessage.getSender()!=null){
            allMessageContacts.add(localMessage.getSender());
        }
        if(localMessage.getFrom()!=null){
            allMessageContacts.addAll(localMessage.getFrom());
        }
        if(localMessage.getRecipientsTo()!=null){
            allMessageContacts.addAll(localMessage.getRecipientsTo());
        }
        if(localMessage.getRecipientsCc()!=null){
            allMessageContacts.addAll(localMessage.getRecipientsCc());
        }
        if(localMessage.getRecipientsBcc()!=null){
            allMessageContacts.addAll(localMessage.getRecipientsBcc());
        }
        if(localMessage.getReplyTo()!=null){
            allMessageContacts.addAll(localMessage.getReplyTo());
        }
        for(MessageContact messageContact : allMessageContacts){
            Contact contact = messageContact.getContact();

            ContactModel.singleInstance().saveOrUpdateContact(contact);
            contact = ContactModel.singleInstance().getContactById(contact.getId());
            messageContact.setContact(contact);

            List<MessageContact> messageContacts = LitePal.where("localmessage_id = ? and contact_id = ? and type = ?",
                                                    localMessage.getId()+"",
                                                    contact.getId()+"",
                                                    messageContact.getType())
                                                    .find(MessageContact.class);
            if(messageContacts.size() < 1){
                messageContact.save();
            }else{
                messageContact.setId(messageContacts.get(0).getId());
            }
        }
    }

    /**
     * 删除指定id的LocalMessage
     * @param id id
     */
    public void deleteMessageById(long id){
        LitePal.delete(LocalMessage.class, id);
    }

    /**
     * 删除指定文件夹的邮件
     * @param folderId 文件夹id
     */
    public void deleteMessagesByFolderId(long folderId){
        LitePal.deleteAll(LocalMessage.class, "localfolder_id = ?", String.valueOf(folderId));
    }

    /**
     * 删除数据库中的所有邮件
     */
    public void deleteAllMessages(){
        LitePal.deleteAll(LocalMessage.class);
    }

    /**
     * 批量修改邮件中的标记，并保存或更新
     * @param localMessages localmessage集合
     * @param flag 需要修改的标记，通常为Flags.Flag下的标记
     * @param set 修改标记的目标值
     */
    public void flagMessage(List<LocalMessage> localMessages, Flags.Flag flag, boolean set){
        for(LocalMessage localMessage : localMessages){
            if(flag == Flags.Flag.DRAFT){
                localMessage.setDraft(set);
            }
            if(flag == Flags.Flag.ANSWERED){
                localMessage.setAnswered(set);
            }
            if(flag == Flags.Flag.USER){
                localMessage.setUser(set);
            }
            if(flag == Flags.Flag.SEEN){
                localMessage.setSeen(set);
            }
            if(flag == Flags.Flag.RECENT){
                localMessage.setRecent(set);
            }
            if(flag == Flags.Flag.FLAGGED){
                localMessage.setFlagged(set);
            }
            if(flag == Flags.Flag.DELETED){
                localMessage.setDeleted(set);
            }
            localMessage.update(localMessage.getId());
        }

    }

    /**
     * 批量删除邮件
     * @param localMessages 要删除的LocalMessage集合
     */
    public void deleteMessages(List<LocalMessage> localMessages) {
        for(LocalMessage localMessage : localMessages){
            LitePal.delete(LocalMessage.class, localMessage.getId());
        }
    }

    /**
     * 批量移动邮件
     * @param localMessages 需要移动的LocalMessage集合
     * @param destFolder 移动到的目标文件夹
     */
    public void moveMessages(List<LocalMessage> localMessages, LocalFolder destFolder){
        if(destFolder == null || localMessages == null){
            return;
        }
        for(LocalMessage localMessage : localMessages){
            localMessage.setFolder(destFolder);
            localMessage.update(localMessage.getId());
        }
    }

    /**
     * 将邮件添加到指定文件夹
     * @param destFolder 目标文件夹
     * @param message 邮件
     */
    public void appendMessage(LocalFolder destFolder, LocalMessage message) {
        message.setFolder(destFolder);
        saveOrUpdateMessage(message);
    }

    /**
     * 填充邮件的各个联系人，一般在从数据库取出邮件时调用
     * @param localMessage 需要填充的邮件
     */
    private void fillMessageContacts(LocalMessage localMessage){
        List<MessageContact> messageContacts = LitePal.where("localmessage_id = ?", String.valueOf(localMessage.getId()))
                .find(MessageContact.class, true);
        List<MessageContact> from = new ArrayList<>();
        List<MessageContact> reply = new ArrayList<>();
        List<MessageContact> to = new ArrayList<>();
        List<MessageContact> cc = new ArrayList<>();
        List<MessageContact> bcc = new ArrayList<>();

        localMessage.setFrom(from);
        localMessage.setRecipientsTo(to);
        localMessage.setRecipientsCc(cc);
        localMessage.setRecipientsBcc(bcc);
        localMessage.setReplyTo(reply);
        for(MessageContact messageContact : messageContacts){
            switch (messageContact.getType()){
                case MessageContact.Type.FROM:
                    from.add(messageContact);
                    break;
                case MessageContact.Type.TO:
                    to.add(messageContact);
                    break;
                case MessageContact.Type.CC:
                    cc.add(messageContact);
                    break;
                case MessageContact.Type.BCC:
                    bcc.add(messageContact);
                    break;
                case MessageContact.Type.REPLY:
                    reply.add(messageContact);
                    break;
                case MessageContact.Type.SENDER:
                    localMessage.setSender(messageContact);
                    break;
            }


        }

    }

}
