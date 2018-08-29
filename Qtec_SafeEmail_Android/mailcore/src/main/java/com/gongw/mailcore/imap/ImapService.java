package com.gongw.mailcore.imap;

import com.sun.mail.imap.IMAPStore;

import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.NoSuchProviderException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;

/**
 * Created by gongw on 2018/8/29.
 */

public class ImapService {
    private IMAPStore imapStore;

    private ImapService(Session session){
        try {
            imapStore = (IMAPStore) session.getStore("imap");
            imapStore.connect();
        } catch (NoSuchProviderException e) {
            e.printStackTrace();
        }
    }
}
