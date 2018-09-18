package com.gongw.mailcore;

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
    protected Map<String, MailFetcher> fetcherMap;

    /**
     * 获取缓存的该邮箱的MailFetcher
     * @param account 邮箱账号信息
     * @return MailFetcher对象
     * @throws MessagingException
     */
    protected MailFetcher getFetcher(Account account) throws MessagingException {
        if(fetcherMap == null){
            fetcherMap = new HashMap<>();
        }
        String email = account.getEmail();
        String storeUrl = account.getStoreUrl();
        if(!fetcherMap.containsKey(email) || !fetcherMap.get(email).getStoreUrl().equals(storeUrl)){
            MailFetcher fetcher = new MailFetcher(storeUrl);
            fetcherMap.put(email, fetcher);
        }
        return fetcherMap.get(email);
    }

}
