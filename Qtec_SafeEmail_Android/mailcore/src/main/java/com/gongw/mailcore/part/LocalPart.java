package com.gongw.mailcore.part;

import com.gongw.mailcore.message.LocalMessage;

import org.litepal.annotation.Column;
import org.litepal.crud.LitePalSupport;

/**
 * 邮件中的part，通常有正文、正文内联引用、附件之分
 * Created by gongw on 2018/9/11.
 */
public class LocalPart extends LitePalSupport {

    public static class Type{
        /**
         * html正文part
         */
        public static final String HTML_CONTENT = "html_content";
        /**
         * text正文part
         */
        public static final String TEXT_CONTENT = "text_content";
        /**
         * 附件part
         */
        public static final String ATTACHMENT = "attachment";
        /**
         * 正文内联引用part
         */
        public static final String INLINE = "inline";
    }

    public static class Location{
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
    }

    private long id;
    /**
     * 在邮件中的序号，用于区分邮件中的各个part
     */
    private int index;
    /**
     * 属于哪封邮件
     */
    @Column(nullable = false)
    private LocalMessage localMessage;
    /**
     * 内容类型，一般包含MimeType和Charset
     */
    private String contentType;
    /**
     * part内容的MimeType
     */
    private String mimeType;
    /**
     * part内容的描述，通常有INLINE\ATTACHMENT\NULL三种
     */
    private String disposition;
    /**
     * 加密方式
     */
    private String encoding;
    /**
     * 文件名称
     */
    private String fileName;
    /**
     * 大小
     */
    private long size;
    /**
     * 内容id，只有类型为INLINE时才有值
     */
    private String contentId;
    /**
     * 在本地的存储路径
     */
    private String localPath;
    /**
     * 在本地的uri
     */
    private String localUri;
    /**
     * 是否解密
     */
    private boolean isDecode;
    /**
     * 在本地的状态,对应静态内部类Location中的值
     */
    private int dataLocation;
    /**
     * part在邮件中的类型，对应静态内部类Type中的类型
     */
    private String type;
    /**
     * part内容的编码方式
     */
    private String charset;


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

    public String getCharset() {
        return charset;
    }

    public void setCharset(String charset) {
        this.charset = charset;
    }
}
