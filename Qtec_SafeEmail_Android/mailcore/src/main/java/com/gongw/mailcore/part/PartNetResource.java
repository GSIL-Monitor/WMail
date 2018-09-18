package com.gongw.mailcore.part;

import android.os.Environment;
import android.text.TextUtils;

import com.gongw.mailcore.MailFetcher;
import com.gongw.mailcore.NetResource;
import com.gongw.mailcore.message.LocalMessage;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.mail.BodyPart;
import javax.mail.Folder;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Part;
import javax.mail.internet.MimeBodyPart;

/**
 * +-------------------------  multipart/mixed  -----------------------+
 * |                                                                   |
 * |    +--------------------  multipart/related  ---------------+     |
 * |    |                                                        |     |
 * |    |   +----  multipart/alternative ----+   +----------+    |     |
 * |    |   |                                |   |   附件   |    |     |
 * |    |   |  +----------+    +----------+  |   +----------+    |     |
 * |    |   |  |纯文本正文|    |超文本正文|  |                   |     |
 * |    |   |  +----------+    +----------+  |   +----------+    |     |
 * |    |   |                                |   |   附件   |    |     |
 * |    |   +--------------------------------+   +----------+    |     |
 * |    |                                                        |     |
 * |    +--------------------------------------------------------+     |
 * |                                                                   |
 * +-------------------------------------------------------------------+
 * Created by gongw on 2018/9/18.
 */

public class PartNetResource extends NetResource {
    private File defaultDir;
    private File contentDir;
    private File attachmentDir;
    private File inlineDir;


    private static class InstanceHolder{
        private static PartNetResource instance = new PartNetResource();
    }

    private PartNetResource(){
        defaultDir = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/mailcore");
        if(!defaultDir.exists()){
            defaultDir.mkdirs();
        }
        contentDir = new File(defaultDir, "content");
        if(!contentDir.exists()){
            contentDir.mkdirs();
        }
        attachmentDir = new File(defaultDir, "attachment");
        if(!attachmentDir.exists()){
            attachmentDir.mkdirs();
        }
        inlineDir = new File(defaultDir, "inline");
        if(!inlineDir.exists()){
            inlineDir.mkdirs();
        }
    }

    public static PartNetResource singleInstance(){
        return InstanceHolder.instance;
    }

    public List<LocalPart> getParts(LocalMessage localMessage) throws MessagingException {
        MailFetcher fetcher = getFetcher(localMessage.getFolder().getAccount());
        List<LocalPart> localParts = new ArrayList<>();
//        fetcher.
//        Folder folder = store.getFolder(localMessage.getFolder().getFullName());
//        folder.open(Folder.READ_WRITE);
//        if(folder instanceof IMAPFolder){
//            IMAPFolder imapFolder = (IMAPFolder) folder;
//            Message message = imapFolder.getMessageByUID(Long.parseLong(localMessage.getUid()));
//            List<LocalPart> localParts = new ArrayList<>();
//            decodeMsgParts(localMessage, message, localParts, false);
//            partLocalResource.saveOrUpdateParts(localParts);
//        }
        return localParts;
    }


    private void decodeMsgParts(LocalMessage localMessage, Part part, List<LocalPart> partList, boolean save) throws IOException, MessagingException {
        String contentType = part.getContentType().toLowerCase();
        String disposition = part.getDisposition();
        if(contentType.startsWith("multipart/mixed") ||
                contentType.toLowerCase().startsWith("multipart/related")||
                contentType.toLowerCase().startsWith("multipart/alternative")){
            Multipart multiPart = (Multipart) part.getContent();
            int bodyCount = multiPart.getCount();
            for(int i=0;i<bodyCount;i++){
                BodyPart bodyPart = multiPart.getBodyPart(i);
                decodeMsgParts(localMessage, bodyPart, partList, save);
            }
        }else {
            LocalPart localPart = convertLocalPart(part);
            localPart.setLocalMessage(localMessage);
            if(save){
                File parent = defaultDir;
                if(disposition  == null){
                    parent = contentDir;
                    //正文部分
                    if(contentType.startsWith("text/html")){
                        localMessage.setHtmlContentPart(localPart);
                    }else if(contentType.startsWith("text/plain")){
                        localMessage.setTextContentPart(localPart);
                    }
                }else if(disposition.equals(Part.ATTACHMENT)){
                    //附件部分
                    parent = attachmentDir;
                }else if(disposition.equals(Part.INLINE)){
                    //正文引用
                    parent = inlineDir;
                }
                String originalFileName = part.getFileName();
                String fileName;
                if (!TextUtils.isEmpty(originalFileName) && originalFileName.matches(".*\\..*")) {
                    fileName = System.currentTimeMillis() + originalFileName.substring(originalFileName.lastIndexOf("."));
                } else {
                    fileName = System.currentTimeMillis() + ".txt";
                }
                File file = new File(parent, fileName);
//                    bodyPart.saveFile(file);//TODO:保存时需要处理编码
                localPart.setLocalPath(file.getAbsolutePath());
                localPart.setLocalUri(file.toURI().toString());
                localPart.setDataLocation(LocalPart.LOCATION_ON_DISK);
            }
            partList.add(localPart);
        }
    }

    private LocalPart convertLocalPart(Part part) throws MessagingException {
        LocalPart localPart = new LocalPart();
        localPart.setDataLocation(LocalPart.LOCATION_MISSING);
        localPart.setContentType(part.getContentType());
        localPart.setDisposition(part.getDisposition());
        localPart.setFileName(part.getFileName());
        localPart.setMimeType(part.getContentType().split(";")[0]);
        if(part instanceof MimeBodyPart){
            localPart.setContentId(((MimeBodyPart)part).getContentID());
            localPart.setEncoding(((MimeBodyPart)part).getEncoding());
        }
        return localPart;
    }
}
