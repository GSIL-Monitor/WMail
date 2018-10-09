package com.gongw.mailcore.account;

import com.gongw.mailcore.folder.LocalFolder;
import com.gongw.mailcore.message.LocalMessage;
import com.gongw.mailcore.part.LocalPart;

import org.litepal.LitePal;

import java.io.File;
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
    public Account getAccountById(long id){
        return LitePal.find(Account.class, id);
    }

    /**
     * 根据email查询数据库中的Account信息
     * @param email 要查询的Accoutn的email
     * @return 查询到的Account
     */
    public Account getAccountByEmail(String email){
        List<Account> accounts = LitePal.where("email = ?", email)
                .find(Account.class);
        if(accounts.size() > 0){
            return accounts.get(0);
        }
        return null;
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
    public void deleteById(long id){
        Account account =getAccountById(id);
        if(account == null){
            return;
        }
        //获取账号下的文件夹数据
        List<LocalFolder> localFolders = LitePal.where("account_id = ?", String.valueOf(id))
                .find(LocalFolder.class);
        for(LocalFolder localFolder : localFolders){
            //获取文件夹下的邮件数据
            List<LocalMessage> localMessages = LitePal.where("localfolder_id = ?", String.valueOf(localFolder.getId()))
                    .find(LocalMessage.class);
            for(LocalMessage localMessage : localMessages){
                //获取邮件相关的附件和正文等PART数据
                List<LocalPart> localParts = LitePal.where("localmessage_id = ?", String.valueOf(localMessage.getId()))
                        .find(LocalPart.class);
                for(LocalPart localPart : localParts){
                    //删除缓存的附件和正文
                    if(localPart.getDataLocation() == LocalPart.Location.LOCATION_ON_DISK && localPart.getLocalPath() != null){
                        File file = new File(localPart.getLocalPath());
                        if(file.exists()){
                            file.delete();
                        }
                    }
                    //删除part数据
                    localPart.delete();
                }
                //删除邮件数据
                localMessage.delete();
            }
            //删除文件夹数据
            localFolder.delete();
        }
        //删除账号数据
        account.delete();
    }

    /**
     * 根据email删除数据库中的Account
     * @param email 要删除的Account的email
     */
    public void deleteByEmail(String email){
        Account account = getAccountByEmail(email);
        deleteById(account.getId());
    }

    /**
     * 删除数据库中的所有Account
     */
    public void deleteAll(){
        List<Account> accounts = getAllAccounts();
        if(accounts == null || accounts.size() < 1){
            return;
        }
        for(Account account : accounts){
            deleteById(account.getId());
        }
    }
}
