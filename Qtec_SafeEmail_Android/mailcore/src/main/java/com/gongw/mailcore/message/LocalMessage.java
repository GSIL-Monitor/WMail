package com.gongw.mailcore.message;

import com.libmailcore.IMAPMessage;

import org.litepal.crud.LitePalSupport;

import java.util.List;

/**
 * Created by gongw on 2018/7/22.
 */

public class LocalMessage extends LitePalSupport {

    private long sequenceNumber;

    private long uid;

    private long size;

    private int flags;

    private int originalFlags;

    private List<String> customFlags;

    private long modSeqValue;

    private LocalPart mainPart;

    private List<String> gmailLabels;

    private long gmailMessageID;

    private long gmailThreadID;

    public LocalMessage(IMAPMessage imapMessage){
        this.sequenceNumber = imapMessage.sequenceNumber();
        this.uid = imapMessage.uid();
        this.size = imapMessage.size();
        this.flags = imapMessage.flags();
        this.originalFlags = imapMessage.originalFlags();
        this.customFlags = imapMessage.customFlags();
        this.modSeqValue = imapMessage.modSeqValue();
        this.mainPart = new LocalPart(imapMessage.mainPart());
        this.gmailLabels = imapMessage.gmailLabels();
        this.gmailMessageID = imapMessage.gmailMessageID();
        this.gmailThreadID = imapMessage.gmailThreadID();
    }

    public IMAPMessage toImapMessage(){
        IMAPMessage imapMessage = new IMAPMessage();
        imapMessage.setSequenceNumber(sequenceNumber);
        imapMessage.setUid(uid);
        imapMessage.setSize(size);
        imapMessage.setFlags(flags);
        imapMessage.setOriginalFlags(originalFlags);
        imapMessage.setCustomFlags(customFlags);
        imapMessage.setModSeqValue(modSeqValue);
        imapMessage.setMainPart(mainPart.toAbstractPart());
        imapMessage.setGmailLabels(gmailLabels);
        imapMessage.setGmailMessageID(gmailMessageID);
        imapMessage.setGmailThreadID(gmailThreadID);
        return imapMessage;
    }

    public LocalMessage(){}

    public long getSequenceNumber() {
        return sequenceNumber;
    }

    public void setSequenceNumber(long sequenceNumber) {
        this.sequenceNumber = sequenceNumber;
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

    public int getFlags() {
        return flags;
    }

    public void setFlags(int flags) {
        this.flags = flags;
    }

    public int getOriginalFlags() {
        return originalFlags;
    }

    public void setOriginalFlags(int originalFlags) {
        this.originalFlags = originalFlags;
    }

    public List<String> getCustomFlags() {
        return customFlags;
    }

    public void setCustomFlags(List<String> customFlags) {
        this.customFlags = customFlags;
    }

    public long getModSeqValue() {
        return modSeqValue;
    }

    public void setModSeqValue(long modSeqValue) {
        this.modSeqValue = modSeqValue;
    }

    public LocalPart getMainPart() {
        return mainPart;
    }

    public void setMainPart(LocalPart mainPart) {
        this.mainPart = mainPart;
    }

    public List<String> getGmailLabels() {
        return gmailLabels;
    }

    public void setGmailLabels(List<String> gmailLabels) {
        this.gmailLabels = gmailLabels;
    }

    public long getGmailMessageID() {
        return gmailMessageID;
    }

    public void setGmailMessageID(long gmailMessageID) {
        this.gmailMessageID = gmailMessageID;
    }

    public long getGmailThreadID() {
        return gmailThreadID;
    }

    public void setGmailThreadID(long gmailThreadID) {
        this.gmailThreadID = gmailThreadID;
    }
}
