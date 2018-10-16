package com.qtec.safemail2;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.alibaba.android.arouter.launcher.ARouter;
import com.gongw.router.provider.ILoginProvider;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ILoginProvider loginService = (ILoginProvider) ARouter.getInstance().build(ILoginProvider.ROUTER_PATH_TO_LOGIN_SERVICE).navigation();
        loginService.goToLogin(this);
    }
}
