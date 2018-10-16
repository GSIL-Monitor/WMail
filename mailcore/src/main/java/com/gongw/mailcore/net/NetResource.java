package com.gongw.mailcore.net;



import com.gongw.mailcore.account.Account;
import com.gongw.mailcore.folder.LocalFolder;
import java.util.HashMap;
import java.util.Map;
import javax.mail.Folder;
import javax.mail.MessagingException;
import javax.mail.Store;
import javax.mail.Transport;
import javax.mail.URLName;

/**
 * Created by gongw on 2018/9/18.
 */

public class NetResource {
    /**
     * 以email,Store键值对形式缓存的Store
     */
    private static final Map<String, Store> storeMap = new HashMap<>();
    /**
     * 以email,Transport键值对形式缓存的Transport
     */
    private static final Map<String, Transport> transportMap = new HashMap<>();

    /**
     * 获取缓存的该邮箱的Store
     * @param account 邮箱账号信息
     * @return Store对象
     * @throws MessagingException
     */
    protected Store getStore(Account account) throws MessagingException {
        String email = account.getEmail();
        String storeUrl = account.getStoreUrl();
        Store store;
        synchronized (storeMap){
            if(!storeMap.containsKey(email) || !storeMap.get(email).getURLName().toString().equals(storeUrl)){
                URLName urlName = new URLName(storeUrl);
                store = MailSession.getDefaultSession().getStore(urlName);
                storeMap.put(email, store);
            }
            store = storeMap.get(email);
        }
        if(!store.isConnected()){
            store.connect();
        }
        return store;
    }

    /**
     * 以指定mode打开指定folder
     * @param localFolder 要打开的folder
     * @param mode 打开方式 Folder.READ_ONLY ，Folder.READ_WRITE
     * @return 打开的folder
     * @throws MessagingException
     */
    public Folder openFolder(LocalFolder localFolder, int mode) throws MessagingException {
        Store store = getStore(localFolder.getAccount());
        Folder folder = store.getFolder(localFolder.getFullName());
        if(!folder.isOpen() || folder.getMode() != mode){
            folder.open(mode);
        }
        return folder;
    }

    /**
     * 关闭指定的folder
     * @param localFolder 要关闭的folder
     * @throws MessagingException
     */
    public void closeFolder(LocalFolder localFolder) throws MessagingException {
        Store store = getStore(localFolder.getAccount());
        Folder folder = store.getFolder(localFolder.getFullName());
        if(folder.isOpen()){
            folder.close();
        }
    }

    /**
     * 获取缓存的该邮箱的Transport
     * @param account 邮箱账号信息
     * @return Transport对象
     * @throws MessagingException
     */
    protected Transport getTransport(Account account) throws MessagingException {
        String email = account.getEmail();
        String transportUrl = account.getTransportUrl();
        Transport transport;
        synchronized (transportMap){
            if(!transportMap.containsKey(email) || !transportMap.get(email).getURLName().toString().equals(transportUrl)){
                URLName urlName = new URLName(transportUrl);
                transport = MailSession.getDefaultSession().getTransport(urlName);
                transportMap.put(email, transport);
            }
            transport = transportMap.get(email);
        }
        if(!transport.isConnected()){
            transport.connect();
        }
        return transport;
    }

}
