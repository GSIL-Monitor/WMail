package com.gongw.mailcore.message;

import com.gongw.mailcore.contact.Contact;
import com.gongw.mailcore.folder.LocalFolder;
import com.gongw.mailcore.part.LocalPart;

import org.litepal.annotation.Column;
import org.litepal.crud.LitePalSupport;
import java.util.Date;
import java.util.List;

/**
 * Created by gongw on 2018/7/22.
 */

public class LocalMessage extends LitePalSupport {

    private long id;

    @Column(nullable = false)
    private LocalFolder folder;

    private boolean expunged;

    private Date receiveDate;

    private Date sentDate;

    private String subject;

    private int flags;

    private List<MessageContact> recipientsTo;

    private List<MessageContact> recipientsCc;

    private List<MessageContact> recipientsBcc;

    private List<MessageContact> from;

    private List<MessageContact> replyTo;

    private MessageContact sender;

    @Column(unique = true)
    private String messageId;

    /**
     * 邮件回复标记，标识邮件是否已回复
     */
    private boolean answered;

    /**
     * 邮件删除标记，标识邮件是否需要删除
     */
    private boolean deleted;

    /**
     * 草稿邮件标记，标识邮件是否为草稿
     */
    private boolean draft;

    /**
     * 邮件标记，一般指标星
     */
    private boolean flagged;

    /**
     * 新邮件标记，表示邮件是否为新邮件
     */
    private boolean recent;

    /**
     * 邮件阅读标记，标识邮件是否已被阅读
     */
    private boolean seen;

    /**
     * 系统是否支持用户自定义标记，只能检索，不能设置这个属性
     */
    private boolean user;

    private List<LocalPart> partList;

    private int attachmentCount;

    private String uid;

    private long size;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public LocalFolder getFolder() {
        return folder;
    }

    public void setFolder(LocalFolder folder) {
        this.folder = folder;
    }

    public boolean isExpunged() {
        return expunged;
    }

    public void setExpunged(boolean expunged) {
        this.expunged = expunged;
    }

    public Date getReceiveDate() {
        return receiveDate;
    }

    public void setReceiveDate(Date receiveDate) {
        this.receiveDate = receiveDate;
    }

    public Date getSentDate() {
        return sentDate;
    }

    public void setSentDate(Date sentDate) {
        this.sentDate = sentDate;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public int getFlags() {
        return flags;
    }

    public void setFlags(int flags) {
        this.flags = flags;
    }

    public MessageContact getSender() {
        return sender;
    }

    public void setSender(MessageContact sender) {
        this.sender = sender;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public List<LocalPart> getPartList() {
        return partList;
    }

    public void setPartList(List<LocalPart> partList) {
        this.partList = partList;
    }

    public int getAttachmentCount() {
        return attachmentCount;
    }

    public void setAttachmentCount(int attachmentCount) {
        this.attachmentCount = attachmentCount;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public boolean isAnswered() {
        return answered;
    }

    public void setAnswered(boolean answered) {
        this.answered = answered;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public boolean isDraft() {
        return draft;
    }

    public void setDraft(boolean draft) {
        this.draft = draft;
    }

    public boolean isFlagged() {
        return flagged;
    }

    public void setFlagged(boolean flagged) {
        this.flagged = flagged;
    }

    public boolean isRecent() {
        return recent;
    }

    public void setRecent(boolean recent) {
        this.recent = recent;
    }

    public boolean isSeen() {
        return seen;
    }

    public void setSeen(boolean seen) {
        this.seen = seen;
    }

    public boolean isUser() {
        return user;
    }

    public void setUser(boolean user) {
        this.user = user;
    }

    public LocalPart getHtmlContentPart() {
        if(partList == null){
            return null;
        }
        for(LocalPart part : partList){
            if(LocalPart.Type.HTML_CONTENT.equals(part.getType())){
                return part;
            }
        }
        return null;
    }

    public LocalPart getTextContentPart() {
        if(partList == null){
            return null;
        }
        for(LocalPart part : partList){
            if(LocalPart.Type.TEXT_CONTENT.equals(part.getType())){
                return part;
            }
        }
        return null;
    }

    public List<MessageContact> getRecipientsTo() {
        return recipientsTo;
    }

    public void setRecipientsTo(List<MessageContact> recipientsTo) {
        this.recipientsTo = recipientsTo;
    }

    public List<MessageContact> getRecipientsCc() {
        return recipientsCc;
    }

    public void setRecipientsCc(List<MessageContact> recipientsCc) {
        this.recipientsCc = recipientsCc;
    }

    public List<MessageContact> getRecipientsBcc() {
        return recipientsBcc;
    }

    public void setRecipientsBcc(List<MessageContact> recipientsBcc) {
        this.recipientsBcc = recipientsBcc;
    }

    public List<MessageContact> getFrom() {
        return from;
    }

    public void setFrom(List<MessageContact> from) {
        this.from = from;
    }

    public List<MessageContact> getReplyTo() {
        return replyTo;
    }

    public void setReplyTo(List<MessageContact> replyTo) {
        this.replyTo = replyTo;
    }
}
