package com.gongw.mailcore.message;

import com.gongw.mailcore.MailFetcher;
import com.gongw.mailcore.NetResource;
import com.gongw.mailcore.contact.Contact;
import com.gongw.mailcore.folder.LocalFolder;
import com.sun.mail.imap.IMAPFolder;
import com.sun.mail.pop3.POP3Folder;

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
        folder.fetch(messages, fetchProfile);

        for(Message message : messages){
            LocalMessage localMessage = new LocalMessage();
            localMessage.setFolder(localFolder);

            if(folder instanceof IMAPFolder){
                IMAPFolder imapFolder = (IMAPFolder) folder;
                localMessage.setUid(String.valueOf(imapFolder.getUID(message)));
            }
            if(folder instanceof POP3Folder){
                POP3Folder pop3Folder = (POP3Folder) folder;
                localMessage.setUid(String.valueOf(pop3Folder.getUID(message)));
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
                if(flag == Flags.Flag.DELETED){
                    localMessage.setDeleted(true);
                }
                if(flag == Flags.Flag.DRAFT){
                    localMessage.setDraft(true);
                }
                if(flag == Flags.Flag.FLAGGED){
                    localMessage.setFlagged(true);
                }
                if(flag == Flags.Flag.RECENT){
                    localMessage.setRecent(true);
                }
                if(flag == Flags.Flag.SEEN){
                    localMessage.setSeen(true);
                }
                if(flag == Flags.Flag.USER){
                    localMessage.setUser(true);
                }
            }
            //设置发件人
            List<Contact> froms = new ArrayList<>();
            localMessage.setFrom(froms);
            for(Address address : message.getFrom()){
                InternetAddress internetAddress = (InternetAddress)address;
                Contact contact = new Contact();
                contact.setEmail(internetAddress.getAddress());
                contact.setPersonalName(internetAddress.getPersonal());
                froms.add(contact);
            }
            //设置收件人、抄送人、密送人
            List<Contact> recipientTo = new ArrayList<>();
            List<Contact> recipientCc = new ArrayList<>();
            List<Contact> recipientBcc = new ArrayList<>();
            localMessage.setRecipientsTo(recipientTo);
            localMessage.setRecipientsCc(recipientCc);
            localMessage.setRecipientsBcc(recipientBcc);
            for(Address address : message.getAllRecipients()){
                InternetAddress internetAddress = (InternetAddress)address;
                Contact contact = new Contact();
                contact.setEmail(internetAddress.getAddress());
                contact.setPersonalName(internetAddress.getPersonal());
                if(internetAddress.getType().equals(Message.RecipientType.TO)){
                    recipientTo.add(contact);
                }
                if(internetAddress.getType().equals(Message.RecipientType.CC)){
                    recipientCc.add(contact);
                }
                if(internetAddress.getType().equals(Message.RecipientType.BCC)){
                    recipientBcc.add(contact);
                }
            }
            //设置回复人
            List<Contact> replyTos = new ArrayList<>();
            localMessage.setReplyTo(replyTos);
            for(Address address : message.getReplyTo()){
                InternetAddress internetAddress = (InternetAddress)address;
                Contact contact = new Contact();
                contact.setEmail(internetAddress.getAddress());
                contact.setPersonalName(internetAddress.getPersonal());
                replyTos.add(contact);
            }

            localMessageList.add(localMessage);
        }

        return localMessageList;
    }

}
