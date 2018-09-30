package com.gongw.mailcore.account;

import org.litepal.LitePal;
import java.util.List;

/**
 * 本地的邮箱账号资源，以数据库的形式保存Account，并提供操作数据库中Account数据的接口
 * Created by gongw on 2018/9/29.
 */

public class AccountLocalResource {

    private static class InstanceHolder{
        private static AccountLocalResource instance = new AccountLocalResource();
    }

    private AccountLocalResource(){
    }

    public static AccountLocalResource singleInstance(){
        return InstanceHolder.instance;
    }

    /**
     * 获取所有数据库中存储的Account信息
     * @return 从数据库中取出的Account集合
     */
    public List<Account> getAllAccounts(){
        return LitePal.findAll(Account.class);
    }

    /**
     * 根据id查询数据库中的Account信息
     * @param id 要查询的Accoutn的id
     * @return 查询到的Account
     */
    public Account getAccountById(int id){
        return LitePal.find(Account.class, id);
    }

    /**
     * 根据email查询数据库中的Account信息
     * @param email 要查询的Accoutn的email
     * @return 查询到的Account集合
     */
    public List<Account> getAccountsByEmail(String email){
        return LitePal.where("email = ?", email)
                .find(Account.class);
    }

    /**
     * 批量保存或更新Account信息到数据库
     * @param accounts 需要保存或更新的Account集合
     */
    public void saveOrUpdateAccounts(List<Account> accounts){
        for(Account account : accounts){
            //TODO:密码存储时需要加密
            saveOrUpdateAccount(account);
        }
    }

    /**
     * 保存或更新Account到数据库，如果email已经存在则为更新操作，否则为保存操作
     * @param account 要保存或更新的Account
     */
    public void saveOrUpdateAccount(Account account){
        List<Account> accounts = LitePal.where("email = ?", account.getEmail())
                .find(Account.class);
        if(accounts.size() < 1){
            account.save();
        }else{
            account.update(accounts.get(0).getId());
        }
    }

    /**
     * 根据id删除数据库中的Account
     * @param id 要删除的Account的id
     */
    public void deleteById(int id){
        LitePal.delete(Account.class, id);
    }

    /**
     * 根据email删除数据库中的Account
     * @param email 要删除的Account的email
     */
    public void deleteByEmail(String email){
        LitePal.deleteAll(Account.class, "email = ?", email);
    }

    /**
     * 删除数据库中的所有Account
     */
    public void deleteAll(){
        LitePal.deleteAll(Account.class);
    }
}
