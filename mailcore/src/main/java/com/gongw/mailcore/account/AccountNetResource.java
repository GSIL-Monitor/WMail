package com.gongw.mailcore.account;
import com.gongw.mailcore.net.NetResource;
import javax.mail.MessagingException;
import javax.mail.Store;
import javax.mail.Transport;

/**
 * 网络邮箱账号资源，并提供Account相关的网络操作接口
 * Created by gongw on 2018/9/29.
 */

public class AccountNetResource extends NetResource {

    private static class InstanceHolder{
        private static AccountNetResource instance = new AccountNetResource();
    }

    private AccountNetResource(){
    }

    public static AccountNetResource singleInstance(){
        return InstanceHolder.instance;
    }

    /**
     * 连接邮箱的收发件服务器
     * @param account 邮箱
     * @throws MessagingException
     */
    public void connect(Account account) throws MessagingException {
        getStore(account);
        getTransport(account);
    }


}
