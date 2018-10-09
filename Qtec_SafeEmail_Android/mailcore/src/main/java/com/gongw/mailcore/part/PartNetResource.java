package com.gongw.mailcore.part;

import android.os.Environment;
import android.text.TextUtils;

import com.gongw.mailcore.message.LocalMessage;
import com.gongw.mailcore.net.NetResource;
import com.sun.mail.imap.IMAPFolder;

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
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.MethodNotSupportedException;
import javax.mail.Multipart;
import javax.mail.Part;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeUtility;

/**
 * 网络上的邮件part资源类，提供操作网络上的Part数据的接口
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
    /**
     * 用于保存part数据的文件夹
     */
    private File defaultDir;
    /**
     * 用于保存正文类型part数据的文件夹
     */
    private File contentDir;
    /**
     * 用于保存附件类型part数据的文件夹
     */
    private File attachmentDir;
    /**
     * 用于保存正文内联引用类型part数据的文件夹
     */
    private File inlineDir;

    private static class InstanceHolder{
        private static PartNetResource instance = new PartNetResource();
    }

    private PartNetResource(){
        defaultDir = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+ "/mailcore");
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

    /**
     * 获取指定邮件下的所有part，不包含multipart/*类型的
     * @param localMessage 指定的邮件
     * @param saveToLocal 是否将part内容保存到本地
     * @return LocalPart集合
     * @throws MessagingException
     * @throws IOException
     */
    public List<LocalPart> getAllParts(LocalMessage localMessage, boolean saveToLocal) throws MessagingException, IOException {
        Folder folder = openFolder(localMessage.getFolder(), Folder.READ_ONLY);
        if(folder instanceof IMAPFolder){
            IMAPFolder imapFolder = (IMAPFolder) folder;
            Message message = imapFolder.getMessageByUID(localMessage.getUid());
            List<LocalPart> localParts = new ArrayList<>();
            parseMsgPart(localMessage, message, localParts, saveToLocal);
            return localParts;
        }
        throw new MethodNotSupportedException("getMessageByUID");
    }

    /**
     * 获取指定邮件的正文part
     * @param localMessage 指定的邮件
     * @param saveToLocal 是否将part内容保存到本地
     * @return LocalPart集合
     * @throws MessagingException
     * @throws IOException
     */
    public List<LocalPart> getContentParts(LocalMessage localMessage, boolean saveToLocal) throws MessagingException, IOException {
        Folder folder = openFolder(localMessage.getFolder(), Folder.READ_ONLY);
        if(folder instanceof IMAPFolder){
            IMAPFolder imapFolder = (IMAPFolder) folder;
            Message message = imapFolder.getMessageByUID(localMessage.getUid());
            List<LocalPart> localParts = new ArrayList<>();
            List<Part> contentParts = filterContentPart(message);
            for(Part part : contentParts){
                parseMsgPart(localMessage, part, localParts, saveToLocal);
            }
            return localParts;
        }
        throw new MethodNotSupportedException("getMessageByUID");
    }

    /**
     * 获取指定邮件的正文内联引用part
     * @param localMessage 指定的邮件
     * @param saveToLocal 是否将part内容保存到本地
     * @return LocalPart集合
     * @throws MessagingException
     * @throws IOException
     */
    public List<LocalPart> getInlineParts(LocalMessage localMessage, boolean saveToLocal) throws MessagingException, IOException {
        Folder folder = openFolder(localMessage.getFolder(), Folder.READ_ONLY);
        if(folder instanceof IMAPFolder){
            IMAPFolder imapFolder = (IMAPFolder) folder;
            Message message = imapFolder.getMessageByUID(localMessage.getUid());
            List<LocalPart> localParts = new ArrayList<>();
            List<Part> inlineParts = filterInlineParts(message);
            for(Part part : inlineParts){
                parseMsgPart(localMessage, part, localParts, saveToLocal);
            }
            return localParts;
        }
        throw new MethodNotSupportedException("getMessageByUID");
    }

    /**
     * 获取指定邮件的附件part
     * @param localMessage 指定的邮件
     * @param saveToLocal 是否将part内容保存到本地
     * @return LocalPart集合
     * @throws MessagingException
     * @throws IOException
     */
    public LocalPart getAttachmentPartByIndex(LocalMessage localMessage, int index, boolean saveToLocal) throws MessagingException, IOException {
        Folder folder = openFolder(localMessage.getFolder(), Folder.READ_ONLY);
        if(folder instanceof IMAPFolder){
            IMAPFolder imapFolder = (IMAPFolder) folder;
            Message message = imapFolder.getMessageByUID(localMessage.getUid());
            List<LocalPart> localParts = new ArrayList<>();
            List<Part> attachmentParts = filterAttachmentParts(message);
            parseMsgPart(localMessage, attachmentParts.get(index), localParts, saveToLocal);
            return localParts.get(0);
        }
        throw new MethodNotSupportedException("getMessageByUID");
    }

    /**
     * 从part中过滤出正文类型的part
     * @param part 指定part
     * @return Part集合
     * @throws MessagingException
     * @throws IOException
     */
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

    /**
     * 从part中过滤出正文引用类型的part
     * @param part 指定part
     * @return Part集合
     * @throws MessagingException
     * @throws IOException
     */
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

    /**
     * 从part中过滤出附件类型的part
     * @param part 指定part
     * @return Part集合
     * @throws MessagingException
     * @throws IOException
     */
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

    /**
     * 解析除multipart/*类型外的part，并将part内容保存到本地
     * @param localMessage 指定邮件
     * @param part 起始part
     * @param partList 解析后的part转为LocalPart，并添加到该集合中
     * @param saveToLocal 决定是否将part内容保存到本地
     * @throws IOException
     * @throws MessagingException
     */
    private void parseMsgPart(LocalMessage localMessage, Part part, List<LocalPart> partList, boolean saveToLocal) throws IOException, MessagingException {
        if(part.isMimeType("multipart/*")){
            Multipart multiPart = (Multipart) part.getContent();
            int bodyCount = multiPart.getCount();
            for(int i=0;i<bodyCount;i++){
                BodyPart bodyPart = multiPart.getBodyPart(i);
                parseMsgPart(localMessage, bodyPart, partList, saveToLocal);
            }
        }else {
            LocalPart localPart = convertLocalPart(part, localMessage);
            if(saveToLocal){
                savePartToFile(part, localPart);
            }
            partList.add(localPart);
        }
    }

    /**
     * 将part转换为LocalPart
     * @param part 需要转换的part
     * @param localMessage part所属的邮件
     * @return LocalPart
     * @throws MessagingException
     */
    private LocalPart convertLocalPart(Part part, LocalMessage localMessage) throws MessagingException {
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
        String disposition = part.getDisposition();
        if(disposition  == null){
            //正文part
            if(part.isMimeType("text/html") || part.isMimeType("message/rfc822")){
                localPart.setType(LocalPart.Type.HTML_CONTENT);
            }else if(part.isMimeType("text/plain")){
                localPart.setType(LocalPart.Type.TEXT_CONTENT);
            }
        }else if(disposition.equals(Part.ATTACHMENT)){
            //附件part
            localPart.setType(LocalPart.Type.ATTACHMENT);
        }else if(disposition.equals(Part.INLINE)){
            //正文引用part
            localPart.setType(LocalPart.Type.INLINE);
        }
        localPart.setLocalMessage(localMessage);
        return localPart;
    }

    /**
     * 将part内容保存到文件
     * @param part 需要保存的part
     * @param localPart 与part对应的LocalPart
     * @throws MessagingException
     * @throws UnsupportedEncodingException
     */
    private void savePartToFile(Part part, LocalPart localPart) throws MessagingException, UnsupportedEncodingException {
        File dir = defaultDir;
        switch (localPart.getType()){
            case LocalPart.Type.HTML_CONTENT:
            case LocalPart.Type.TEXT_CONTENT:
                dir = contentDir;
                break;
            case LocalPart.Type.INLINE:
                dir = inlineDir;
                break;
            case LocalPart.Type.ATTACHMENT:
                dir = attachmentDir;
                break;
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
            localPart.setLocalPath(file.getAbsolutePath());
            localPart.setLocalUri(file.toURI().toString());
            localPart.setDataLocation(LocalPart.Location.LOCATION_ON_DISK);
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
    }
}
