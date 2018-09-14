package com.gongw.mailcore.message;

import com.gongw.mailcore.IMAPFetcher;
import com.gongw.mailcore.POPFetcher;
import com.gongw.mailcore.account.Account;
import com.gongw.mailcore.contact.Contact;
import com.gongw.mailcore.folder.LocalFolder;
import com.sun.mail.imap.IMAPMessage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.mail.Address;
import javax.mail.Flags;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.URLName;
import javax.mail.internet.InternetAddress;

/**
 * Created by gongw on 2018/7/17.
 */

public class MessageNetResource {

    private Map<String, IMAPFetcher> imapFetcherMap = new HashMap<>();
    private Map<String, POPFetcher> popFetcherMap = new HashMap<>();


    public List<LocalMessage> fetchMessages(Account account, LocalFolder localFolder, int start, int end) throws MessagingException {
        List<LocalMessage> localMessages = new ArrayList<>();
        if(account.getStoreUrl().startsWith("imap")){
            if(!imapFetcherMap.containsKey(account.getStoreUrl())){
                IMAPFetcher imapFetcher = new IMAPFetcher(new URLName(account.getStoreUrl()));
                imapFetcherMap.put(account.getStoreUrl(), imapFetcher);
            }
            IMAPFetcher imapFetcher  = imapFetcherMap.get(account.getStoreUrl());
            List<IMAPMessage> messageList = Arrays.asList(imapFetcher.fetchMessages(localFolder.getFullName(), start, end));
            //设置邮件标记
            for(IMAPMessage message : messageList){
                LocalMessage localMessage = new LocalMessage();
                localMessage.setFolder(localFolder);
                localMessage.setSubject(message.getSubject());
                localMessage.setSentDate(message.getSentDate());
                localMessage.setReceiveDate(message.getReceivedDate());
                localMessage.setSize(message.getSize());
//                localMessage.setUid(message.header());
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

                localMessages.add(localMessage);
            }
        }else if(account.getStoreUrl().startsWith("pop")){
            if(!popFetcherMap.containsKey(account.getStoreUrl())){
//                POPFetcher popFetcher = new POPFetcher(new URLName(account.getStoreUrl()));
//                popFetcherMap.put(account.getStoreUrl(), popFetcher);
            }
            POPFetcher popFetcher  = popFetcherMap.get(account.getStoreUrl());


        }

        return localMessages;
    }

}
