package com.gongw.router.provider;

import android.app.Activity;

import com.alibaba.android.arouter.facade.template.IProvider;

/**
 * Created by gongw on 2018/7/10.
 */

public interface ILoginProvider extends IProvider {

    String ROUTER_PATH_TO_LOGIN_SERVICE = "/login/service";

    void goToLogin(Activity activity);

}
