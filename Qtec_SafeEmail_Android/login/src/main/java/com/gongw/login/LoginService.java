package com.gongw.login;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.gongw.router.provider.ILoginProvider;

/**
 * Created by gongw on 2018/7/10.
 */

@Route(path = ILoginProvider.ROUTER_PATH_TO_LOGIN_SERVICE, name = "login")
public class LoginService implements ILoginProvider {

    @Override
    public void init(Context context) {}


    @Override
    public void goToLogin(Activity activity) {
        Intent intent = new Intent(activity, LoginActivity.class);
        activity.startActivity(intent);
    }

}
