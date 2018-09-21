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
import javax.mail.Message;
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

    public List<LocalPart> getAllParts(LocalMessage localMessage) throws MessagingException, IOException {
        MailFetcher fetcher = getFetcher(localMessage.getFolder().getAccount());
        List<LocalPart> localParts = new ArrayList<>();
        Message message = fetcher.fetchMessage(localMessage.getFolder().getFullName(), Long.parseLong(localMessage.getUid()));
        parseMsgPart(localMessage, message, localParts);
        return localParts;
    }

    public List<LocalPart> getContentParts(LocalMessage localMessage) throws MessagingException, IOException {
        MailFetcher fetcher = getFetcher(localMessage.getFolder().getAccount());
        List<LocalPart> localParts = new ArrayList<>();
        Message message = fetcher.fetchMessage(localMessage.getFolder().getFullName(), Long.parseLong(localMessage.getUid()));
        List<Part> contentParts = filterContentPart(message);
        for(Part part : contentParts){
            parseMsgPart(localMessage, part, localParts);
        }
        return localParts;
    }

    public List<LocalPart> getInlineParts(LocalMessage localMessage) throws MessagingException, IOException {
        MailFetcher fetcher = getFetcher(localMessage.getFolder().getAccount());
        List<LocalPart> localParts = new ArrayList<>();
        Message message = fetcher.fetchMessage(localMessage.getFolder().getFullName(), Long.parseLong(localMessage.getUid()));
        List<Part> inlineParts = filterInlineParts(message);
        for(Part part : inlineParts){
            parseMsgPart(localMessage, part, localParts);
        }
        return localParts;
    }

    public LocalPart getAttachmentPartByIndex(LocalMessage localMessage, int index) throws MessagingException, IOException {
        MailFetcher fetcher = getFetcher(localMessage.getFolder().getAccount());
        List<LocalPart> localParts = new ArrayList<>();
        Message message = fetcher.fetchMessage(localMessage.getFolder().getFullName(), Long.parseLong(localMessage.getUid()));
        List<Part> attachmentParts = filterAttachmentParts(message);
        parseMsgPart(localMessage, attachmentParts.get(index), localParts);
        return localParts.get(0);
    }

    private List<Part> filterContentPart(Part part) throws MessagingException, IOException {
        String contentType = part.getContentType().toLowerCase();
        if(contentType.startsWith("multipart/mixed")){
            Multipart multiPart = (Multipart) part.getContent();
            return filterContentPart(multiPart.getBodyPart(0));
        }
        if(contentType.startsWith("multipart/related")){
            Multipart multiPart = (Multipart) part.getContent();
            return filterContentPart(multiPart.getBodyPart(0));
        }
        List<Part> contentParts = new ArrayList<>();
        if(contentType.startsWith("multipart/alternative")){
            Multipart multiPart = (Multipart) part.getContent();
            int count = multiPart.getCount();
            for(int i=0;i<count;i++){
                Part contentPart = multiPart.getBodyPart(i);
                contentParts.add(contentPart);
            }
            return contentParts;
        }
        contentParts.add(part);
        return contentParts;
    }

    private List<Part> filterInlineParts(Part part) throws MessagingException, IOException {
        String contentType = part.getContentType().toLowerCase();
        if(contentType.startsWith("multipart/mixed")){
            Multipart multiPart = (Multipart) part.getContent();
            return filterInlineParts(multiPart.getBodyPart(0));
        }
        List<Part> inLineParts = new ArrayList<>();
        if(contentType.startsWith("multipart/related")){
            Multipart multiPart = (Multipart) part.getContent();
            int count = multiPart.getCount();
            for(int i=1;i<count;i++){
                Part inline = multiPart.getBodyPart(i);
                if(Part.INLINE.equals(inline.getDisposition())){
                    inLineParts.add(inline);
                }
            }
            return inLineParts;
        }
        return inLineParts;
    }

    private List<Part> filterAttachmentParts(Part part) throws MessagingException, IOException {
        String contentType = part.getContentType().toLowerCase();
        List<Part> attachmentParts = new ArrayList<>();
        if(contentType.startsWith("multipart/mixed")){
            Multipart multiPart = (Multipart) part.getContent();
            int count = multiPart.getCount();
            for(int i=1;i<count;i++){
                Part attachment = multiPart.getBodyPart(i);
                if(Part.ATTACHMENT.equals(attachment.getDisposition())){
                    attachmentParts.add(attachment);
                }
            }
            return attachmentParts;
        }
        return attachmentParts;
    }

    private void parseMsgPart(LocalMessage localMessage, Part part, List<LocalPart> partList) throws IOException, MessagingException {
        String contentType = part.getContentType().toLowerCase();
        String disposition = part.getDisposition();
        if(contentType.startsWith("multipart")){
            Multipart multiPart = (Multipart) part.getContent();
            int bodyCount = multiPart.getCount();
            for(int i=0;i<bodyCount;i++){
                BodyPart bodyPart = multiPart.getBodyPart(i);
                parseMsgPart(localMessage, bodyPart, partList);
            }
        }else {
            LocalPart localPart = convertLocalPart(part);
            localPart.setLocalMessage(localMessage);
            File parent = defaultDir;
            if(disposition  == null){
                //正文部分
                parent = contentDir;
                if(contentType.startsWith("text/html")){
                    localPart.setType(LocalPart.Type.HTML_CONTENT);
                }else if(contentType.startsWith("text/plain")){
                    localPart.setType(LocalPart.Type.TEXT_CONTENT);
                }
            }else if(disposition.equals(Part.ATTACHMENT)){
                //附件部分
                parent = attachmentDir;
                localPart.setType(LocalPart.Type.ATTACHMENT);
            }else if(disposition.equals(Part.INLINE)){
                //正文引用
                parent = inlineDir;
                localPart.setType(LocalPart.Type.INLINE);
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
