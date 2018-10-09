package com.gongw.mailcore;

import android.content.Context;
import org.litepal.LitePal;

/**
 * Created by gongw on 2018/9/29.
 */

public class Mail {

    private static Context appContext;

    public static void init(Context context){
        appContext = context.getApplicationContext();
        LitePal.initialize(appContext);
    }

    public static Context getAppContext(){
        return appContext;
    }

}
