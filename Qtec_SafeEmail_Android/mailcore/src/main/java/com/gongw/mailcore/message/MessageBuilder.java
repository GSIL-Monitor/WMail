package com.gongw.mailcore.message;

import com.gongw.mailcore.net.MailSession;
import com.gongw.mailcore.part.LocalPart;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.Address;
import javax.mail.MessagingException;
import javax.mail.Part;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

/**
 * 用于生成MimeMessage邮件，邮件的内容结构如下：
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
 * Created by gongw on 2018/9/26.
 */
public class MessageBuilder {
    /**
     * 邮件主题
     */
    private String subject;
    /**
     * 邮件来源人
     */
    private Address[] from;
    /**
     * 邮件收件人
     */
    private Address[] to;
    /**
     * 邮件抄送人
     */
    private Address[] cc;
    /**
     * 邮件密送人
     */
    private Address[] bcc;
    /**
     * 邮件回复人
     */
    private Address[] reply;
    /**
     * 邮件发送人
     */
    private Address sender;
    /**
     * 发送日期
     */
    private Date sentDate;
    /**
     * 自定义header
     */
    private Map<String, String> customHeadersMap;
    /**
     * 邮件正文
     */
    private String content;
    /**
     * 邮件正文的内联引用
     */
    private File[] inLines;
    /**
     * 邮件附件
     */
    private File[] attachments;

    public MessageBuilder subject(String subject){
        this.subject = subject;
        return this;
    }

    public MessageBuilder to(Address[] to){
        this.to = to;
        return this;
    }

    public MessageBuilder to(List<MessageContact> to) throws UnsupportedEncodingException {
        if(to == null || to.size() < 1){
            return this;
        }
        int size = to.size();
        Address[] toAddress = new Address[size];
        for(int i=0;i<size;i++){
            MessageContact messageContact = to.get(i);
            toAddress[i] = messageContact.getContact().toAddress();
        }
        this.to = toAddress;
        return this;
    }

    public MessageBuilder cc(Address[] cc){
        this.cc = cc;
        return this;
    }

    public MessageBuilder cc(List<MessageContact> cc) throws UnsupportedEncodingException {
        if(cc == null || cc.size() < 1){
            return this;
        }
        int size = cc.size();
        Address[] ccAddress = new Address[size];
        for(int i=0;i<size;i++){
            MessageContact messageContact = cc.get(i);
            ccAddress[i] = messageContact.getContact().toAddress();
        }
        this.cc = ccAddress;
        return this;
    }

    public MessageBuilder bcc(Address[] bcc){
        this.bcc = bcc;
        return this;
    }

    public MessageBuilder bcc(List<MessageContact> bcc) throws UnsupportedEncodingException {
        if(bcc == null || bcc.size() < 1){
            return this;
        }
        int size = bcc.size();
        Address[] bccAddress = new Address[size];
        for(int i=0;i<size;i++){
            MessageContact messageContact = bcc.get(i);
            bccAddress[i] = messageContact.getContact().toAddress();
        }
        this.bcc = bccAddress;
        return this;
    }

    public MessageBuilder reply(Address[] reply){
        this.reply = reply;
        return this;
    }

    public MessageBuilder reply(List<MessageContact> reply) throws UnsupportedEncodingException {
        if(reply == null || reply.size() < 1){
            return this;
        }
        int size = reply.size();
        Address[] replyAddress = new Address[size];
        for(int i=0;i<size;i++){
            MessageContact messageContact = reply.get(i);
            replyAddress[i] = messageContact.getContact().toAddress();
        }
        this.reply = replyAddress;
        return this;
    }

    public MessageBuilder from(Address[] from){
        this.from = from;
        return this;
    }

    public MessageBuilder from(List<MessageContact> from) throws UnsupportedEncodingException {
        if(from == null || from.size() < 1){
            return this;
        }
        int size = from.size();
        Address[] fromAddress = new Address[size];
        for(int i=0;i<size;i++){
            MessageContact messageContact = from.get(i);
            fromAddress[i] = messageContact.getContact().toAddress();
        }
        this.from = fromAddress;
        return this;
    }

    public MessageBuilder sender(Address sender){
        this.sender = sender;
        return this;
    }

    public MessageBuilder sender(MessageContact sender) throws UnsupportedEncodingException {
        if(sender == null){
            return this;
        }
        this.sender = sender.getContact().toAddress();
        return this;
    }

    public MessageBuilder sentDate(Date sentDate){
        this.sentDate = sentDate;
        return this;
    }

    public MessageBuilder addHeader(String key, String value){
        if(key == null || value == null){
            return this;
        }
        if(this.customHeadersMap == null){
            this.customHeadersMap = new HashMap<>();
        }
        customHeadersMap.put(key, value);
        return this;
    }

    public MessageBuilder removeHeader(String key){
        if(key == null){
            return this;
        }
        if(this.customHeadersMap == null){
            this.customHeadersMap = new HashMap<>();
        }
        customHeadersMap.remove(key);
        return this;
    }

    public MessageBuilder content(String content){
        this.content = content;
        return this;
    }

    public MessageBuilder content(LocalPart part){
        if(part == null){
            return this;
        }
        FileInputStream fis = null;
        ByteArrayOutputStream bos = null;
        String content = "";
        try {
            fis = new FileInputStream(part.getLocalPath());
            bos = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int length;
            while((length = fis.read(buffer)) != -1){
                bos.write(buffer, 0, length);
            }
            content = bos.toString(part.getCharset());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if(fis != null){
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if(bos != null){
                try {
                    bos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return content(content);
    }

    public MessageBuilder inLine(File[] inLines){
        if(inLines == null || inLines.length < 1){
            return this;
        }
        this.inLines = inLines;
        return this;
    }

    public MessageBuilder inLine(List<LocalPart> localParts){
        if(localParts == null || localParts.size() < 1){
            return this;
        }
        int size = localParts.size();
        File[] files = new File[size];
        for(int i=0;i<size;i++){
            File file = new File(localParts.get(i).getLocalPath());
            files[i] = file;
        }
        return inLine(files);
    }

    public MessageBuilder attachment(File[] attachments){
        if(attachments == null || attachments.length < 1){
            return this;
        }
        this.attachments = attachments;
        return this;
    }

    public MessageBuilder attachment(List<LocalPart> localParts){
        if(localParts == null || localParts.size() < 1){
            return this;
        }
        int size = localParts.size();
        File[] files = new File[size];
        for(int i=0;i<size;i++){
            File file = new File(localParts.get(i).getLocalPath());
            files[i] = file;
        }
        return attachment(files);
    }

    /**
     * 生成一封MimeMessage邮件,可通过MessageSender的sendMsg发送
     * @return MimeMessage
     * @throws MessagingException
     */
    public MimeMessage build() throws MessagingException {
        MimeMessage mimeMessage = new MimeMessage(MailSession.getDefaultSession());
        //开始设置头部信息
        if(subject != null){
            mimeMessage.setSubject(subject);
        }
        if(from != null){
            mimeMessage.setFrom(from[0]);
        }
        if(sender != null){
            mimeMessage.setSender(sender);
        }
        if(to != null){
            mimeMessage.setRecipients(MimeMessage.RecipientType.TO, to);
        }
        if(cc != null){
            mimeMessage.setRecipients(MimeMessage.RecipientType.CC, cc);
        }
        if(bcc != null){
            mimeMessage.setRecipients(MimeMessage.RecipientType.BCC, bcc);
        }
        if(reply != null){
            mimeMessage.setReplyTo(reply);
        }
        if(sentDate != null){
            mimeMessage.setSentDate(sentDate);
        }
        if(customHeadersMap != null){
            for(Iterator<Map.Entry<String, String>> iterator = customHeadersMap.entrySet().iterator();iterator.hasNext();){
                Map.Entry<String, String> next = iterator.next();
                mimeMessage.addHeader(next.getKey(), next.getValue());
            }
        }
        //开始设置part信息
        //只有正文
        if(inLines == null && attachments == null){
            //正文

            mimeMessage.setContent(content, "text/html;charset=utf-8");
        }
        //只有正文和正文引用
        if(inLines != null && attachments == null){
            MimeMultipart multipart = new MimeMultipart();
            //正文
            MimeBodyPart contentPart = new MimeBodyPart();
            contentPart.setContent(content, "text/html;charset=utf-8");
            multipart.addBodyPart(contentPart);
            //正文引用
            for(File file : inLines){
                MimeBodyPart inLinePart = new MimeBodyPart();
                inLinePart.setDataHandler(new DataHandler(new FileDataSource(file)));
                inLinePart.setContentID(System.currentTimeMillis() + file.getName());
                inLinePart.setDescription(Part.INLINE);
                multipart.addBodyPart(inLinePart);
            }
            multipart.setSubType("related");
            mimeMessage.setContent(multipart);
        }
        //只有正文和附件
        if(inLines == null && attachments != null){
            MimeMultipart multipart = new MimeMultipart();
            //正文
            MimeBodyPart contentPart = new MimeBodyPart();
            contentPart.setContent(content, "text/html;charset=utf-8");
            multipart.addBodyPart(contentPart);
            //附件
            for(File file : attachments){
                MimeBodyPart attachPart = new MimeBodyPart();
                attachPart.setDataHandler(new DataHandler(new FileDataSource(file)));
                attachPart.setFileName(file.getName());
                attachPart.setDescription(Part.ATTACHMENT);
                multipart.addBodyPart(attachPart);
            }
            multipart.setSubType("mixed");
            mimeMessage.setContent(multipart);
        }
        //正文，正文引用和附件都有
        if(inLines != null && attachments != null){
            MimeMultipart relatedMultiPart = new MimeMultipart();
            //正文
            MimeBodyPart contentPart = new MimeBodyPart();
            contentPart.setContent(content, "text/html;charset=utf-8");
            relatedMultiPart.addBodyPart(contentPart);
            //正文引用
            for(File file : inLines){
                MimeBodyPart inLinePart = new MimeBodyPart();
                inLinePart.setDataHandler(new DataHandler(new FileDataSource(file)));
                inLinePart.setContentID(System.currentTimeMillis() + file.getName());
                inLinePart.setDescription(Part.INLINE);
                relatedMultiPart.addBodyPart(inLinePart);
            }
            relatedMultiPart.setSubType("related");

            MimeBodyPart newContentPart = new MimeBodyPart();
            newContentPart.setContent(relatedMultiPart);

            MimeMultipart mixedMultiPart = new MimeMultipart();
            mixedMultiPart.addBodyPart(newContentPart);
            //附件
            for(File file : attachments){
                MimeBodyPart attachPart = new MimeBodyPart();
                attachPart.setDataHandler(new DataHandler(new FileDataSource(file)));
                attachPart.setFileName(file.getName());
                attachPart.setDescription(Part.ATTACHMENT);
                mixedMultiPart.addBodyPart(attachPart);
            }
            mixedMultiPart.setSubType("mixed");
            mimeMessage.setContent(mixedMultiPart);
        }

        mimeMessage.saveChanges();
        return mimeMessage;
    }

    /**
     * 根据LocalFolder对象生成一封MimeMessage邮件,可通过MessageSender的sendMsg发送
     * @param localMessage LocalMessage对象
     * @return MimeMessage
     * @throws MessagingException
     */
    public MimeMessage build(LocalMessage localMessage) throws MessagingException, UnsupportedEncodingException {
        return subject(localMessage.getSubject())
                .sentDate(localMessage.getSentDate())
                .sender(localMessage.getSender())
                .from(localMessage.getFrom())
                .to(localMessage.getRecipientsTo())
                .cc(localMessage.getRecipientsCc())
                .bcc(localMessage.getRecipientsBcc())
                .reply(localMessage.getReplyTo())
                //TODO:添加自定义header
//                .addHeader()
                .content(localMessage.getHtmlContentPart() == null ? localMessage.getTextContentPart() : localMessage.getHtmlContentPart())
                .inLine(localMessage.getInLineParts())
                .attachment(localMessage.getAttachmentParts())
                .build();
    }

}
