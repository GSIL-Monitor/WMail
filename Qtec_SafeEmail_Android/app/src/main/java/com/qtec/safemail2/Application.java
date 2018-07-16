package com.qtec.safemail2;

import com.alibaba.android.arouter.launcher.ARouter;
import com.gongw.common.base.BaseApplication;

/**
 * Created by gongw on 2018/7/10.
 */

public class Application extends BaseApplication{

    @Override
    public void onCreate() {
        super.onCreate();

        if (BuildConfig.DEBUG) {
            //开启InstantRun之后，一定要在ARouter.init之前调用openDebug
            ARouter.openDebug();
            ARouter.openLog();
        }
        ARouter.init(this);
    }
}
