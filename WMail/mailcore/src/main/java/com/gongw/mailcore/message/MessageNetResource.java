package com.gongw.mailcore.message;

import com.gongw.mailcore.account.Account;
import com.gongw.mailcore.contact.Contact;
import com.gongw.mailcore.folder.LocalFolder;
import com.gongw.mailcore.net.NetResource;
import com.sun.mail.imap.IMAPFolder;
import com.sun.mail.imap.IMAPMessage;
import com.sun.mail.pop3.POP3Folder;
import com.sun.mail.pop3.POP3Message;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.mail.Address;
import javax.mail.FetchProfile;
import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.MethodNotSupportedException;
import javax.mail.Multipart;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;

/**
 * 网络上的邮件资源类，提供操作网络上的LocalMessage数据的接口
 * Created by gongw on 2018/7/17.
 */

public class MessageNetResource extends NetResource {

    private static class InstanceHolder{
        private static MessageNetResource instance = new MessageNetResource();
    }

    private MessageNetResource(){
    }

    public static MessageNetResource singleInstance(){
        return InstanceHolder.instance;
    }

    /**
     * 从邮箱服务器拉取指定文件夹下，指定位置的邮件
     * @param localFolder 指定的文件夹
     * @param pageIndex 指定的页序号
     * @return 符合条件的LocalMessage集合
     * @throws MessagingException
     * @throws IOException
     */
    public List<LocalMessage> fetchMessages(LocalFolder localFolder, int pageIndex, int pageSize) throws MessagingException, IOException {
        List<LocalMessage> localMessageList = new ArrayList<>();
        Folder folder = openFolder(localFolder, Folder.READ_ONLY);
        int msgCount = localFolder.getMsgCount();
        if(msgCount == 0){
            return localMessageList;
        }
        int start = msgCount - (pageIndex + 1) * pageSize + 1;
        start = start < 1 ? 1 : start;
        int end = start + pageSize - 1;
        end = end < 1 ? msgCount : end;

        Message[] messages = folder.getMessages(start, end);
        if(messages.length < 1){
            return localMessageList;
        }
        FetchProfile fetchProfile = new FetchProfile();
        fetchProfile.add(FetchProfile.Item.ENVELOPE);
        folder.fetch(messages, fetchProfile);
        for(Message message : messages) {
            LocalMessage localMessage = convertLocalMessage(localFolder, message, folder);
            localMessageList.add(localMessage);
        }
        return localMessageList;
    }

    /**
     * 批量修改邮件的标记
     * @param localMessages 邮件集合
     * @param flag 修改的标记
     * @param set 标记的目标值
     * @throws MessagingException
     */
    public void flagMessages(List<LocalMessage> localMessages, Flags.Flag flag, boolean set) throws MessagingException {
        if(localMessages == null || flag == null){
            return;
        }
        Map<String, List<LocalMessage>> messageMap = classifyMessagesByFolderUrl(localMessages);
        for(Iterator<List<LocalMessage>> iterator = messageMap.values().iterator(); iterator.hasNext();){
            List<LocalMessage> messages = iterator.next();
            int size = messages.size();
            long[] uids = new long[size];
            for(int i=0;i<size;i++){
                uids[i] = messages.get(i).getUid();
            }
            LocalFolder localFolder = messages.get(0).getFolder();
            Folder folder = openFolder(localFolder, Folder.READ_WRITE);
            if(folder instanceof IMAPFolder){
                IMAPFolder imapFolder = (IMAPFolder) folder;
                Message[] msgArray = imapFolder.getMessagesByUID(uids);
                imapFolder.setFlags(msgArray, new Flags(flag), set);
            }
        }

    }

    /**
     *  删除指定邮件
     * @param localMessages 指定的邮件集合
     * @throws MessagingException
     */
    public void deleteMessages(List<LocalMessage> localMessages) throws MessagingException {
        if(localMessages == null){
            return;
        }
        Map<String, List<LocalMessage>> messageMap = classifyMessagesByFolderUrl(localMessages);
        for(Iterator<List<LocalMessage>> iterator = messageMap.values().iterator(); iterator.hasNext();){
            List<LocalMessage> messages = iterator.next();
            int size = messages.size();
            long[] uids = new long[size];
            for(int i=0;i<size;i++){
                uids[i] = messages.get(i).getUid();
            }
            LocalFolder localFolder = messages.get(0).getFolder();
            Folder folder = openFolder(localFolder, Folder.READ_WRITE);
            if(folder instanceof IMAPFolder){
                IMAPFolder imapFolder = (IMAPFolder) folder;
                Message[] msgArray = imapFolder.getMessagesByUID(uids);
                imapFolder.setFlags(msgArray,  new Flags(Flags.Flag.DELETED), true);
            }
            folder.expunge();
        }
    }

    /**
     * 移动邮件到指定文件夹
     * @param localMessages 邮件集合
     * @param destFolder 目标文件夹
     * @throws MessagingException
     */
    public void moveMessages(List<LocalMessage> localMessages, LocalFolder destFolder) throws MessagingException {
        if(localMessages == null || destFolder == null){
            return;
        }
        Map<String, List<LocalMessage>> messageMap = classifyMessagesByFolderUrl(localMessages);
        for(Iterator<List<LocalMessage>> iterator = messageMap.values().iterator(); iterator.hasNext();){
            List<LocalMessage> messages = iterator.next();
            int size = messages.size();
            long[] uids = new long[size];
            for(int i=0;i<size;i++){
                uids[i] = messages.get(i).getUid();
            }
            LocalFolder localFolder = messages.get(0).getFolder();
            Folder folder = openFolder(localFolder, Folder.READ_WRITE);
            Folder dest = openFolder(destFolder, Folder.READ_WRITE);
            if(folder instanceof IMAPFolder && dest instanceof IMAPFolder){
                IMAPFolder imapFolder = (IMAPFolder) folder;
                Message[] msgArray = imapFolder.getMessagesByUID(uids);
                imapFolder.moveMessages(msgArray, dest);
            }else{
                throw new MethodNotSupportedException("getMessageByUID, moveMessages");
            }
        }
    }

    /**
     * 添加邮件到指定文件夹
     * @param destFolder 指定文件夹
     * @param messages 邮件
     * @throws MessagingException
     */
    public void appendMessage(LocalFolder destFolder, Message... messages) throws MessagingException {
        if(destFolder == null || messages == null){
            return;
        }
        Folder folder = openFolder(destFolder, Folder.READ_WRITE);
        folder.appendMessages(messages);
    }

    /**
     * 发送邮件
     * @param account 用于发送的账号
     * @param localMessage 要发送的邮件
     * @throws UnsupportedEncodingException
     * @throws MessagingException
     */
    public void sendMessage(Account account, LocalMessage localMessage) throws UnsupportedEncodingException, MessagingException {
        if(localMessage == null){
            return;
        }
        Transport transport = getTransport(account);
        Message message = new MessageBuilder().build(localMessage);
        transport.sendMessage(message, message.getAllRecipients());
    }

    /**
     * 按Folder Url划分,用于不同账号不同文件夹的邮件分类
     * @param localMessages LocalMessage集合
     * @return 划分好的Message Map, key为文件夹的url，value为对应的邮件集合
     */
    private Map<String, List<LocalMessage>> classifyMessagesByFolderUrl(List<LocalMessage> localMessages){
        Map<String, List<LocalMessage>> messageMap = new HashMap<>();
        for(LocalMessage localMessage : localMessages){
            String folderUrl = localMessage.getFolder().getUrl();
            if(!messageMap.containsKey(folderUrl)){
                List<LocalMessage> messages = new ArrayList<>();
                messageMap.put(folderUrl, messages);
            }
            messageMap.get(folderUrl).add(localMessage);
        }
        return messageMap;
    }

    /**
     * 将从网络获取的Message对象转换为LocalMessage对象
     * @param localFolder 邮件所在文件夹
     * @param message 从网络获取的Message邮件
     * @param folder 邮件所在的文件夹
     * @return LocalMessage对象
     * @throws MessagingException
     * @throws IOException
     */
    private LocalMessage convertLocalMessage(LocalFolder localFolder, Message message, Folder folder) throws MessagingException, IOException {
        LocalMessage localMessage = new LocalMessage();
        localMessage.setFolder(localFolder);

        if(folder instanceof IMAPFolder){
            IMAPFolder imapFolder = (IMAPFolder) folder;
            IMAPMessage imapMessage = (IMAPMessage) message;
            //UID
            localMessage.setUid(imapFolder.getUID(imapMessage));
            //MessageID
            localMessage.setMessageId(imapMessage.getMessageID());
            //Sender
            Address sender = imapMessage.getSender();
            if(sender != null){
                Contact contact = convertContact(sender);
                MessageContact messageContact = convertMessageContact(localMessage, contact, MessageContact.Type.SENDER);
                localMessage.setSender(messageContact);
            }
        }
        if(folder instanceof POP3Folder){
            POP3Folder pop3Folder = (POP3Folder) folder;
            POP3Message pop3Message = (POP3Message) message;
            //UID
            localMessage.setUid(Long.parseLong(pop3Folder.getUID(pop3Message)));
            //MessageID
            localMessage.setMessageId(pop3Message.getMessageID());
            //Sender
            Address sender = pop3Message.getSender();
            if(sender != null){
                Contact contact = convertContact(sender);
                MessageContact messageContact = convertMessageContact(localMessage, contact, MessageContact.Type.SENDER);
                localMessage.setSender(messageContact);
            }
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
        localMessage.setAnswered(message.isSet(Flags.Flag.ANSWERED));
        localMessage.setDeleted(message.isSet(Flags.Flag.DELETED));
        localMessage.setDraft(message.isSet(Flags.Flag.DRAFT));
        localMessage.setFlagged(message.isSet(Flags.Flag.FLAGGED));
        localMessage.setRecent(message.isSet(Flags.Flag.RECENT));
        localMessage.setSeen(message.isSet(Flags.Flag.SEEN));
        localMessage.setUser(message.isSet(Flags.Flag.USER));

        //设置发件人
        Address[] from = message.getFrom();
        if(from!=null && from.length > 0){
            List<MessageContact> froms = new ArrayList<>(from.length);
            localMessage.setFrom(froms);
            for(Address address : from){
                Contact contact = convertContact(address);
                MessageContact messageContact = convertMessageContact(localMessage, contact, MessageContact.Type.FROM);
                froms.add(messageContact);
            }
        }

        //设置各种收件人，抄送，密送
        Address[] to = message.getRecipients(Message.RecipientType.TO);
        if(to!=null && to.length > 0){
            List<MessageContact> recipientTo = new ArrayList<>(to.length);
            localMessage.setRecipientsTo(recipientTo);
            for(Address address : to){
                Contact contact = convertContact(address);
                MessageContact messageContact = convertMessageContact(localMessage, contact, MessageContact.Type.TO);
                recipientTo.add(messageContact);
            }
        }

        Address[] cc = message.getRecipients(Message.RecipientType.CC);
        if(cc!=null && cc.length > 0){
            List<MessageContact> recipientCc = new ArrayList<>(cc.length);
            localMessage.setRecipientsCc(recipientCc);
            for(Address address : cc){
                Contact contact = convertContact(address);
                MessageContact messageContact = convertMessageContact(localMessage, contact, MessageContact.Type.CC);
                recipientCc.add(messageContact);
            }
        }

        Address[] bcc = message.getRecipients(Message.RecipientType.BCC);
        if(bcc!=null && bcc.length>0){
            List<MessageContact> recipientBcc = new ArrayList<>(bcc.length);
            localMessage.setRecipientsBcc(recipientBcc);
            for(Address address : bcc){
                Contact contact = convertContact(address);
                MessageContact messageContact = convertMessageContact(localMessage, contact, MessageContact.Type.BCC);
                recipientBcc.add(messageContact);
            }
        }

        //设置回复人
        Address[] reply = message.getReplyTo();
        if(reply!=null && reply.length > 0){
            List<MessageContact> replyTos = new ArrayList<>(reply.length);
            localMessage.setReplyTo(replyTos);
            for(Address address : reply){
                Contact contact = convertContact(address);
                MessageContact messageContact = convertMessageContact(localMessage, contact, MessageContact.Type.REPLY);
                replyTos.add(messageContact);
            }
        }
        return localMessage;
    }

    /**
     * 将Address对象转换为Contact对象
     * @param address 需要转换的Address对象
     * @return Contact对象
     */
    private Contact convertContact(Address address){
        InternetAddress internetAddress = (InternetAddress)address;
        String email = internetAddress.getAddress();
        String personalName = internetAddress.getPersonal();
        Contact contact = new Contact();
        contact.setEmail(email!=null ? email : "");
        contact.setPersonalName(personalName!=null ? personalName : "");
        return contact;
    }

    /**
     * 用Contact和LocalMessage生成MessageContact对象
     * @param localMessage LocalMessage对象
     * @param contact Contact对象
     * @param type Contact对象在LocalMessage对象中的类型
     * @return MessageContact对象
     */
    private MessageContact convertMessageContact(LocalMessage localMessage, Contact contact, String type){
        MessageContact messageContact = new MessageContact();
        messageContact.setLocalMessage(localMessage);
        messageContact.setContact(contact);
        messageContact.setType(type);
        return messageContact;
    }

}
