package com.gongw.mailcore.contact;


import org.litepal.LitePal;
import java.util.List;


/**
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

    public List<Contact> getAllAccounts(){
        return LitePal.findAll(Contact.class);
    }

    public Contact getContact(String email, String personalName){
        List<Contact> contacts = LitePal.where("email = ? and personalName = ?", email, personalName)
                .find(Contact.class);
        if(contacts.size() < 1){
            return null;
        }
        return contacts.get(0);
    }

    public Contact getContactById(long id){
        return LitePal.find(Contact.class, id);
    }

    public List<Contact> getContactsByEmail(String email){
        return LitePal.where("email = ?", email)
                .find(Contact.class);
    }

    public void saveOrUpdateContacts(List<Contact> contacts){
        for(Contact contact : contacts){
            saveOrUpdateContact(contact);
        }
    }

    public void saveOrUpdateContact(Contact contact){
        List<Contact> contacts = LitePal.where("email = ? and personalName = ?", contact.getEmail(), contact.getPersonalName())
                .find(Contact.class);
        if(contacts.size() < 1){
            contact.save();
        }else{
            contact.update(contacts.get(0).getId());
            contact.setId(contacts.get(0).getId());
        }
//        contact.saveOrUpdate("email = ? and personalName = ?", contact.getEmail(), contact.getPersonalName());
    }

    public void deleteById(int id){
        LitePal.delete(Contact.class, id);
    }

    public void deleteByEmail(String email){
        LitePal.deleteAll(Contact.class, "email = ?", email);
    }

    public void deleteAll(){
        LitePal.deleteAll(Contact.class);
    }
}
