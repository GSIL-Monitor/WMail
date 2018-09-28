package com.gongw.mailcore.message;

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
    /**
     * 该邮件属于的文件夹
     */
    @Column(nullable = false)
    private LocalFolder folder;
    /**
     * 该邮件是否已经被清除了
     */
    private boolean expunged;
    /**
     * 邮件接收日期
     */
    private Date receiveDate;
    /**
     * 邮件发送日期
     */
    private Date sentDate;
    /**
     * 邮件主题
     */
    private String subject;
    /**
     * 邮件的收件人集合
     */
    private List<MessageContact> recipientsTo;
    /**
     * 邮件的抄送人集合
     */
    private List<MessageContact> recipientsCc;
    /**
     * 邮件的密送人集合
     */
    private List<MessageContact> recipientsBcc;
    /**
     * 邮件的来源人
     */
    private List<MessageContact> from;
    /**
     * 邮件的回复人
     */
    private List<MessageContact> replyTo;
    /**
     * 邮件的发送人
     */
    private MessageContact sender;
    /**
     * 邮件的messageId
     */
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
    /**
     * 邮件的part集合，不包含multipart/*类型
     */
    private List<LocalPart> partList;
    /**
     * 邮件的附件数
     */
    private int attachmentCount;
    /**
     * 邮件的uid，在文件夹中是唯一的
     */
    private long uid;
    /**
     * 邮件大小
     */
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

    public long getUid() {
        return uid;
    }

    public void setUid(long uid) {
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
