package com.gongw.mailcore.account;

import java.util.List;

import javax.mail.MessagingException;

/**
 * Account业务模型，提供对Account数据的获取、保存、修改、删除等操作接口
 * Created by gongw on 2018/9/11.
 */

public class AccountModel {
    /**
     * 邮箱账号数据库资源对象，提供操作本地邮箱账号资源的接口
     */
    private AccountLocalResource localAccountResource;
    /**
     * 邮箱账号网络资源对象，提供邮箱账号的网络操作接口
     */
    private AccountNetResource netAccountResource;

    private static class InstanceHolder{
        private static AccountModel instance = new AccountModel();
    }

    public static AccountModel singleInstance(){
        return InstanceHolder.instance;
    }


    private AccountModel(){
        localAccountResource = AccountLocalResource.singleInstance();
        netAccountResource = AccountNetResource.singleInstance();
    }

    /**
     * 连接邮箱服务器
     * @param account 邮箱账号
     * @throws MessagingException
     */
    public void connect(Account account) throws MessagingException {
        netAccountResource.connect(account);
    }

    /**
     * 获取所有数据库中存储的Account信息
     * @return 从数据库中取出的Account集合
     */
    public List<Account> getAllAccounts(){
        return localAccountResource.getAllAccounts();
    }

    /**
     * 根据id查询数据库中的Account信息
     * @param id 要查询的Accoutn的id
     * @return 查询到的Account
     */
    public Account getAccountById(int id){
        return localAccountResource.getAccountById(id);
    }

    /**
     * 保存或更新Account到数据库，如果email已经存在则为更新操作，否则为保存操作
     * @param account 要保存或更新的Account
     */
    public void saveOrUpdateAccount(Account account){
        localAccountResource.saveOrUpdateAccount(account);
    }

    /**
     * 根据email查询数据库中的Account信息
     * @param email 要查询的Accoutn的email
     * @return 查询到的Account集合
     */
    public List<Account> getAccountsByEmail(String email){
        return localAccountResource.getAccountsByEmail(email);
    }

    /**
     * 批量保存或更新Account信息到数据库
     * @param accounts 需要保存或更新的Account集合
     */
    public void saveOrUpdateAccounts(List<Account> accounts){
        localAccountResource.saveOrUpdateAccounts(accounts);
    }

    /**
     * 根据id删除数据库中的Account
     * @param id 要删除的Account的id
     */
    public void deleteById(int id){
        localAccountResource.deleteById(id);
    }

    /**
     * 根据email删除数据库中的Account
     * @param email 要删除的Account的email
     */
    public void deleteByEmail(String email){
        localAccountResource.deleteByEmail(email);
    }

    /**
     * 删除数据库中的所有Account
     */
    public void deleteAll(){
       localAccountResource.deleteAll();
    }
}
