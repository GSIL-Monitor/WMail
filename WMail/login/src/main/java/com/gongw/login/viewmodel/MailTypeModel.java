package com.gongw.login.viewmodel;


import com.gongw.common.base.BaseApplication;
import com.gongw.login.R;
import com.gongw.login.model.MailType;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by gongw on 2018/7/11.
 */

public class MailTypeModel {

    private static class InstanceHolder {
        static final MailTypeModel INSTANCE = new MailTypeModel();
    }

    private MailTypeModel(){}

    public static MailTypeModel getInstance(){
        return InstanceHolder.INSTANCE;
    }

    public List<MailType> getDatas(){
        List<MailType> datas = new ArrayList<>();
        String[] types = BaseApplication.getContext().getResources().getStringArray(R.array.mail_type);

        for(String type : types){
            String[] mailType = type.split("=");
            datas.add(new MailType(mailType[0], mailType[1]));
        }

        return datas;
    }
}
