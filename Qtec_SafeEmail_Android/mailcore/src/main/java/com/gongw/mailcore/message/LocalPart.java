package com.gongw.mailcore.message;

import com.libmailcore.AbstractPart;

import org.litepal.crud.LitePalSupport;

/**
 * Created by gongw on 2018/7/22.
 */

public class LocalPart extends LitePalSupport {

    private  int partType;

    private String filename;

    private String mimeType;

    private String charset;

    private String uniqueID;

    private String contentID;

    private String contentLocation;

    private String contentDescription;

    private boolean isInlineAttachment;

    public LocalPart(AbstractPart part){
        this.partType = part.partType();
        this.filename = part.filename();
        this.mimeType = part.mimeType();
        this.charset = part.charset();
        this.uniqueID = part.uniqueID();
        this.contentID = part.contentID();
        this.contentDescription = part.contentDescription();
        this.contentLocation = part.contentLocation();
        this.isInlineAttachment = part.isInlineAttachment();
    }

    public AbstractPart toAbstractPart(){
        AbstractPart abstractPart = new AbstractPart();
        abstractPart.setPartType(partType);
        abstractPart.setFilename(filename);
        abstractPart.setMimeType(mimeType);
        abstractPart.setCharset(charset);
        abstractPart.setUniqueID(uniqueID);
        abstractPart.setContentID(contentID);
        abstractPart.setContentLocation(contentLocation);
        abstractPart.setContentDescription(contentDescription);
        abstractPart.setInlineAttachment(isInlineAttachment);
        return abstractPart;
    }

    public LocalPart(){}

    public int getPartType() {
        return partType;
    }

    public void setPartType(int partType) {
        this.partType = partType;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public String getCharset() {
        return charset;
    }

    public void setCharset(String charset) {
        this.charset = charset;
    }

    public String getUniqueID() {
        return uniqueID;
    }

    public void setUniqueID(String uniqueID) {
        this.uniqueID = uniqueID;
    }

    public String getContentID() {
        return contentID;
    }

    public void setContentID(String contentID) {
        this.contentID = contentID;
    }

    public String getContentLocation() {
        return contentLocation;
    }

    public void setContentLocation(String contentLocation) {
        this.contentLocation = contentLocation;
    }

    public String getContentDescription() {
        return contentDescription;
    }

    public void setContentDescription(String contentDescription) {
        this.contentDescription = contentDescription;
    }

    public boolean isInlineAttachment() {
        return isInlineAttachment;
    }

    public void setInlineAttachment(boolean inlineAttachment) {
        isInlineAttachment = inlineAttachment;
    }
}
