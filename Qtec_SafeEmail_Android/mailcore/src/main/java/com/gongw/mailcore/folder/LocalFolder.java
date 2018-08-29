package com.gongw.mailcore.folder;

import org.litepal.crud.LitePalSupport;

/**
 * Created by gongw on 2018/7/22.
 */

public class LocalFolder extends LitePalSupport {

    private String path;

    private char delimiter;

    private int flags;

    private long uidNext;

    private long uidValidity;

    private long modSequenceValue;

    private int messageCount;

    private long firstUnseenUid;

    private boolean allowsNewPermanentFlags;

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public char getDelimiter() {
        return delimiter;
    }

    public void setDelimiter(char delimiter) {
        this.delimiter = delimiter;
    }

    public int getFlags() {
        return flags;
    }

    public void setFlags(int flags) {
        this.flags = flags;
    }

    public long getUidNext() {
        return uidNext;
    }

    public void setUidNext(long uidNext) {
        this.uidNext = uidNext;
    }

    public long getUidValidity() {
        return uidValidity;
    }

    public void setUidValidity(long uidValidity) {
        this.uidValidity = uidValidity;
    }

    public long getModSequenceValue() {
        return modSequenceValue;
    }

    public void setModSequenceValue(long modSequenceValue) {
        this.modSequenceValue = modSequenceValue;
    }

    public int getMessageCount() {
        return messageCount;
    }

    public void setMessageCount(int messageCount) {
        this.messageCount = messageCount;
    }

    public long getFirstUnseenUid() {
        return firstUnseenUid;
    }

    public void setFirstUnseenUid(long firstUnseenUid) {
        this.firstUnseenUid = firstUnseenUid;
    }

    public boolean isAllowsNewPermanentFlags() {
        return allowsNewPermanentFlags;
    }

    public void setAllowsNewPermanentFlags(boolean allowsNewPermanentFlags) {
        this.allowsNewPermanentFlags = allowsNewPermanentFlags;
    }
}
