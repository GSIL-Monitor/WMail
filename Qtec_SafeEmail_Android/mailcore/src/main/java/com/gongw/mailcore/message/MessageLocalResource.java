package com.gongw.mailcore.message;


import com.gongw.mailcore.contact.Contact;
import com.gongw.mailcore.contact.ContactModel;
import org.litepal.LitePal;
import java.util.ArrayList;
import java.util.List;

/**
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

    public LocalMessage getMessageById(long id){
        LocalMessage message = LitePal.find(LocalMessage.class, id);
        fillMessageContacts(message);
        return message;
    }

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

    public int getMsgCountByFolderId(long folderId, int limit, int offset){
        return LitePal.where("localfolder_id = ?", String.valueOf(folderId))
                .limit(limit)
                .offset(offset)
                .count(LocalMessage.class);
    }

    public void saveOrUpdateMessages(List<LocalMessage> localMessages){
        for(LocalMessage localMessage : localMessages){
            saveOrUpdateMessage(localMessage);
        }
    }

    public void saveOrUpdateMessage(LocalMessage localMessage){
        List<LocalMessage> localMessages = LitePal.where("localfolder_id = ? and uid = ?", String.valueOf(localMessage.getFolder().getId()), localMessage.getUid())
                .find(LocalMessage.class);
        if(localMessages.size() < 1){
            localMessage.save();
        }else{
            localMessage.update(localMessages.get(0).getId());
            localMessage.setId(localMessages.get(0).getId());
        }
//        localMessage.saveOrUpdate("localfolder_id = ? and uid = ?", String.valueOf(localMessage.getFolder().getId()), localMessage.getUid());

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

    public void deleteMessageById(long id){
        LitePal.delete(LocalMessage.class, id);
    }

    public void deleteMessagesByFolderId(long folderId){
        LitePal.deleteAll(LocalMessage.class, "localfolder_id = ?", String.valueOf(folderId));
    }

    public void deleteAllMessages(){
        LitePal.deleteAll(LocalMessage.class);
    }

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
