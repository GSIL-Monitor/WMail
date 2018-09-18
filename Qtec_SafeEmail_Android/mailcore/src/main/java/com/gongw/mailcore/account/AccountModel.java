package com.gongw.mailcore.account;

import org.litepal.LitePal;

import java.util.List;

/**
 * Created by gongw on 2018/9/11.
 */

public class AccountModel {

    private static class InstanceHolder{
        private static AccountModel instance = new AccountModel();
    }

    public static AccountModel singleInstance(){
        return InstanceHolder.instance;
    }

    private AccountModel(){}

    public List<Account> getAllAccounts(){
        return LitePal.findAll(Account.class);
    }

    public Account getAccountsById(int id){
        return LitePal.find(Account.class, id);
    }

    public List<Account> getAccountsByEmail(String email){
        return LitePal.where("email = ?", email)
                .find(Account.class);
    }

    public void saveOrUpdateAccounts(List<Account> accounts){
        for(Account account : accounts){
            //TODO:密码存储时需要加密
            saveOrUpdateAccount(account);
        }
    }

    public void saveOrUpdateAccount(Account account){
        account.saveOrUpdate("email = ?", account.getEmail());
    }

    public void deleteById(int id){
        LitePal.delete(Account.class, id);
    }

    public void deleteByEmail(String email){
        LitePal.deleteAll(Account.class, "email = ?", email);
    }

    public void deleteAll(){
        LitePal.deleteAll(Account.class);
    }
}
