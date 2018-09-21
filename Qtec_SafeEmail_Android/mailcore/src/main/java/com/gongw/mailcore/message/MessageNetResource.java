package com.gongw.mailcore.message;

import com.gongw.mailcore.MailFetcher;
import com.gongw.mailcore.NetResource;
import com.gongw.mailcore.contact.Contact;
import com.gongw.mailcore.contact.ContactModel;
import com.gongw.mailcore.folder.LocalFolder;
import com.sun.mail.imap.IMAPFolder;
import com.sun.mail.imap.IMAPMessage;
import com.sun.mail.pop3.POP3Folder;
import com.sun.mail.pop3.POP3Message;

import org.litepal.LitePal;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.mail.Address;
import javax.mail.FetchProfile;
import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.internet.InternetAddress;

/**
 * Created by gongw on 2018/7/17.
 */

public class MessageNetResource extends NetResource{

    private static class InstanceHolder{
        private static MessageNetResource instance = new MessageNetResource();
    }

    private MessageNetResource(){
    }

    public static MessageNetResource singleInstance(){
        return InstanceHolder.instance;
    }


    public List<LocalMessage> fetchMessages(LocalFolder localFolder, int start, int end) throws MessagingException, IOException {
        List<LocalMessage> localMessageList = new ArrayList<>();
        MailFetcher fetcher = getFetcher(localFolder.getAccount());
        Message[] messages = fetcher.fetchMessages(localFolder.getFullName(), start, end);
        if(messages.length < 1){
            return localMessageList;
        }
        Folder folder = messages[0].getFolder();
        FetchProfile fetchProfile = new FetchProfile();
        fetchProfile.add(FetchProfile.Item.ENVELOPE);
        folder.fetch(messages, fetchProfile);

        for(Message message : messages){
            LocalMessage localMessage = new LocalMessage();
            localMessage.setFolder(localFolder);

            if(folder instanceof IMAPFolder){
                IMAPFolder imapFolder = (IMAPFolder) folder;
                IMAPMessage imapMessage = (IMAPMessage) message;
                //UID
                localMessage.setUid(String.valueOf(imapFolder.getUID(imapMessage)));
                //MessageID
                localMessage.setMessageId(imapMessage.getMessageID());
                //Sender
                Contact contact = convertContact(imapMessage.getSender());
                MessageContact messageContact = convertMessageContact(localMessage, contact, MessageContact.Type.SENDER);
                localMessage.setSender(messageContact);
            }
            if(folder instanceof POP3Folder){
                POP3Folder pop3Folder = (POP3Folder) folder;
                POP3Message pop3Message = (POP3Message) message;
                //UID
                localMessage.setUid(String.valueOf(pop3Folder.getUID(pop3Message)));
                //MessageID
                localMessage.setMessageId(pop3Message.getMessageID());
                //Sender
                Contact contact = convertContact(pop3Message.getSender());
                MessageContact messageContact = convertMessageContact(localMessage, contact, MessageContact.Type.SENDER);
                localMessage.setSender(messageContact);
            }
            localMessage.setSubject(message.getSubject());
            localMessage.setSentDate(message.getSentDate());
            localMessage.setReceiveDate(message.getReceivedDate());
            localMessage.setSize(message.getSize());
            if(message.getContentType().startsWith("multipart/mixed") ){
                //设置附件数量
                localMessage.setAttachmentCount(((Multipart)message.getContent()).getCount() - 1);
            }
            //设置flag
            for(Flags.Flag flag : message.getFlags().getSystemFlags()){
                if(flag == Flags.Flag.ANSWERED){
                    localMessage.setAnswered(true);
                }
                else if(flag == Flags.Flag.DELETED){
                    localMessage.setDeleted(true);
                }
                else if(flag == Flags.Flag.DRAFT){
                    localMessage.setDraft(true);
                }
                else if(flag == Flags.Flag.FLAGGED){
                    localMessage.setFlagged(true);
                }
                else if(flag == Flags.Flag.RECENT){
                    localMessage.setRecent(true);
                }
                else if(flag == Flags.Flag.SEEN){
                    localMessage.setSeen(true);
                }
                else if(flag == Flags.Flag.USER){
                    localMessage.setUser(true);
                }
            }

            //设置发件人
            List<MessageContact> froms = new ArrayList<>();
            localMessage.setFrom(froms);
            for(Address address : message.getFrom()){
                Contact contact = convertContact(address);
                MessageContact messageContact = convertMessageContact(localMessage, contact, MessageContact.Type.FROM);
                froms.add(messageContact);
            }
            //设置各种收件人，抄送，密送
            List<MessageContact> recipientTo = new ArrayList<>();
            localMessage.setRecipientsTo(recipientTo);
            List<MessageContact> recipientCc = new ArrayList<>();
            localMessage.setRecipientsCc(recipientCc);
            List<MessageContact> recipientBcc = new ArrayList<>();
            localMessage.setRecipientsBcc(recipientBcc);

            for(Address address : message.getAllRecipients()){
                Contact contact = convertContact(address);
                if(Message.RecipientType.TO.toString().equals(address.getType())){
                    MessageContact messageContact = convertMessageContact(localMessage, contact, MessageContact.Type.TO);
                    recipientTo.add(messageContact);
                }
                if(Message.RecipientType.CC.toString().equals(address.getType())){
                    MessageContact messageContact = convertMessageContact(localMessage, contact, MessageContact.Type.CC);
                    recipientTo.add(messageContact);
                }
                if(Message.RecipientType.BCC.toString().equals(address.getType())){
                    MessageContact messageContact = convertMessageContact(localMessage, contact, MessageContact.Type.BCC);
                    recipientTo.add(messageContact);
                }
            }

            //设置回复人
            List<MessageContact> replyTos = new ArrayList<>();
            localMessage.setReplyTo(replyTos);
            for(Address address : message.getReplyTo()){
                Contact contact = convertContact(address);
                MessageContact messageContact = convertMessageContact(localMessage, contact, MessageContact.Type.REPLY);
                replyTos.add(messageContact);
            }

            localMessageList.add(localMessage);
        }

        return localMessageList;
    }

    private Contact convertContact(Address address){
        InternetAddress internetAddress = (InternetAddress)address;
        String email = internetAddress.getAddress();
        String personalName = internetAddress.getPersonal();
        Contact contact = new Contact();
        contact.setEmail(email);
        contact.setPersonalName(personalName);
        ContactModel.singleInstance().saveOrUpdateContact(contact);
        return ContactModel.singleInstance().getContact(email, personalName);
    }

    private MessageContact convertMessageContact(LocalMessage localMessage, Contact contact, String type){
        MessageContact messageContact = new MessageContact();
        messageContact.setLocalMessage(localMessage);
        messageContact.setContact(contact);
        messageContact.setType(type);
        messageContact.saveOrUpdate("localmessage_id = ? and contact_id = ? and type = ?",
                                    String.valueOf(localMessage.getId()),
                                    String.valueOf(localMessage.getId()),
                                    String.valueOf(localMessage.getId()));
        return LitePal.where("localmessage_id = ? and contact_id = ? and type = ?",
                String.valueOf(localMessage.getId()),
                String.valueOf(localMessage.getId()),
                String.valueOf(localMessage.getId()))
                .find(MessageContact.class).get(0);
    }

}
