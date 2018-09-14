package com.gongw.mailcore.part;

import com.gongw.mailcore.message.LocalMessage;

import org.litepal.crud.LitePalSupport;

/**
 * Created by gongw on 2018/9/11.
 */

public class LocalPart extends LitePalSupport {

    private long id;

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


}
