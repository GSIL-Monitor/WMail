package com.gongw.mailcore.message;

import com.gongw.mailcore.net.MailSession;

import java.io.File;
import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.Address;
import javax.mail.MessagingException;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

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
 * Created by gongw on 2018/9/26.
 */

public class MessageBuilder {

    private String subject;
    private Address from;
    private Address[] to;
    private Address[] cc;
    private Address[] bcc;
    private Address[] reply;
    private Address sender;
    private String content = "";
    private File[] inLines;
    private File[] attachments;

    public MessageBuilder subject(String subject){
        this.subject = subject;
        return this;
    }

    public MessageBuilder to(Address[] to){
        this.to = to;
        return this;
    }

    public MessageBuilder cc(Address[] cc){
        this.cc = cc;
        return this;
    }

    public MessageBuilder bcc(Address[] bcc){
        this.bcc = bcc;
        return this;
    }

    public MessageBuilder reply(Address[] reply){
        this.reply = reply;
        return this;
    }

    public MessageBuilder from(Address from){
        this.from = from;
        return this;
    }

    public MessageBuilder sender(Address sender){
        this.sender = sender;
        return this;
    }

    public MessageBuilder content(String content){
        this.content = content;
        return this;
    }

    public MessageBuilder inLine(File[] inLines){
        this.inLines = inLines;
        return this;
    }

    public MessageBuilder attachment(File[] attachments){
        this.attachments = attachments;
        return this;
    }

    public MimeMessage build() throws MessagingException {
        MimeMessage mimeMessage = new MimeMessage(MailSession.getDefaultSession());
        //头部基本信息
        mimeMessage.setSubject(subject);
        mimeMessage.setFrom(from);
        mimeMessage.setSender(sender);
        mimeMessage.setRecipients(MimeMessage.RecipientType.TO, to);
        mimeMessage.setRecipients(MimeMessage.RecipientType.CC, cc);
        mimeMessage.setRecipients(MimeMessage.RecipientType.BCC, bcc);
        mimeMessage.setReplyTo(reply);

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
                mixedMultiPart.addBodyPart(attachPart);
            }
            mixedMultiPart.setSubType("mixed");
            mimeMessage.setContent(mixedMultiPart);
        }

        mimeMessage.saveChanges();
        return mimeMessage;
    }



}
