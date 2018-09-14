package com.gongw.mailcore;

import javax.mail.Folder;
import javax.mail.MessagingException;
import javax.mail.NoSuchProviderException;
import javax.mail.URLName;

/**
 * Created by gongw on 2018/9/3.
 */

public class Test {

    public static void main(String[] args){
        URLName urlName = new URLName("imap", "imaps.exmail.qq.com", 993, null, "gongw@qtec.cn", "Gg147258");
        try {
            IMAPFetcher IMAPFetcher = new IMAPFetcher(urlName);
            Folder[] folders = IMAPFetcher.fetchFolders();
            System.out.println(folders[0].getName());
        } catch (NoSuchProviderException e) {
            e.printStackTrace();
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }
}
