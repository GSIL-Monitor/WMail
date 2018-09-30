package com.gongw.mailcore.net;

import com.gongw.mailcore.account.Account;

import java.util.HashMap;
import java.util.Map;
import javax.mail.MessagingException;

/**
 * Created by gongw on 2018/9/18.
 */

public class NetResource {
    /**
     * 以email,MailFetcher键值对形式缓存的MailFetcher
     */
    private static Map<String, MessageFetcher> fetcherMap = new HashMap<>();
    /**
     * 以email,MailFetcher键值对形式缓存的MailSender
     */
    private static Map<String, MessageSender> senderMap = new HashMap<>();

    /**
     * 获取缓存的该邮箱的MailFetcher
     * @param account 邮箱账号信息
     * @return MailFetcher对象
     * @throws MessagingException
     */
    protected MessageFetcher getFetcher(Account account) throws MessagingException {
        synchronized (fetcherMap){
            String email = account.getEmail();
            String storeUrl = account.getStoreUrl();
            if(!fetcherMap.containsKey(email) || !fetcherMap.get(email).getStoreUrl().equals(storeUrl)){
                MessageFetcher fetcher = new MessageFetcher(storeUrl);
                fetcherMap.put(email, fetcher);
            }
            return fetcherMap.get(email);
        }
    }

    /**
     * 获取缓存的该邮箱的MailSender
     * @param account 邮箱账号信息
     * @return MailSender对象
     * @throws MessagingException
     */
    protected MessageSender getSender(Account account) throws MessagingException {
        synchronized (senderMap){
            String email = account.getEmail();
            String transportUrl = account.getTransportUrl();
            if(!senderMap.containsKey(email) || !senderMap.get(email).getTransportUrl().equals(transportUrl)){
                MessageSender sender = new MessageSender(transportUrl);
                senderMap.put(email, sender);
            }
            return senderMap.get(email);
        }
    }

}
