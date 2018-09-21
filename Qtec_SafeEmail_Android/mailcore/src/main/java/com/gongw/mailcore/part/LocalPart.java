package com.gongw.mailcore.part;

import com.gongw.mailcore.message.LocalMessage;

import org.litepal.annotation.Column;
import org.litepal.crud.LitePalSupport;

/**
 * Created by gongw on 2018/9/11.
 */

public class LocalPart extends LitePalSupport {

    public static class Type{
        public static final String HTML_CONTENT = "html_content";
        public static final String TEXT_CONTENT = "text_content";
        public static final String ATTACHMENT = "attachment";
        public static final String INLINE = "inline";
    }

    /**
     * 数据存储在数据库
     */
    public static final int LOCATION_ON_DATABASE = 1;
    /**
     * 数据存储在外部存储
     */
    public static final int LOCATION_ON_DISK = 2;
    /**
     * 数据遗失或未下载
     */
    public static final int LOCATION_MISSING = 4;

    private long id;

    private int index;

    @Column(nullable = false)
    private LocalMessage localMessage;

    private String contentType;

    private String mimeType;

    private String disposition;

    private String encoding;

    private String fileName;

    private long size;

    private String contentId;

    private String localPath;

    private String localUri;

    private boolean isDecode;

    private int dataLocation;

    private String type;


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public LocalMessage getLocalMessage() {
        return localMessage;
    }

    public void setLocalMessage(LocalMessage localMessage) {
        this.localMessage = localMessage;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public String getDisposition() {
        return disposition;
    }

    public void setDisposition(String disposition) {
        this.disposition = disposition;
    }

    public String getEncoding() {
        return encoding;
    }

    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public String getContentId() {
        return contentId;
    }

    public void setContentId(String contentId) {
        this.contentId = contentId;
    }

    public String getLocalPath() {
        return localPath;
    }

    public void setLocalPath(String localPath) {
        this.localPath = localPath;
    }

    public String getLocalUri() {
        return localUri;
    }

    public void setLocalUri(String localUri) {
        this.localUri = localUri;
    }

    public boolean isDecode() {
        return isDecode;
    }

    public void setDecode(boolean decode) {
        isDecode = decode;
    }

    public int getDataLocation() {
        return dataLocation;
    }

    public void setDataLocation(int dataLocation) {
        this.dataLocation = dataLocation;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
