package com.gongw.mailcore.contact;


import org.litepal.LitePal;
import java.util.List;


/**
 * Contact业务模型，采用单例模式，提供对Contact数据的获取、保存、修改、删除等操作接口
 * Created by gongw on 2018/9/11.
 */

public class ContactModel {

    private static class InstanceHolder{
        private static ContactModel instance = new ContactModel();
    }

    public static ContactModel singleInstance(){
        return InstanceHolder.instance;
    }

    private ContactModel(){}

    /**
     * 获取数据库中的所有Contact
     * @return 获取到的Contact集合
     */
    public List<Contact> getAllContacts(){
        return LitePal.findAll(Contact.class);
    }

    /**
     * 根据邮箱地址和名称获取Contact
     * @param email 邮箱地址
     * @param personalName 名称
     * @return 数据库中符合条件的Contact
     */
    public Contact getContact(String email, String personalName){
        List<Contact> contacts = LitePal.where("email = ? and personalName = ?", email, personalName)
                .find(Contact.class);
        if(contacts.size() < 1){
            return null;
        }
        return contacts.get(0);
    }

    /**
     * 根据id获取Contact
     * @param id id
     * @return id对应的Contact
     */
    public Contact getContactById(long id){
        return LitePal.find(Contact.class, id);
    }

    /**
     * 根据邮箱地址获取Contact
     * @param email 邮箱地址
     * @return 符合条件的Contact
     */
    public List<Contact> getContactsByEmail(String email){
        return LitePal.where("email = ?", email)
                .find(Contact.class);
    }

    /**
     * 批量保存或修改Contact到数据库
     * @param contacts 要保存或修改的Contact集合
     */
    public void saveOrUpdateContacts(List<Contact> contacts){
        for(Contact contact : contacts){
            saveOrUpdateContact(contact);
        }
    }

    /**
     * 保存或修改Contact
     * 根据email和personalName是否符合来判断是保存操作还是修改操作
     * @param contact 要保存或修改的contact
     */
    public void saveOrUpdateContact(Contact contact){
        List<Contact> contacts = LitePal.where("email = ? and personalName = ?", contact.getEmail(), contact.getPersonalName())
                .find(Contact.class);
        if(contacts.size() < 1){
            contact.save();
        }else{
            contact.update(contacts.get(0).getId());
            contact.setId(contacts.get(0).getId());
        }
    }

    /**
     * 根据id删除Contact
     * @param id 要删除的Contact的id
     */
    public void deleteById(int id){
        LitePal.delete(Contact.class, id);
    }

    /**
     * 根据email删除Contact
     * @param email 要删除的Contact的email
     */
    public void deleteByEmail(String email){
        LitePal.deleteAll(Contact.class, "email = ?", email);
    }

    /**
     * 删除数据库中所有的Contact
     */
    public void deleteAll(){
        LitePal.deleteAll(Contact.class);
    }
}
