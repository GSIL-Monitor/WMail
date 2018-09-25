package com.gongw.mailcore.part;

import android.os.Environment;
import android.text.TextUtils;
import com.gongw.mailcore.MailFetcher;
import com.gongw.mailcore.NetResource;
import com.gongw.mailcore.message.LocalMessage;

import org.w3c.dom.Text;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Part;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeUtility;

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
        if(part.isMimeType("multipart/mixed")){
            Multipart multiPart = (Multipart) part.getContent();
            return filterContentPart(multiPart.getBodyPart(0));
        }
        if(part.isMimeType("multipart/related")){
            Multipart multiPart = (Multipart) part.getContent();
            return filterContentPart(multiPart.getBodyPart(0));
        }
        List<Part> contentParts = new ArrayList<>();
        if(part.isMimeType("multipart/alternative")){
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
        if(part.isMimeType("multipart/mixed")){
            Multipart multiPart = (Multipart) part.getContent();
            return filterInlineParts(multiPart.getBodyPart(0));
        }
        List<Part> inLineParts = new ArrayList<>();
        if(part.isMimeType("multipart/related")){
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
        List<Part> attachmentParts = new ArrayList<>();
        if(part.isMimeType("multipart/mixed")){
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
        String disposition = part.getDisposition();
        if(part.isMimeType("multipart/*")){
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
                if(part.isMimeType("text/html") || part.isMimeType("message/rfc822")){
                    localPart.setType(LocalPart.Type.HTML_CONTENT);
                }else if(part.isMimeType("text/plain")){
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

            File file = savePartToFile(part, parent);
            localPart.setLocalPath(file.getAbsolutePath());
            localPart.setLocalUri(file.toURI().toString());
            localPart.setDataLocation(LocalPart.Location.LOCATION_ON_DISK);
            partList.add(localPart);
        }
    }

    private LocalPart convertLocalPart(Part part) throws MessagingException {
        LocalPart localPart = new LocalPart();
        localPart.setDataLocation(LocalPart.Location.LOCATION_MISSING);
        localPart.setSize(part.getSize());
        localPart.setContentType(part.getContentType());
        localPart.setDisposition(part.getDisposition());
        localPart.setFileName(part.getFileName());
        localPart.setMimeType(part.getContentType().split(";")[0]);
        String charset = "iso-8859-1";
        int charsetIndex = part.getContentType().indexOf("charset=");
        if(charsetIndex!= -1){
            charset = part.getContentType().substring(charsetIndex + 8);
        }
        localPart.setCharset(charset);
        if(part instanceof MimeBodyPart){
            localPart.setContentId(((MimeBodyPart)part).getContentID());
            localPart.setEncoding(((MimeBodyPart)part).getEncoding());
        }
        return localPart;
    }

    private File savePartToFile(Part part, File dir) throws MessagingException, UnsupportedEncodingException {
        if(!dir.exists()){
            dir.mkdirs();
        }
        String fileName = part.getFileName();
        if(TextUtils.isEmpty(fileName)){
            fileName = System.currentTimeMillis() + ".txt";
        }else{
            fileName = System.currentTimeMillis() + MimeUtility.decodeText(fileName);
        }
        File file = new File(dir, fileName);
        OutputStream fos = null;
        InputStream is = null;
        try {
            is = part.getInputStream();
            if(part instanceof MimeBodyPart){
                MimeBodyPart mimeBodyPart = (MimeBodyPart) part;
                String encoding = mimeBodyPart.getEncoding();
                if(encoding!=null){
                    is = MimeUtility.decode(part.getInputStream(), mimeBodyPart.getEncoding());
                }
            }
            fos = new FileOutputStream(file);

            byte[] buffer = new byte[1024];
            int length;
            while((length = is.read(buffer)) != -1){
                fos.write(buffer, 0, length);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (MessagingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                if (is != null) {
                    is.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                if (fos != null) {
                    fos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return file;
    }
}
