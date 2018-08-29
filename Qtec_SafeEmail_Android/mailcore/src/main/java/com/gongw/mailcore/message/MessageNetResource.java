package com.gongw.mailcore.message;

import com.gongw.mailcore.folder.LocalFolder;
import com.gongw.mailcore.setting.ConnectInfo;
import com.libmailcore.IMAPMessage;
import com.libmailcore.IndexSet;
import com.libmailcore.MailException;

import java.util.List;

/**
 * Created by gongw on 2018/7/17.
 */

public class MessageNetResource {

    private ImapService imapService;

    private static class InstanceHolder{
        private static MessageNetResource instance = new MessageNetResource();
    }

    public static MessageNetResource getInstance(){
        return InstanceHolder.instance;
    }

    private MessageNetResource(ConnectInfo connectInfo){
        imapService = new ImapService(connectInfo);
    }

    public void getMessages(LocalFolder folder, int messagesRequestKind, IndexSet indexSet, List<String> extraHeaders){
        imapService.fetchMessages(folder.getPath(), messagesRequestKind, indexSet, extraHeaders, new ImapOperationCallback.FetchMessagesOperationCallback() {
            @Override
            public void succeeded(List<IMAPMessage> imapMessages) {

            }

            @Override
            public void failed(MailException e) {

            }
        });
    }


}
