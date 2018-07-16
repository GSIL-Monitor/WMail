package com.gongw.login;

import com.gongw.common.base.BaseActivity;
import com.gongw.login.databinding.ActivityLoginBinding;
import com.gongw.login.view.MailTypeFragment;

/**
 * Created by gongw on 2018/7/10.
 */

public class LoginActivity extends BaseActivity<ActivityLoginBinding> {

    @Override
    public int getContentViewLayout() {
        return R.layout.activity_login;
    }

    @Override
    public void init() {
        addFragment(binding.flContainer.getId(), new MailTypeFragment(), false);
    }

}
